package org.jetbrains.r.codeInsight.findUsages

import com.intellij.codeInsight.TargetElementEvaluatorEx2
import com.intellij.psi.PsiElement
import org.jetbrains.r.psi.api.RAssignmentStatement
import org.jetbrains.r.psi.api.RNamedArgument
import org.jetbrains.r.psi.api.RParameter

class RTargetElementEvaluator: TargetElementEvaluatorEx2() {
  override fun isAcceptableNamedParent(parent: PsiElement): Boolean {
    val grandParent = parent.parent
    if (grandParent is RAssignmentStatement) {
      return grandParent.assignee == parent
    }

    return when (grandParent) {
      is RParameter, is RNamedArgument -> true
      else -> false
    }
  }
}