// Copyright (c) 2017, Holger Brandl, Ekaterina Tuzova
/*
 * Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.jetbrains.r.inspections

import com.google.common.base.Joiner
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.ResolveResult
import icons.org.jetbrains.r.RBundle
import org.jetbrains.annotations.Nls
import org.jetbrains.r.psi.api.RCallExpression
import org.jetbrains.r.psi.api.ROperator
import org.jetbrains.r.psi.api.RPsiElement
import org.jetbrains.r.psi.api.RVisitor
import org.jetbrains.r.psi.references.RReferenceBase

class UnresolvedReferenceInspection : RInspection() {

  @Nls
  override fun getDisplayName(): String {
    return RBundle.message("unresolved.reference.inspection.name")
  }

  override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession): PsiElementVisitor {
    return ReferenceVisitor(holder)
  }

  private inner class ReferenceVisitor internal constructor(private val myProblemHolder: ProblemsHolder) : RVisitor() {

    override fun visitOperator(element: ROperator) {
      if (!element.text.startsWith("%")) return

      val targets = element.reference.multiResolve(false)
      handleResolveResult(element, targets)
    }

    override fun visitCallExpression(element: RCallExpression) {
      // resolve normally
      val reference = element.expression.reference

      if (reference is RReferenceBase<*>) {
        val targets = reference.multiResolve(false)
        handleResolveResult(element.expression, targets)
      }
    }

    private fun handleResolveResult(element: RPsiElement, targets: Array<in ResolveResult>) {
      if (targets.isEmpty()) {
        myProblemHolder.registerProblem(element, UNRESOLVED_MSG, ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
      }
    }
  }

  companion object {

    var UNRESOLVED_MSG = RBundle.message("unresolved.reference.inspection.description")

    fun missingImportMsg(symbol: String, foundIn: List<String>): String {
      return RBundle.message("unresolved.reference.inspection.missing.msg", symbol, Joiner.on(", ").join(foundIn))
    }
  }
}
