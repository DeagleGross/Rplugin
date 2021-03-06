/*
 * Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.jetbrains.r.statistics

import com.intellij.internal.statistic.eventLog.FeatureUsageData
import com.intellij.internal.statistic.service.fus.collectors.FUCounterUsageLogger
import com.intellij.openapi.project.Project
import org.jetbrains.r.interpreter.*
import org.jetbrains.r.rinterop.RCondaUtil
import java.io.File

enum class RStatisticsEvent(val id: String) {
  SETUP_INTERPRETER("setup.interpreter"),
  CALL_METHOD_FROM_CONSOLE("call.method.from.console")
}

object RStatistics {
  private const val INTERPRETERS_ID = "r.interpreters"
  private const val WORKFLOW_ID = "r.workflow"

  private fun logEvent(project: Project?,
                       groupId: String,
                       eventId: RStatisticsEvent,
                       dataInitializer: FeatureUsageData.() -> Unit = { }) {
    val data = FeatureUsageData()
    dataInitializer(data)
    FUCounterUsageLogger.getInstance().logEvent(project, groupId, eventId.id, data)
  }

  fun logConsoleMethodCall(project: Project?, name: String) {
    logEvent(project, WORKFLOW_ID, RStatisticsEvent.CALL_METHOD_FROM_CONSOLE) {
      addData("name", name)
    }
  }

  fun logSetupInterpreter(project: Project?, interpreter: RInterpreter) {
    val suggestedInterpreters = collectFoundInterpreters(interpreter)
    val isConda = isConda(interpreter.interpreterLocation)
    logEvent(project, INTERPRETERS_ID, RStatisticsEvent.SETUP_INTERPRETER) {
      addData("version", interpreter.version.toCompactString())
      addData("is.conda", isConda)
      addData("suggested", suggestedInterpreters)
    }
  }

  private fun isConda(interpreterLocation: RInterpreterLocation): Boolean {
    return RCondaUtil.findCondaByRInterpreter(File(interpreterLocation.toLocalPathOrNull() ?: return false)) != null
  }

  private fun collectFoundInterpreters(selected: RInterpreter): List<String> {
    val allInterpreters = RInterpreterUtil.suggestAllInterpreters(false)
    val selectedInfo: RInterpreterInfo? = allInterpreters.find { it.interpreterLocation == selected.interpreterLocation }
    return if (selectedInfo == null) {
      // WTF actually??? This means we report information about interpreter and it is missing in all available interpreter list
      allInterpreters
    }
    else {
      // Place current interpreter to the beginning
      val others = allInterpreters - selectedInfo
      (listOf(selectedInfo) + others)
    }.map { infoToString(it) }
  }

  private fun infoToString(info: RInterpreterInfo): String =
    """${info.version.toCompactString()}_${isConda(info.interpreterLocation)}"""
}