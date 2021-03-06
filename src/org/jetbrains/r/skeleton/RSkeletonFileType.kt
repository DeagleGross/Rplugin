/*
 * Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.jetbrains.r.skeleton

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.r.RBundle
import javax.swing.Icon

object RSkeletonFileType : FileType {
  const val EXTENSION = "RPluginSkeletonFile"

  override fun getDefaultExtension(): String = EXTENSION

  override fun getIcon(): Icon? = null

  override fun getName(): String = EXTENSION

  override fun getDescription(): String = RBundle.message("r.skeleton.file.description.binary.skeleton.format.for.r.package")

  override fun isBinary(): Boolean = true

  override fun isReadOnly(): Boolean = true
}
