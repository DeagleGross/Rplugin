/*
 * Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.jetbrains.r

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.extensions.PluginDescriptor
import com.intellij.util.io.exists
import org.jetbrains.r.settings.RInterpreterSettings
import java.io.File
import java.nio.file.Paths

object RPluginUtil {
  const val PLUGIN_ID = "R4Intellij"
  private const val PLUGIN_NAME = "rplugin"

  fun getPlugin(): PluginDescriptor = PluginManagerCore.getPluginDescriptorOrPlatformByClassName(RInterpreterSettings::class.java.name)!!

  fun findFileInRHelpers(relativePath: String): File =
    Paths.get(helpersPath, relativePath).toFile()

  val helpersPath: String
    get() = helperPathOrNull ?: throw IllegalStateException("Cannot find rplugin directory")

  val helperPathOrNull: String?
    get() = Paths.get(PathManager.getPluginsPath(), PLUGIN_NAME).takeIf { it.exists() }?.toString() ?:
            Paths.get(PathManager.getPreInstalledPluginsPath(), PLUGIN_NAME).takeIf { it.exists() }?.toString()
}
