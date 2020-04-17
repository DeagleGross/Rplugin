/*
 * Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.jetbrains.r.settings

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import org.intellij.datavis.r.inlays.components.CHANGE_DARK_MODE_TOPIC
import org.jetbrains.r.run.graphics.RGraphicsUtils
import java.awt.Dimension

@State(name = "RGraphicsSettings", storages = [Storage("rGraphicsSettings.xml")])
class RGraphicsSettings : SimplePersistentStateComponent<RGraphicsSettingsState>(RGraphicsSettingsState()) {
  companion object {
    private const val CURRENT_VERSION = 1

    fun getInstance(project: Project) = project.service<RGraphicsSettings>()

    fun getScreenParameters(project: Project): RGraphicsUtils.ScreenParameters {
      return with(getInstance(project).state) {
        if (width == 0 || height == 0 || resolution == 0 || version != CURRENT_VERSION) {
          RGraphicsUtils.getDefaultScreenParameters(false).also {
            setScreenParameters(project, it)
          }
        } else {
          RGraphicsUtils.ScreenParameters(Dimension(width, height), resolution)
        }
      }
    }

    fun setScreenDimension(project: Project, dimension: Dimension) {
      setScreenParameters(project, dimension, null)
    }

    fun setScreenParameters(project: Project, parameters: RGraphicsUtils.ScreenParameters) {
      setScreenParameters(project, parameters.dimension, parameters.resolution ?: 0)
    }

    fun isDarkModeEnabled(project: Project): Boolean =
      getInstance(project).state.darkMode

    fun setDarkMode(project: Project, isEnabled: Boolean) {
      getInstance(project).state.darkMode = isEnabled
      project.messageBus.syncPublisher(CHANGE_DARK_MODE_TOPIC).onDarkModeChanged(isEnabled)
    }

    fun getImageNumber(project: Project): Int {
      return getInstance(project).state.imageNumber
    }

    fun setImageNumber(project: Project, number: Int) {
      getInstance(project).state.imageNumber = number
    }

    fun getOutputDirectory(project: Project): String? {
      return getInstance(project).state.outputDirectory
    }

    fun setOutputDirectory(project: Project, directory: String?) {
      getInstance(project).state.outputDirectory = directory
    }
    
    private fun setScreenParameters(project: Project, dimension: Dimension, resolutionMaybe: Int?) {
      getInstance(project).state.apply {
        width = dimension.width
        height = dimension.height
        if (resolutionMaybe != null) {
          resolution = resolutionMaybe
        }
        version = CURRENT_VERSION
      }
    }
  }
}

class RGraphicsSettingsState : BaseState() {
  var width: Int by property(0)
  var height: Int by property(0)
  var resolution: Int by property(0)
  var version: Int by property(0)
  var imageNumber: Int by property(0)
  var outputDirectory: String? by string()
  var darkMode: Boolean by property(true)
}
