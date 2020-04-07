// Copyright (c) 2017, Holger Brandl, Ekaterina Tuzova
/*
 * Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.jetbrains.r.editor

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.*
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.util.ProcessingContext
import com.intellij.util.Processor
import org.apache.commons.lang.StringUtils
import org.jetbrains.annotations.Nullable
import org.jetbrains.r.RLanguage
import org.jetbrains.r.console.RConsoleRuntimeInfo
import org.jetbrains.r.console.RConsoleView
import org.jetbrains.r.console.runtimeInfo
import org.jetbrains.r.editor.completion.AES_PARAMETER_FILTER
import org.jetbrains.r.editor.completion.GGPlot2AesColumnCompletionProvider
import org.jetbrains.r.interpreter.RInterpreterManager
import org.jetbrains.r.packages.RPackage
import org.jetbrains.r.parsing.RElementTypes.*
import org.jetbrains.r.psi.*
import org.jetbrains.r.psi.api.*
import org.jetbrains.r.psi.references.RSearchScopeUtil
import org.jetbrains.r.psi.stubs.RAssignmentCompletionIndex
import org.jetbrains.r.psi.stubs.RInternalAssignmentCompletionIndex
import org.jetbrains.r.refactoring.RNamesValidator
import org.jetbrains.r.rinterop.RValueFunction
import org.jetbrains.r.util.PathUtil
import javax.swing.Icon
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.math.min

const val TABLE_MANIPULATION_PRIORITY = 110.0
const val IMPORT_PACKAGE_PRIORITY = 110.0
const val NAMED_ARGUMENT_PRIORITY = 100.0
const val VARIABLE_GROUPING = 90
const val PACKAGE_PRIORITY = -1.0
const val GLOBAL_GROUPING = 0
const val NAMESPACE_NAME_GROUPING = -1

class RLookupElement(private val lookup: String,
                     private val bold: Boolean,
                     private val icon: Icon? = null,
                     private val packageName: String? = null,
                     private val tailText: String? = null,
                     private val itemText: String = lookup) : LookupElement() {

  override fun getLookupString() = lookup

  override fun renderElement(presentation: LookupElementPresentation) {
    presentation.itemText = itemText
    presentation.isItemTextBold = bold
    presentation.icon = icon
    presentation.typeText = packageName
    if (tailText != null) presentation.appendTailText(tailText, true)
  }
}

class RCompletionContributor : CompletionContributor() {

  init {
    addTableContextCompletion()
    addGGPlotAesColumnCompletionProvider()
    addStringLiteralCompletion()
    addInstalledPackageCompletion()
    addNamespaceAccessExpression()
    addMemberAccessCompletion()
    addIdentifierCompletion()
  }

  private fun addNamespaceAccessExpression() {
    extend(CompletionType.BASIC, psiElement()
      .withLanguage(RLanguage.INSTANCE)
      .and(RElementFilters.NAMESPACE_REFERENCE_FILTER), NamespaceAccessCompletionProvider())
  }

  private fun addIdentifierCompletion() {
    extend(CompletionType.BASIC, psiElement()
      .withLanguage(RLanguage.INSTANCE)
      .andOr(RElementFilters.IDENTIFIER_FILTER, RElementFilters.OPERATOR_FILTER), IdentifierCompletionProvider())
  }

  private fun addMemberAccessCompletion() {
    extend(CompletionType.BASIC, psiElement().withLanguage(RLanguage.INSTANCE)
      .and(RElementFilters.MEMBER_ACCESS_FILTER), MemberAccessCompletionProvider())
  }

  private fun addInstalledPackageCompletion() {
    extend(CompletionType.BASIC, psiElement().withLanguage(RLanguage.INSTANCE)
      .and(RElementFilters.IMPORT_CONTEXT), InstalledPackageCompletionProvider())
  }

  private fun addTableContextCompletion() {
    extend(CompletionType.BASIC, psiElement().withLanguage(RLanguage.INSTANCE)
      .and(RElementFilters.IDENTIFIER_OR_STRING_FILTER), TableContextCompletionProvider())
  }

  private fun addStringLiteralCompletion() {
    extend(CompletionType.BASIC, psiElement().withLanguage(RLanguage.INSTANCE)
      .and(RElementFilters.STRING_FILTER), StringLiteralCompletionProvider())
  }

  private fun addGGPlotAesColumnCompletionProvider() {
    extend(CompletionType.BASIC, psiElement().withLanguage(RLanguage.INSTANCE)
      .and(AES_PARAMETER_FILTER), GGPlot2AesColumnCompletionProvider())
  }

  private class MemberAccessCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
      val position = parameters.position
      val file = parameters.originalFile
      val info = file.runtimeInfo ?: return
      val memberAccess = PsiTreeUtil.getParentOfType(position, RMemberExpression::class.java) ?: return
      val leftExpr = memberAccess.leftExpr ?: return
      val noCalls = PsiTreeUtil.processElements(leftExpr) { it !is RCallExpression }
      if (noCalls) {
        info.loadObjectNames(leftExpr.text).forEach { result.consume(createNamespaceAccess(it)) }
      }
    }
  }


  private class InstalledPackageCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
      val position = parameters.position
      val interpreter = RInterpreterManager.getInterpreter(position.project) ?: return
      val installedPackages = interpreter.installedPackages
      installedPackages.filter { it.isUser }.forEach {
        result.consume(createPackageLookupElement(it.packageName, true))
      }
    }
  }

  private class NamespaceAccessCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
      val position = PsiTreeUtil.getParentOfType(parameters.position, RIdentifierExpression::class.java, false)
                     ?: return
      val project = position.project
      val namespaceAccess = position.parent as? RNamespaceAccessExpression ?: return
      val namespaceName = namespaceAccess.namespaceName
      val interpreter = RInterpreterManager.getInterpreter(project) ?: return
      val packageFile = interpreter.getSkeletonFileByPackageName(namespaceName) ?: return
      val scope = GlobalSearchScope.fileScope(packageFile)
      val isInternalAccess = namespaceAccess.node.findChildByType(R_TRIPLECOLON) != null
      addCompletionFromIndices(project, scope, parameters.originalFile, "", HashSet(), result,
                               isInternalAccess = isInternalAccess)
    }
  }

  private class IdentifierCompletionProvider : CompletionProvider<CompletionParameters>() {

    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, _result: CompletionResultSet) {
      val probableIdentifier = PsiTreeUtil.getParentOfType(parameters.position, RExpression::class.java, false)
      val position = if (probableIdentifier != null) {
        val infixOperator = PsiTreeUtil.findChildOfType(probableIdentifier, RInfixOperator::class.java)
        if (infixOperator != null) infixOperator else probableIdentifier  // operator surrounded by % or identifier
      }
      else PsiTreeUtil.getParentOfType(parameters.position, RPsiElement::class.java, false) ?: return // operator with parser error

      val result =
        if (probableIdentifier == null) _result.withPrefixMatcher("%${_result.prefixMatcher.prefix}")
        else _result
      val parent = position.parent
      val shownNames = HashSet<String>()
      val project = position.project
      val originalFile = parameters.originalFile

      // don't complete parameters name
      if (parent is RParameter && position == parent.variable) {
        return
      }

      val file = parameters.originalFile
      val isHelpFromRConsole = file.getUserData(RConsoleView.IS_R_CONSOLE_KEY)?.let { file.firstChild is RHelpExpression } ?: false
      addKeywords(position, shownNames, result, isHelpFromRConsole)
      addLocalsFromControlFlow(position, shownNames, result, isHelpFromRConsole)
      addLocalsFromRuntime(originalFile, shownNames, result)

      // we are completing an assignee, so we don't want to suggest function names here
      if (position is RExpression && position.isAssignee()) {
        return
      }

      addPackageCompletion(position, result)
      addNamedArgumentsCompletion(originalFile, parent, result)
      val prefix = position.name?.let { StringUtil.trimEnd(it, CompletionInitializationContext.DUMMY_IDENTIFIER_TRIMMED) } ?: ""
      addCompletionFromIndices(project, RSearchScopeUtil.getScope(originalFile), parameters.originalFile, prefix, shownNames, result,
                               isHelpFromRConsole)
    }

    private fun addLocalsFromRuntime(originFile: PsiFile, shownNames: HashSet<String>, result: CompletionResultSet) {
      originFile.runtimeInfo?.variables?.let { variables ->
        variables.filterKeys { shownNames.add(it) }.forEach { (name, value) ->
          if (value is RValueFunction) {
            val code = "${RNamesValidator.quoteIfNeeded(name)} <- ${value.header} NULL"
            val element = RElementFactory.createRPsiElementFromTextOrNull(originFile.project, code) as? RAssignmentStatement ?: return@forEach
            result.consume(createFunctionLookupElement(element, isHelpFromRConsole = false, isLocal = true))
          }
          else {
            result.consume(createLocalVariableLookupElement(name, false))
          }
        }
      }
    }

    private fun addKeywords(position: RPsiElement,
                            shownNames: HashSet<String>,
                            result: CompletionResultSet,
                            isHelpFromRConsole: Boolean) {
      for (keyword in BUILTIN_CONSTANTS + KEYWORDS) {
        val necessaryCondition = KEYWORD_NECESSARY_CONDITION[keyword]
        val stringValue = keyword.toString()
        if (isHelpFromRConsole || necessaryCondition == null || necessaryCondition(position)) {
          shownNames.add(stringValue)
          result.addElement(PrioritizedLookupElement.withGrouping(RLookupElement(stringValue, true), GLOBAL_GROUPING))
        }
      }
      for (keyword in KEYWORDS_WITH_BRACKETS) {
        val stringValue = keyword.toString()
        shownNames.add(stringValue)
        val insertHandler = InsertHandler<LookupElement> { context, _ ->
          if (!isHelpFromRConsole) {
            val document = context.document
            document.insertString(context.tailOffset, " ()")
            context.editor.caretModel.moveCaretRelatively(2, 0, false, false, false)
          }
        }
        result.addElement(createLookupElementWithGrouping(RLookupElement(stringValue, true, tailText = " (...)"),
                                                          insertHandler,
                                                          GLOBAL_GROUPING))
      }
    }

    private fun addLocalsFromControlFlow(position: RPsiElement,
                                         shownNames: HashSet<String>,
                                         result: CompletionResultSet,
                                         isHelpFromRConsole: Boolean) {
      val controlFlowHolder = PsiTreeUtil.getParentOfType(position, RControlFlowHolder::class.java)
      controlFlowHolder?.getLocalVariableInfo(position)?.variables?.values?.sortedBy { it.variableDescription.name }?.forEach {
        val name = it.variableDescription.name
        shownNames.add(name)
        val parent = it.variableDescription.firstDefinition.parent
        if (parent is RAssignmentStatement && parent.isFunctionDeclaration) {
          result.consume(createFunctionLookupElement(parent, isHelpFromRConsole, true))
        }
        else {
          result.consume(createLocalVariableLookupElement(name, parent is RParameter))
        }
      }
    }

    private fun consumeParameter(parameterName: String, shownNames: MutableSet<String>, result: CompletionResultSet) {
      if (shownNames.add(parameterName)) {
        result.consume(createNamedArgumentLookupElement(parameterName))
      }
    }

    private fun addNamedArgumentsCompletion(originalFile: PsiFile, parent: PsiElement?, result: CompletionResultSet) {
      if (parent !is RArgumentList && parent !is RNamedArgument) return

      val mainCall = (if (parent is RNamedArgument) parent.parent.parent else parent.parent) as? RCallExpression  ?: return
      val shownNames = HashSet<String>()

      for (functionDeclaration in RPsiUtil.resolveCall(mainCall)) {
        functionDeclaration.parameterNameList.forEach { consumeParameter(it, shownNames, result) }
      }

      val info = originalFile.runtimeInfo ?: return
      val mainFunctionName = when (val expression = mainCall.expression) {
        is RNamespaceAccessExpression -> expression.identifier?.name ?: return
        is RIdentifierExpression -> expression.name
        else -> return
      }
      val namedArguments = info.loadAllNamedArguments("'$mainFunctionName'")

      for (parameter in namedArguments) {
        consumeParameter(parameter, shownNames, result)
      }
    }

    companion object {
      private val BUILTIN_CONSTANTS = listOf(R_TRUE, R_FALSE, R_NULL, R_NA, R_INF, R_NAN,
                                             R_NA_INTEGER_, R_NA_REAL_, R_NA_COMPLEX_, R_NA_CHARACTER_)
      private val KEYWORDS_WITH_BRACKETS = listOf(R_IF, R_WHILE, R_FUNCTION, R_FOR)
      private val KEYWORDS = listOf(R_ELSE, R_REPEAT, R_IN)
      private val KEYWORD_NECESSARY_CONDITION = mapOf<IElementType, (PsiElement) -> Boolean>(
        R_IN to { element ->
          if (element.parent !is RForStatement) false
          else {
            val newText = element.parent.text.replace(element.text, R_IN.toString())
            val newElement = RElementFactory
              .buildRFileFromText(element.project, newText).findElementAt(element.textOffset - element.parent.textOffset)
            if (PsiTreeUtil.getParentOfType(newElement, PsiErrorElement::class.java, false) != null) false
            else {
              !isErrorElementBefore(newElement!!)
            }
          }
        },
        R_ELSE to { element ->
          var sibling: PsiElement? = PsiTreeUtil.skipWhitespacesAndCommentsBackward(element)
          while (sibling?.elementType == R_NL) {
            sibling = PsiTreeUtil.skipWhitespacesAndCommentsBackward(sibling)
          }
          sibling is RIfStatement && !sibling.node.getChildren(null).any { it.elementType == R_ELSE }
        }
      )

      private val PsiElement.prevLeafs: Sequence<PsiElement>
        get() = generateSequence({ PsiTreeUtil.prevLeaf(this) }, { PsiTreeUtil.prevLeaf(it) })

      private fun isErrorElementBefore(token: PsiElement): Boolean {
        for (leaf in token.prevLeafs) {
          if (leaf is PsiWhiteSpace || leaf is PsiComment) continue
          if (leaf is PsiErrorElement || PsiTreeUtil.findFirstParent(leaf) { it is PsiErrorElement } != null) return true
          if (leaf.textLength != 0) break
        }
        return false
      }
    }
  }

  private class TableContextCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
      val position = parameters.position
      val runtimeInfo = parameters.originalFile.runtimeInfo ?: return
      val columns = mutableListOf<TableManipulationColumn>()
      addTableCompletion(RDplyrAnalyzer, position, runtimeInfo, columns)
      addTableCompletion(RDataTableAnalyzer, position, runtimeInfo, columns)
      result.addAllElements(columns.distinct().map {
        PrioritizedLookupElement.withPriority(
          RLookupElement(it.name, true, AllIcons.Nodes.Field, packageName = it.type),
          TABLE_MANIPULATION_PRIORITY
        )
      })
    }
  }

  private class StringLiteralCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
      val stringLiteral = PsiTreeUtil.getParentOfType(parameters.position, RStringLiteralExpression::class.java, false) ?: return
      addTableLiterals(stringLiteral, parameters, result)
      addFilePathCompletion(parameters, stringLiteral, result)
    }

    private fun addTableLiterals(stringLiteral: @Nullable RStringLiteralExpression,
                                 parameters: CompletionParameters,
                                 result: CompletionResultSet) {
      val parent = stringLiteral.parent as? ROperatorExpression ?: return
      if (!parent.isBinary || (parent.operator?.name != "==" && parent.operator?.name != "!=")) return
      val other = (if (parent.leftExpr == stringLiteral) parent.rightExpr else parent.leftExpr) ?: return
      val runtimeInfo = parameters.originalFile.runtimeInfo ?: return

      val values = mutableListOf<String>()
      addColumnStringValues(RDplyrAnalyzer, stringLiteral, other, runtimeInfo, values)
      addColumnStringValues(RDataTableAnalyzer, stringLiteral, other, runtimeInfo, values)
      val insertHandler = InsertHandler<LookupElement> { insertHandlerContext, _ ->
        insertHandlerContext.file.findElementAt(insertHandlerContext.editor.caretModel.offset)?.let { element ->
          insertHandlerContext.editor.caretModel.moveToOffset(element.textRange.endOffset)
        }
      }
      result.addAllElements(values.distinct().map {
        createLookupElementWithPriority(RLookupElement(escape(it), true, AllIcons.Nodes.Field, itemText = it),
                                        insertHandler,
                                        TABLE_MANIPULATION_PRIORITY)
      })
    }

    private fun <T : TableManipulationFunction> addColumnStringValues(tableAnalyser: TableManipulationAnalyzer<T>,
                                                                      stringLiteral: RStringLiteralExpression,
                                                                      columnNameIdentifier: RExpression,
                                                                      runtimeInfo: RConsoleRuntimeInfo,
                                                                      result: MutableList<String>) {
      val contextInfo = tableAnalyser.getContextInfo(stringLiteral, runtimeInfo)
      val text = if (contextInfo != null) {
        val columnName =
          if (columnNameIdentifier is RMemberExpression) columnNameIdentifier.rightExpr?.name
          else columnNameIdentifier.name
        val command = StringBuilder()
        command.append("unlist(list(")
        contextInfo.callInfo.passedTableArguments.forEachIndexed { ind, table ->
          if (ind != 0) command.append(", ")
          command.append("(")
          tableAnalyser.transformExpression(table, command, runtimeInfo, true)
          command.append(")$$columnName")
        }
        command.append("))")
        command.toString()
      } else {
        if (!tableAnalyser.isSafe(columnNameIdentifier, runtimeInfo)) return
        "(${columnNameIdentifier.text})"
      }
      result.addAll(runtimeInfo.loadDistinctStrings(text).filter { it.isNotEmpty() })
    }

    private val escape = StringUtil.escaper(true, "\"")::`fun`
  }

  companion object {
    private fun <T : TableManipulationFunction> addTableCompletion(tableAnalyser: TableManipulationAnalyzer<T>,
                                                                   position: PsiElement,
                                                                   runtimeInfo: RConsoleRuntimeInfo,
                                                                   result: MutableList<TableManipulationColumn>) {
      val (tableCallInfo, currentArgument) = tableAnalyser.getContextInfo(position, runtimeInfo) ?: return
      val tableInfos = tableCallInfo.passedTableArguments.map { tableAnalyser.getTableColumns(it, runtimeInfo) }
      val isCorrectTableType = tableInfos.all { it.type == tableAnalyser.tableColumnType }
      val isQuotesNeeded = !isCorrectTableType && tableAnalyser.isSubscription(tableCallInfo.function)
                           || tableCallInfo.function.isQuotesNeeded(tableCallInfo.argumentInfo, currentArgument)
      if (!isQuotesNeeded && position.parent is RStringLiteralExpression) return

      var columns = tableInfos.map { it.columns }.flatten()
        .groupBy { it.name }
        .map { (name, list) ->
          TableManipulationColumn(name, StringUtils.join(list.mapNotNull { it.type }, "/"))
        }

      if (tableAnalyser is RDplyrAnalyzer)
        columns = tableAnalyser.addCurrentColumns(columns, tableCallInfo, currentArgument)

      if (isQuotesNeeded && position.parent !is RStringLiteralExpression) {
        columns = columns.map { TableManipulationColumn("\"${it.name}\"", it.type) }
      }
      result.addAll(columns)
    }

    private fun addFilePathCompletion(parameters: CompletionParameters,
                                      stringLiteral: RStringLiteralExpression,
                                      _result: CompletionResultSet) {
      val reference = parameters.position.containingFile.findReferenceAt(parameters.offset) as? FileReference ?: return
      val filepath = stringLiteral.name?.trim() ?: return
      val filePrefix = PathUtil.toPath(filepath)?.fileName?.toString()?.replace(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED, "") ?: return
      val result = _result.withPrefixMatcher(filePrefix)
      val variants = reference.variants.map {
        when (it) {
          is LookupElement -> it
          is PsiNamedElement -> LookupElementBuilder.createWithIcon(it)
          else -> LookupElementBuilder.create(it)
        }
      }

      for (lookup in variants) {
        if (result.prefixMatcher.prefixMatches(lookup)) {
          result.addElement(lookup)
        }
      }
    }

    private fun addPackageCompletion(position: PsiElement, result: CompletionResultSet) {
      val interpreter = RInterpreterManager.getInterpreter(position.project) ?: return
      val installedPackages = interpreter.installedPackages
      installedPackages.forEach { result.consume(createPackageLookupElement(it.packageName, false)) }
    }

    private fun createGlobalLookupElement(assignment: RAssignmentStatement, isHelpFromRConsole: Boolean): LookupElement {
      return if (assignment.isFunctionDeclaration) {
        createFunctionLookupElement(assignment, isHelpFromRConsole)
      }
      else {
        val name = assignment.name
        val packageName = RPackage.getOrCreateRPackageBySkeletonFile(assignment.containingFile)?.name
        val icon = AllIcons.Nodes.Constant
        PrioritizedLookupElement.withGrouping(RLookupElement(name, false, icon, packageName), GLOBAL_GROUPING)
      }
    }

    private fun createFunctionLookupElement(functionAssignment: RAssignmentStatement,
                                            isHelpFromRConsole: Boolean,
                                            isLocal: Boolean = false): LookupElement {
      if (functionAssignment.name.startsWith("%")) return createOperatorLookupElement(functionAssignment, isLocal)
      val packageName = if (isLocal) null else RPackage.getOrCreateRPackageBySkeletonFile(functionAssignment.containingFile)?.name
      val icon = AllIcons.Nodes.Function
      val tailText = functionAssignment.functionParameters
      val noArgs = tailText == "()"
      val insertHandler = InsertHandler<LookupElement> { context, _ ->
        if (!isHelpFromRConsole) {
          val document = context.document
          document.insertString(context.tailOffset, "()")
          context.editor.caretModel.moveCaretRelatively(if (noArgs) 2 else 1, 0, false, false, false)
        }
      }
      return  createLookupElementWithGrouping(RLookupElement(functionAssignment.name, false, icon, packageName, tailText),
                                              insertHandler,
                                              if (isLocal) VARIABLE_GROUPING else GLOBAL_GROUPING)
    }

    private fun createLocalVariableLookupElement(lookupString: String, isParameter: Boolean): LookupElement {
      val icon = if (isParameter) AllIcons.Nodes.Parameter else AllIcons.Nodes.Variable
      return PrioritizedLookupElement.withGrouping(RLookupElement(lookupString, true, icon), VARIABLE_GROUPING)
    }

    private fun createNamedArgumentLookupElement(lookupString: String): LookupElement {
      val icon = AllIcons.Nodes.Parameter
      val insertHandler = InsertHandler<LookupElement> { context, _ ->
        val document = context.document
        document.insertString(context.tailOffset, " = ")
        context.editor.caretModel.moveCaretRelatively(3, 0, false, false, false)
      }
      return createLookupElementWithPriority(RLookupElement(lookupString, true, icon, tailText = " = "),
                                             insertHandler,
                                             NAMED_ARGUMENT_PRIORITY)
    }

    private fun createPackageLookupElement(lookupString: String, inImport: Boolean): LookupElement {
      return if (inImport) {
        PrioritizedLookupElement.withPriority(RLookupElement(lookupString, true, AllIcons.Nodes.Package), IMPORT_PACKAGE_PRIORITY)
      }
      else {
        val insertHandler = InsertHandler<LookupElement> { context, _ ->
          val document = context.document
          document.insertString(context.tailOffset, "::")
          AutoPopupController.getInstance(context.project).scheduleAutoPopup(context.editor)
          context.editor.caretModel.moveCaretRelatively(2, 0, false, false, false)
        }
        createLookupElementWithPriority(RLookupElement(lookupString, true, AllIcons.Nodes.Package, tailText = "::"),
                                        insertHandler,
                                        PACKAGE_PRIORITY)
      }
    }

    private fun createOperatorLookupElement(functionAssignment: RAssignmentStatement, isLocal: Boolean): LookupElement {
      val packageName = if (isLocal) null else RPackage.getOrCreateRPackageBySkeletonFile(functionAssignment.containingFile)?.name
      val icon = AllIcons.Nodes.Function
      val insertHandler = InsertHandler<LookupElement> { context, _ ->
        val document = context.document
        val startOffset = context.tailOffset
        val endOffset = min(context.tailOffset + 1, document.textLength)
        if (endOffset <= startOffset) return@InsertHandler
        if (document.getText(TextRange(startOffset, endOffset)) == "%") {
          document.replaceString(startOffset, endOffset, "")
        }
      }
      return createLookupElementWithGrouping(RLookupElement(functionAssignment.name, false, icon, packageName),
                                             insertHandler,
                                             if (isLocal) VARIABLE_GROUPING else GLOBAL_GROUPING)
    }

    private fun createNamespaceAccess(lookupString: String): LookupElement {
      val insertHandler = InsertHandler<LookupElement> { context, _ ->
        val document = context.document
        document.replaceString(context.startOffset, context.tailOffset, RNamesValidator.quoteIfNeeded(lookupString))
        context.editor.caretModel.moveToOffset(context.tailOffset)
      }
      return createLookupElementWithGrouping(RLookupElement(lookupString, false, AllIcons.Nodes.Package),
                                             insertHandler,
                                             NAMESPACE_NAME_GROUPING)
    }

    private fun addCompletionFromIndices(project: Project,
                                         scope: GlobalSearchScope,
                                         originFile: PsiFile,
                                         prefix: String,
                                         shownNames: HashSet<String>,
                                         result: CompletionResultSet,
                                         isHelpFromRConsole: Boolean = false, isInternalAccess: Boolean = false) {
      val runtimeInfo = originFile.runtimeInfo
      val interpreter = RInterpreterManager.getInterpreter(originFile.project)
      var hasElementsWithPrefix = false
      if (runtimeInfo != null && interpreter != null) {
        val loadedPackages = runtimeInfo.loadedPackages
          .mapNotNull { interpreter.getSkeletonFileByPackageName(it.key)?.virtualFile?.to(it.value) }
          .toMap()
        val runtimeScope = GlobalSearchScope.filesScope(originFile.project, loadedPackages.keys).intersectWith(scope)
        val lookupElements = mutableMapOf<String, Pair<LookupElement, Int?>>()
        processElementsFromIndex(project, runtimeScope, isHelpFromRConsole, isInternalAccess) { element, file ->
          if (shownNames.contains(element.lookupString)) return@processElementsFromIndex
          if (element.lookupString.startsWith(prefix)) {
            hasElementsWithPrefix = true
          }
          val previousPriority = lookupElements[element.lookupString]?.second
          val currentPriority = loadedPackages[file]
          if (previousPriority == null || (currentPriority != null && currentPriority < previousPriority)) {
            lookupElements[element.lookupString] = element to currentPriority
          }
        }
        result.addAllElements(lookupElements.values.map { it.first })
        shownNames.addAll(lookupElements.keys)
      }
      if (!hasElementsWithPrefix) {
        processElementsFromIndex(project, scope, isHelpFromRConsole, isInternalAccess) { it, _ ->
          if (shownNames.add(it.lookupString)) result.consume(it)
        }
      }
    }

    private fun processElementsFromIndex(project: Project,
                                         scope: GlobalSearchScope,
                                         isHelpFromRConsole: Boolean,
                                         isInternalAccess: Boolean,
                                         consumer: (LookupElement, VirtualFile) -> Unit) {

      val indexAccessor = if (isInternalAccess) RInternalAssignmentCompletionIndex else RAssignmentCompletionIndex
      indexAccessor.process("", project, scope, Processor { assignment ->
        consumer(createGlobalLookupElement(assignment, isHelpFromRConsole), assignment.containingFile.virtualFile)
        return@Processor true
      })
    }
  }
}

private fun createLookupElementWithGrouping(lookupElement: LookupElement,
                                            insertHandler: InsertHandler<LookupElement>,
                                            grouping: Int): LookupElement {
  val lookupElementWithInsertHandler = PrioritizedLookupElement.withInsertHandler(lookupElement, insertHandler)
  return PrioritizedLookupElement.withGrouping(lookupElementWithInsertHandler, grouping)
}

private fun createLookupElementWithPriority(lookupElement: LookupElement,
                                            insertHandler: InsertHandler<LookupElement>,
                                            priority: Double): LookupElement {
  val lookupElementWithInsertHandler = PrioritizedLookupElement.withInsertHandler(lookupElement, insertHandler)
  return PrioritizedLookupElement.withPriority(lookupElementWithInsertHandler, priority)
}