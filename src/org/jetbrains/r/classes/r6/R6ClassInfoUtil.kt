/*
 * Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.jetbrains.r.classes.r6

import com.intellij.openapi.util.Key
import org.jetbrains.r.hints.parameterInfo.RArgumentInfo
import org.jetbrains.r.hints.parameterInfo.RParameterInfoUtil
import org.jetbrains.r.packages.RPackageProjectManager
import org.jetbrains.r.psi.RElementFactory
import org.jetbrains.r.psi.api.*
import org.jetbrains.r.psi.impl.RCallExpressionImpl
import org.jetbrains.r.psi.impl.RMemberExpressionImpl
import org.jetbrains.r.psi.isFunctionFromLibrarySoft
import java.util.*

object R6ClassInfoUtil {
  const val R6PackageName = "R6"
  const val R6CreateClassMethod = "R6Class"

  const val R6ClassThisKeyword = "self"
  const val functionNew = "new"

  const val argumentClassName = "classname"
  const val argumentSuperClass = "inherit"
  const val argumentPublic = "public"
  const val argumentPrivate = "private"
  const val argumentActive = "active"

  private val INSTANTIATE_CLASS_DEFINITION_KEY: Key<RAssignmentStatement> = Key.create("R6_INSTANTIATE_CLASS_DEFINITION")

  private val INSTANTIATE_CLASS_DEFINITION =
    """R6Class <- function (classname = NULL, public = list(), private = NULL,
                            active = NULL, inherit = NULL, lock_objects = TRUE, class = TRUE,
                            portable = TRUE, lock_class = FALSE, cloneable = TRUE,
                            parent_env = parent.frame(), lock) {}""".trimIndent()

  fun getAssociatedClassNameFromInstantiationCall(call: RCallExpression) : String? {
    val callExpression = call.expression as? RMemberExpressionImpl ?: return null
    if (callExpression.rightExpr?.text != functionNew) return null
    return callExpression.leftExpr?.text
  }

  fun getAssociatedClassName(callExpression: RCallExpression,
                             argumentInfo: RArgumentInfo? = RParameterInfoUtil.getArgumentInfo(callExpression)): String? {
    argumentInfo ?: return null
    if (!callExpression.isFunctionFromLibrarySoft(R6CreateClassMethod, R6PackageName)) return null
    val arg = argumentInfo.getArgumentPassedToParameter(argumentClassName) as? RStringLiteralExpression
    return arg?.name
  }

  fun getAssociatedSuperClassName(callExpression: RCallExpression,
                                  argumentInfo: RArgumentInfo? = RParameterInfoUtil.getArgumentInfo(callExpression)): String? {
    argumentInfo ?: return null
    if (!callExpression.isFunctionFromLibrarySoft(R6CreateClassMethod, R6PackageName)) return null
    return (argumentInfo.getArgumentPassedToParameter(argumentSuperClass) as? RIdentifierExpression)?.name
  }

  fun getAllClassMembers(callExpression: RCallExpression,
                         argumentInfo: RArgumentInfo? = RParameterInfoUtil.getArgumentInfo(callExpression),
                         onlyPublic: Boolean = false) : MutableList<R6ClassMember> {
    val r6ClassMembers = mutableListOf<R6ClassMember>()

    val fields = getAssociatedFields(callExpression, argumentInfo, onlyPublic)
    if (!fields.isNullOrEmpty()) r6ClassMembers.addAll(fields)

    val functions = getAssociatedMethods(callExpression, argumentInfo, onlyPublic)
    if (!functions.isNullOrEmpty()) r6ClassMembers.addAll(functions)

    val activeBindings = getAssociatedActiveBindings(callExpression, argumentInfo)
    if (!activeBindings.isNullOrEmpty()) r6ClassMembers.addAll(activeBindings)

    return r6ClassMembers
  }

  fun getAssociatedFields(callExpression: RCallExpression,
                          argumentInfo: RArgumentInfo? = RParameterInfoUtil.getArgumentInfo(callExpression),
                          onlyPublic: Boolean = false): List<R6ClassField>? {
    argumentInfo ?: return null
    if (!callExpression.isFunctionFromLibrarySoft(R6CreateClassMethod, R6PackageName)) return null

    val r6ClassFields = mutableListOf<R6ClassField>()
    val publicContents = (argumentInfo.getArgumentPassedToParameter(argumentPublic) as? RCallExpressionImpl)?.argumentList?.expressionList
    if (!publicContents.isNullOrEmpty()) getFieldsFromExpressionList(r6ClassFields, publicContents, true)

    if (!onlyPublic) {
      val privateContents = (argumentInfo.getArgumentPassedToParameter(argumentPrivate) as? RCallExpressionImpl)?.argumentList?.expressionList
      if (!privateContents.isNullOrEmpty()) getFieldsFromExpressionList(r6ClassFields, privateContents, false)
    }

    return r6ClassFields
  }

  fun getAssociatedMethods(callExpression: RCallExpression,
                           argumentInfo: RArgumentInfo? = RParameterInfoUtil.getArgumentInfo(callExpression),
                           onlyPublic: Boolean = false): List<R6ClassMethod>? {
    argumentInfo ?: return null
    if (!callExpression.isFunctionFromLibrarySoft(R6CreateClassMethod, R6PackageName)) return null

    val r6ClassMethods = mutableListOf<R6ClassMethod>()
    val publicContents = (argumentInfo.getArgumentPassedToParameter(argumentPublic) as? RCallExpressionImpl)?.argumentList?.expressionList
    if (!publicContents.isNullOrEmpty()) getMethodsFromExpressionList(r6ClassMethods, publicContents, true)

    if (!onlyPublic) {
      val privateContents = (argumentInfo.getArgumentPassedToParameter(argumentPrivate) as? RCallExpressionImpl)?.argumentList?.expressionList
      if (!privateContents.isNullOrEmpty()) getMethodsFromExpressionList(r6ClassMethods, privateContents, false)
    }

    return r6ClassMethods
  }

  fun getAssociatedActiveBindings(callExpression: RCallExpression,
                                  argumentInfo: RArgumentInfo? = RParameterInfoUtil.getArgumentInfo(callExpression)): List<R6ClassActiveBinding>? {
    argumentInfo ?: return null
    if (!callExpression.isFunctionFromLibrarySoft(R6CreateClassMethod, R6PackageName)) return null
    val r6ClassActiveBindings = mutableListOf<R6ClassActiveBinding>()

    val activeBindings = (argumentInfo.getArgumentPassedToParameter(argumentActive) as? RCallExpressionImpl)?.argumentList?.expressionList
    if (!activeBindings.isNullOrEmpty()) getActiveBindingsFromExpressionList(r6ClassActiveBindings, activeBindings)
    return r6ClassActiveBindings
  }

  fun parseR6ClassInfo(callExpression: RCallExpression): R6ClassInfo? {
    if (!callExpression.isFunctionFromLibrarySoft(R6CreateClassMethod, R6PackageName)) return null
    val project = callExpression.project
    var definition = project.getUserData(INSTANTIATE_CLASS_DEFINITION_KEY)

    if (definition == null || !definition.isValid) {
      val instantiateClassDefinition =
        RElementFactory.createRPsiElementFromText(callExpression.project, INSTANTIATE_CLASS_DEFINITION) as RAssignmentStatement
      definition = instantiateClassDefinition.also { project.putUserData(INSTANTIATE_CLASS_DEFINITION_KEY, it) }
    }

    val argumentInfo = RParameterInfoUtil.getArgumentInfo(callExpression, definition) ?: return null
    val className = getAssociatedClassName(callExpression, argumentInfo) ?: return null
    val superClassName = getAssociatedSuperClassName(callExpression, argumentInfo) ?: ""
    val fields = getAssociatedFields(callExpression, argumentInfo) ?: emptyList()
    val methods = getAssociatedMethods(callExpression, argumentInfo) ?: emptyList()
    val activeBindings = getAssociatedActiveBindings(callExpression, argumentInfo) ?: emptyList()

    val packageName = RPackageProjectManager.getInstance(project).getProjectPackageDescriptionInfo()?.packageName ?: ""
    return R6ClassInfo(className, packageName, superClassName, fields, methods, activeBindings)
  }

  private fun getFieldsFromExpressionList(r6ClassFields: MutableList<R6ClassField>, callExpressions: List<RExpression>, isFromPublicScope: Boolean){
    callExpressions.forEach {
      if (it.lastChild !is RFunctionExpression && !it.name.isNullOrEmpty()) { r6ClassFields.add(R6ClassField(it.name!!, isFromPublicScope)) }
    }
  }

  private fun getMethodsFromExpressionList(r6ClassMethods: MutableList<R6ClassMethod>, callExpressions: List<RExpression>, isFromPublicScope: Boolean){
    callExpressions.forEach {
      if (it.lastChild is RFunctionExpression && !it.name.isNullOrEmpty()) { r6ClassMethods.add(R6ClassMethod(it.name!!, isFromPublicScope)) }
    }
  }

  private fun getActiveBindingsFromExpressionList(r6ClassActiveBindings: MutableList<R6ClassActiveBinding>, callExpressions: List<RExpression>){
    callExpressions.forEach {
      if (it.lastChild is RFunctionExpression && !it.name.isNullOrEmpty()) { r6ClassActiveBindings.add(R6ClassActiveBinding(it.name!!)) }
    }
  }
}