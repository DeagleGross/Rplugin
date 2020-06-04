/*
 * Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.jetbrains.r.interpreter

import com.intellij.ide.browsers.BrowserLauncher
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.progress.runBackgroundableTask
import com.intellij.openapi.project.DumbModeTask
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.impl.PsiDocumentManagerImpl
import com.intellij.psi.stubs.StubUpdatingIndex
import com.intellij.util.indexing.FileBasedIndex
import org.jetbrains.concurrency.AsyncPromise
import org.jetbrains.concurrency.Promise
import org.jetbrains.concurrency.runAsync
import org.jetbrains.r.RBundle
import org.jetbrains.r.RPluginUtil
import org.jetbrains.r.configuration.RSettingsProjectConfigurable
import org.jetbrains.r.notifications.RNotificationUtil
import org.jetbrains.r.packages.RSkeletonUtil
import org.jetbrains.r.settings.RInterpreterSettings
import org.jetbrains.r.settings.RSettings
import org.jetbrains.r.statistics.RStatistics

interface RInterpreterManager {
  val interpreter: RInterpreter?
  val interpreterPath: String
  val interpreterPathValidatedPromise: Promise<Unit>

  /**
   *  true if skeletons update was performed at least once.
   */
  val isSkeletonInitialized: Boolean

  fun hasInterpreter(): Boolean {
    return interpreter != null
  }

  fun initializeInterpreter(force: Boolean = false): Promise<Unit>

  companion object {
    fun getInstance(project: Project): RInterpreterManager = project.getService(RInterpreterManager::class.java)
    fun getInstanceIfCreated(project: Project): RInterpreterManager? = project.getServiceIfCreated(RInterpreterManager::class.java)

    fun getInterpreter(project: Project): RInterpreter? = getInstance(project).interpreter
  }
}

class RInterpreterManagerImpl(private val project: Project): RInterpreterManager {
  @Volatile
  override var isSkeletonInitialized: Boolean = false
    private set

  @Volatile
  override var interpreterPath: String = fetchInterpreterPath()
    private set
  override var interpreterPathValidatedPromise: Promise<Unit> = checkInterpreterPath(interpreterPath).then { if (!it) interpreterPath = "" }
  private var asyncPromise = AsyncPromise<Unit>()
  private var initialized = false
  private var rInterpreter: RInterpreterImpl? = null
  private var firstOpenedFile = true

  init {
    if (!ApplicationManager.getApplication().isUnitTestMode) {
      invalidateRSkeletonCaches()
    }
  }


  private fun fetchInterpreterPath(): String {
    return if (ApplicationManager.getApplication().isUnitTestMode) {
      RInterpreterUtil.suggestHomePath()
    } else {
      RSettings.getInstance(project).interpreterPath
    }
  }

  private fun checkInterpreterPath(path: String): Promise<Boolean> = runAsync {
    val (isViable, e) = try {
      Pair(path.isNotBlank() && RInterpreterUtil.getVersionByPath(path) != null, null)
    } catch (e: Exception) {
      Pair(false, e)
    }
    if (!isViable) {
      val message = createInvalidPathErrorMessage(path, e?.message)
      val settingsAction = RNotificationUtil.createNotificationAction(GO_TO_SETTINGS_HINT) {
        ShowSettingsUtil.getInstance().showSettingsDialog(project, RSettingsProjectConfigurable::class.java)
      }
      val downloadAction = RNotificationUtil.createNotificationAction(DOWNLOAD_R_HINT) {
        openDownloadRPage()
      }
      RNotificationUtil.notifyInterpreterError(project, message, settingsAction, downloadAction)
    }
    isViable
  }

  private fun ensureActiveInterpreterStored() {
    rInterpreter?.let { suggested ->
      val interpreter = RBasicInterpreterInfo(SUGGESTED_INTERPRETER_NAME, suggested.interpreterPath, suggested.version)
      RInterpreterSettings.addOrEnableInterpreter(interpreter)
    }
  }

  override fun initializeInterpreter(force: Boolean): Promise<Unit> {
    if (initialized && !force) {
      return asyncPromise
    }
    synchronized(this) {
      if (initialized && !force) {
        return asyncPromise
      }
      if (force) {
        rInterpreter = null
        asyncPromise = AsyncPromise()
        val oldInterpreterPath = interpreterPath
        interpreterPath = fetchInterpreterPath()
        interpreterPathValidatedPromise = checkInterpreterPath(interpreterPath).then { if (!it) interpreterPath = oldInterpreterPath }
      }
      if (!initialized) {
        RLibraryWatcher.subscribe(project, RLibraryWatcher.TimeSlot.FIRST) {
          scheduleSkeletonUpdate()
        }
      }
      initialized = true
      setupInterpreter(asyncPromise)
      return asyncPromise
    }
  }

  override val interpreter: RInterpreter?
    get() = rInterpreter

  private fun setupInterpreter(promise: AsyncPromise<Unit>) {
    runBackgroundableTask("Initializing R interpreter", project) {
      interpreterPathValidatedPromise.blockingGet(Int.MAX_VALUE)!!
      if (interpreterPath == "") {
        promise.setError("Cannot initialize interpreter")
        return@runBackgroundableTask
      }
      val versionInfo = RInterpreterImpl.loadInterpreterVersionInfo(interpreterPath, project.basePath!!)
      RInterpreterImpl(versionInfo, interpreterPath, project).let {
        rInterpreter = it
        ensureActiveInterpreterStored()
        scheduleSkeletonUpdate()
        promise.setResult(Unit)
        RStatistics.logSetupInterpreter(it)
      }
    }
  }

  private fun scheduleSkeletonUpdate(): Promise<Unit> {
    return AsyncPromise<Unit>().also { promise ->
      val interpreter = rInterpreter
      if (interpreter != null) {
        interpreter.updateState()
          .onProcessed { promise.setResult(Unit) }
          .onSuccess {
            val updater = object : Task.Backgroundable(project, "Update skeletons", false) {
              override fun run(indicator: ProgressIndicator) {
                RLibraryWatcher.getInstance(project).registerRootsToWatch(interpreter.libraryPaths)
                RLibraryWatcher.getInstance(project).refresh()
                updateSkeletons(interpreter)
              }
            }
            ProgressManager.getInstance().run(updater)
          }
      } else {
        promise.setResult(Unit)
      }
    }
  }

  private fun updateSkeletons(interpreter: RInterpreterImpl) {
    val dumbModeTask = object : DumbModeTask(interpreter) {
      override fun performInDumbMode(indicator: ProgressIndicator) {
        if (!project.isOpen || project.isDisposed) return
        if (RSkeletonUtil.updateSkeletons(interpreter, project, indicator)) {
          runInEdt { runWriteAction { refreshSkeletons(interpreter) } }
        }
        isSkeletonInitialized = true
        RInterpreterUtil.updateIndexableSet(project)
      }
    }
    DumbService.getInstance(project).queueTask(dumbModeTask)
  }

  private fun refreshSkeletons(interpreter: RInterpreterImpl) {
    if (!project.isOpen || project.isDisposed) return
    interpreter.skeletonPaths.forEach { skeletonPath ->
      val libraryRoot = LocalFileSystem.getInstance().refreshAndFindFileByPath(skeletonPath) ?: return@forEach
      VfsUtil.markDirtyAndRefresh(false, true, true, libraryRoot)
      WriteAction.runAndWait<Exception> { PsiDocumentManagerImpl.getInstance(project).commitAllDocuments() }
    }
  }

  companion object {
    private const val DOWNLOAD_R_PAGE = "https://cloud.r-project.org/"

    private val SUGGESTED_INTERPRETER_NAME = RBundle.message("project.settings.suggested.interpreter")
    private val GO_TO_SETTINGS_HINT = RBundle.message("interpreter.manager.go.to.settings.hint")
    private val DOWNLOAD_R_HINT = RBundle.message("interpreter.manager.download.r.hint")

    private fun createInvalidPathErrorMessage(path: String, details: String?): String {
      val additional = details?.let { ":\n$it" }
      return RBundle.message("interpreter.manager.invalid.path", path, additional ?: "")
    }

    fun openDownloadRPage() {
      BrowserLauncher.instance.browse(DOWNLOAD_R_PAGE)
    }
  }
}

/**
 * This method invalidate Stub Index on first start of R plugin
 * We need to do that to workaround IntelliJ 2020.1 platform bug when
 * skeleton files are not re-indexed on plugin update.
 */
private fun invalidateRSkeletonCaches() {
  val key = "rplugin.version"
  val version = RPluginUtil.getPlugin().version
  val propertiesComponent = PropertiesComponent.getInstance()
  val lastVersion = propertiesComponent.getValue(key)
  if (version != lastVersion) {
    propertiesComponent.setValue(key, version)
    FileBasedIndex.getInstance().requestRebuild(StubUpdatingIndex.INDEX_ID)
  }
}

