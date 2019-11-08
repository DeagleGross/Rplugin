/*
 * Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package icons.org.jetbrains.r.notifications

import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import icons.org.jetbrains.r.RBundle

object RNotificationUtil {
  private val UNKNOWN_ERROR_MESSAGE = RBundle.message("notification.unknown.error.message")
  private const val CONSOLE_GROUP_ID = "RConsole"
  private val CONSOLE_FAILURE_TITLE = RBundle.message("notification.console.failure")

  private const val INTERPRETER_GROUP_ID = "RInterpreter"
  private val INTERPRETER_FAILURE_TITLE = RBundle.message("notification.interpreter.failure")

  private const val GRAPHICS_GROUP_ID = "RGraphics"
  private val GRAPHICS_FAILURE_TITLE = RBundle.message("notification.graphics.failure")

  private const val EXECUTION_GROUP_ID = "Console Execution"
  private val EXECUTION_FAILURE_TITLE = RBundle.message("notification.execution.failure")

  fun notifyConsoleError(project: Project, message: String?, action: AnAction? = null) {
    notifyError(project, CONSOLE_GROUP_ID, CONSOLE_FAILURE_TITLE, message, action)
  }

  fun notifyInterpreterError(project: Project, message: String?, action: AnAction? = null) {
    notifyError(project, INTERPRETER_GROUP_ID, INTERPRETER_FAILURE_TITLE, message, action)
  }

  fun notifyGraphicsError(project: Project, message: String?, action: AnAction? = null) {
    notifyError(project, GRAPHICS_GROUP_ID, GRAPHICS_FAILURE_TITLE, message, action)
  }

  fun notifyExecutionError(project: Project, message: String?, action: AnAction? = null) {
    notifyError(project, EXECUTION_GROUP_ID, EXECUTION_FAILURE_TITLE, message, action)
  }

  private fun notifyError(project: Project, groupDisplayId: String, title: String, message: String?, action: AnAction? = null) {
    val notification = Notification(groupDisplayId, title, message ?: UNKNOWN_ERROR_MESSAGE, NotificationType.ERROR)
    if (action != null) {
      notification.addAction(action)
    }
    notification.notify(project)
  }

  fun createNotificationAction(text: String, onClick: () -> Unit): AnAction {
    return object : NotificationAction(text) {
      override fun actionPerformed(e: AnActionEvent, notification: Notification) {
        notification.expire()
        onClick()
      }
    }
  }
}
