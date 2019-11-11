/*
 * Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.jetbrains.r.skeleton.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import com.intellij.util.IncorrectOperationException
import org.jetbrains.annotations.TestOnly
import org.jetbrains.r.console.RConsoleManager
import org.jetbrains.r.console.RConsoleView
import org.jetbrains.r.interpreter.RInterpreterManager
import org.jetbrains.r.packages.RSkeletonUtil
import org.jetbrains.r.psi.RElementFactory
import org.jetbrains.r.psi.RPomTarget
import org.jetbrains.r.psi.api.RAssignmentStatement
import org.jetbrains.r.psi.api.RExpression
import org.jetbrains.r.psi.api.RFunctionExpression
import org.jetbrains.r.psi.stubs.RAssignmentStub
import org.jetbrains.r.rinterop.RRef
import org.jetbrains.r.rinterop.RVar

class RSkeletonAssignmentStatement(private val myStub: RSkeletonAssignmentStub) : RSkeletonBase(), RAssignmentStatement {
  override fun getMirror() = null

  override fun getParent(): PsiElement = myStub.getParentStub().getPsi()

  override fun setName(name: String): PsiElement {
    throw IncorrectOperationException("Operation not supported in: $javaClass")
  }

  override fun getStub(): RAssignmentStub = myStub

  override fun getElementType(): IStubElementType<out StubElement<*>, *> = stub.stubType

  override fun isLeft(): Boolean = true

  override fun isRight(): Boolean = !isLeft

  override fun isEqual(): Boolean = false

  override fun isClosureAssignment(): Boolean = false

  override fun getAssignedValue(): RExpression? = null

  override fun getAssignee(): RExpression? = null

  override fun getName(): String = myStub.name

  override fun getNameIdentifier(): PsiNamedElement? = null

  override fun isFunctionDeclaration(): Boolean = myStub.isFunctionDeclaration

  override fun getFunctionParameters(): String = if (this.isFunctionDeclaration()) "(" + myStub.parameters + ")" else ""

  private val parameterNameListValue: List<String> by lazy l@{
    if (!isFunctionDeclaration()) return@l emptyList<String>()
    return@l (RElementFactory.createRPsiElementFromText(project, "function " + functionParameters) as? RFunctionExpression)
      ?.parameterList?.parameterList?.map{ it.name } ?: emptyList<String>()
  }

  override fun getParameterNameList(): List<String> {
    return parameterNameListValue
  }

  override fun canNavigate(): Boolean {
    return super.canNavigate() && RInterpreterManager.getInstance(project).interpreter != null
  }

  override fun getText(): String {
    return name + functionParameters
  }

  override fun navigate(requestFocus: Boolean) {
    RConsoleManager.getInstance(project).currentConsoleAsync.onError {
      throw IllegalStateException("not console available")
    }.onSuccess { consoleView ->
      RPomTarget.createPsiElementByRValue(createRVar(consoleView)).navigate(requestFocus)
    }
  }

  @TestOnly
  internal fun createRVar(consoleView: RConsoleView): RVar {
    val nameWithoutExtension = containingFile.virtualFile.nameWithoutExtension
    val (packageName, _) = RSkeletonUtil.parsePackageAndVersionFromSkeletonFilename(nameWithoutExtension)
                           ?: throw IllegalStateException("bad filename")
    val expressionRef = RRef.expressionRef("$packageName::`${getName()}`", consoleView.rInterop)
    return RVar(name, expressionRef, expressionRef.getValueInfo())
  }
}