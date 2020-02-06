/*
 * Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.jetbrains.r.interpreter

import com.intellij.execution.process.ProcessOutput
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import org.jetbrains.concurrency.Promise
import org.jetbrains.r.common.ExpiringList
import org.jetbrains.r.packages.RInstalledPackage
import org.jetbrains.r.rinterop.RInterop

interface RInterpreter : RInterpreterInfo {
  val isUpdating: Boolean

  val installedPackages: ExpiringList<RInstalledPackage>

  val libraryPaths: List<VirtualFile>

  val userLibraryPath: String

  val interop: RInterop

  fun getPackageByName(name: String): RInstalledPackage?

  fun getLibraryPathByName(name: String): VirtualFile?

  fun getProcessOutput(scriptText: String): ProcessOutput?

  /**
   * @return a system-dependant paths to the skeleton roots
   */
  val skeletonPaths: List<String>

  val skeletonRoots: Set<VirtualFile>

  /** A place where all skeleton-related data will be stored */
  val skeletonsDirectory: String

  fun runCommand(cmd: String): String? {
    return getProcessOutput(cmd)?.stdout
  }

  fun getSkeletonFileByPackageName(name: String): PsiFile?

  fun updateState(): Promise<Unit>

  fun findLibraryPathBySkeletonPath(skeletonPath: String): String?
}
