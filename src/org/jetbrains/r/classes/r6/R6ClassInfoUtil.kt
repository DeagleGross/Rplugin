/*
 * Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.jetbrains.r.classes.r6

import com.intellij.openapi.util.Key
import com.intellij.psi.util.CachedValuesManager
import org.jetbrains.r.hints.parameterInfo.RArgumentInfo
import org.jetbrains.r.hints.parameterInfo.RParameterInfoUtil
import org.jetbrains.r.packages.RPackageProjectManager
import org.jetbrains.r.psi.RElementFactory
import org.jetbrains.r.psi.api.*
import org.jetbrains.r.psi.impl.RCallExpressionImpl
import org.jetbrains.r.psi.impl.RMemberExpressionImpl
import org.jetbrains.r.psi.isFunctionFromLibrarySoft
import org.jetbrains.r.psi.references.RSearchScopeUtil
import org.jetbrains.r.psi.stubs.classes.LibraryClassNameIndexProvider

object R6ClassInfoUtil {
  const val R6PackageName = "R6"
  const val R6CreateClassMethod = "R6Class"

  const val R6ClassThisKeyword = "self"
  const val functionNew = "new"
  const val functionSet = "set"

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

  /**
   * @param call expression `MyClass$new()`
   * @return class name which type is instantiated
   */
  fun getAssociatedClassNameFromInstantiationCall(call: RCallExpression): String? {
    val callExpression = call.expression as? RMemberExpressionImpl ?: return null
    if (callExpression.rightExpr?.text != functionNew) return null
    return callExpression.leftExpr?.name
  }

  /**
   * @param rMemberExpression expression `self$someMember` or `obj$someMember`
   * @return className of class where `self$...` is used or of which object is called
   */
  fun getClassNameFromInternalClassMemberUsageExpression(rMemberExpression: RMemberExpression?): String? {
    if (rMemberExpression == null) return null
    val classDefinitionCall = R6ClassPsiUtil.getClassDefinitionCallFromMemberUsage(rMemberExpression) ?: return null
    return getAssociatedClassNameFromR6ClassCall(classDefinitionCall)
  }

  /**
   * @param callExpression expression `R6Class("MyClass", ...)`
   * @return
   */
  fun getAssociatedClassNameFromR6ClassCall(callExpression: RCallExpression,
                                            argumentInfo: RArgumentInfo? = RParameterInfoUtil.getArgumentInfo(callExpression)): String? {
    argumentInfo ?: return null
    if (!callExpression.isFunctionFromLibrarySoft(R6CreateClassMethod, R6PackageName)) return null
    val rAssignmentStatement = callExpression.parent as? RAssignmentStatement ?: return null
    return rAssignmentStatement.assignee?.name
  }

  /**
   * @param callExpression expression `R6Class("MyClass", ...)`
   * @return names of all inherited chain of parents
   */
  fun getAssociatedSuperClassesHierarchy(callExpression: RCallExpression,
                                         argumentInfo: RArgumentInfo? = RParameterInfoUtil.getArgumentInfo(callExpression)): MutableList<String>? {
    argumentInfo ?: return null

    var classDeclarationExpression = callExpression
    val classNamesHierarchy = mutableListOf<String>()

    while (true) {
      val directInherit = getAssociatedSuperClassName(classDeclarationExpression) ?: break
      classNamesHierarchy.add(directInherit)
      classDeclarationExpression = getSuperClassDefinitionCallExpression(classDeclarationExpression) ?: break
    }

    return classNamesHierarchy
  }

  /**
   * @param callExpression expression `R6Class("MyClass", ...)`
   * @return direct parent classname
   */
  private fun getAssociatedSuperClassName(callExpression: RCallExpression,
                                          argumentInfo: RArgumentInfo? = RParameterInfoUtil.getArgumentInfo(callExpression)): String? {
    argumentInfo ?: return null
    if (!callExpression.isFunctionFromLibrarySoft(R6CreateClassMethod, R6PackageName)) return null
    return (argumentInfo.getArgumentPassedToParameter(argumentSuperClass) as? RIdentifierExpression)?.name
  }

  private fun getSuperClassDefinitionCallExpression(callExpression: RCallExpression,
                                                    argumentInfo: RArgumentInfo? = RParameterInfoUtil.getArgumentInfo(callExpression)): RCallExpression? {
    if (!callExpression.isFunctionFromLibrarySoft(R6CreateClassMethod, R6PackageName)) return null
    val inheritClassDefinition = argumentInfo?.getArgumentPassedToParameter(argumentSuperClass) as? RIdentifierExpression
    return (inheritClassDefinition?.reference?.resolve() as? RAssignmentStatement)?.assignedValue as? RCallExpression
  }

  fun getAllClassMembers(callExpression: RCallExpression): List<IR6ClassMember> {
    val r6ClassInfo = CachedValuesManager.getProjectPsiDependentCache(callExpression) { callExpression.associatedR6ClassInfo } ?: return emptyList()
    val allSuperClasses = getAssociatedSuperClassesHierarchy(callExpression)

    val callSearchScope = RSearchScopeUtil.getScope(callExpression)
    val project = callExpression.project

    if (allSuperClasses != null) {
      return (r6ClassInfo.members + r6ClassInfo.activeBindings + allSuperClasses.flatMap { superClassName ->
        LibraryClassNameIndexProvider.R6ClassNameIndex.findClassInfos(superClassName, project, callSearchScope).flatMap { it.members + it.activeBindings }
      }).distinctBy { it.name }
    }

    return emptyList()
  }

  fun getAssociatedMembers(callExpression: RCallExpression,
                           argumentInfo: RArgumentInfo? = RParameterInfoUtil.getArgumentInfo(callExpression),
                           onlyPublic: Boolean = false): List<R6ClassMember>? {
    argumentInfo ?: return null
    if (!callExpression.isFunctionFromLibrarySoft(R6CreateClassMethod, R6PackageName)) return null

    val r6ClassMethods = mutableListOf<R6ClassMember>()
    val publicContents = (argumentInfo.getArgumentPassedToParameter(argumentPublic) as? RCallExpressionImpl)?.argumentList?.expressionList
    if (!publicContents.isNullOrEmpty()) getMembersFromExpressionList(r6ClassMethods, publicContents, true)

    if (!onlyPublic) {
      val privateContents = (argumentInfo.getArgumentPassedToParameter(
        argumentPrivate) as? RCallExpressionImpl)?.argumentList?.expressionList
      if (!privateContents.isNullOrEmpty()) getMembersFromExpressionList(r6ClassMethods, privateContents, false)
    }

    return r6ClassMethods
  }

  fun getAssociatedActiveBindings(callExpression: RCallExpression,
                                  argumentInfo: RArgumentInfo? = RParameterInfoUtil.getArgumentInfo(
                                    callExpression)): List<R6ClassActiveBinding>? {
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
    val className = getAssociatedClassNameFromR6ClassCall(callExpression, argumentInfo) ?: return null
    val superClassesHierarchy = getAssociatedSuperClassesHierarchy(callExpression, argumentInfo) ?: emptyList()
    val members = getAssociatedMembers(callExpression, argumentInfo) ?: emptyList()
    val activeBindings = getAssociatedActiveBindings(callExpression, argumentInfo) ?: emptyList()

    return R6ClassInfo(className, superClassesHierarchy, members, activeBindings)
  }

  private fun getMembersFromExpressionList(r6ClassFields: MutableList<R6ClassMember>,
                                          callExpressions: List<RExpression>,
                                          isFromPublicScope: Boolean) {
    callExpressions.forEach {
      if (it.lastChild != null && !it.name.isNullOrEmpty()) {
        r6ClassFields.add(R6ClassMember(it.name!!, isFromPublicScope))
      }
    }
  }

  private fun getActiveBindingsFromExpressionList(r6ClassActiveBindings: MutableList<R6ClassActiveBinding>,
                                                  callExpressions: List<RExpression>) {
    callExpressions.forEach {
      if (it.lastChild is RFunctionExpression && !it.name.isNullOrEmpty()) {
        r6ClassActiveBindings.add(R6ClassActiveBinding(it.name!!))
      }
    }
  }
}