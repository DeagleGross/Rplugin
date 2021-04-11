/*
 * Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.jetbrains.r.classes.common.context

import org.jetbrains.r.hints.parameterInfo.RArgumentInfo
import org.jetbrains.r.psi.api.RCallExpression
import org.jetbrains.r.psi.api.RPsiElement

interface ILibraryClassContext {
  val functionName: String
  val functionCall: RCallExpression
  val argumentInfo: RArgumentInfo
  val originalElement: RPsiElement
}