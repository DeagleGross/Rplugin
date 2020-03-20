/*
 * Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.datavis.r.inlays.components

import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.ui.Gray
import com.intellij.ui.tabs.TabInfo
import com.intellij.ui.tabs.TabsListener
import com.intellij.ui.tabs.impl.JBTabsImpl
import org.intellij.datavis.r.inlays.InlayOutput
import org.intellij.datavis.r.inlays.MouseWheelUtils
import org.intellij.datavis.r.inlays.dataframe.DataFrameCSVAdapter
import org.intellij.datavis.r.inlays.runAsyncInlay
import org.intellij.datavis.r.ui.ToolbarUtil
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Rectangle
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.JComponent
import javax.swing.JPanel

/** Page control with table and chart pages. */
class NotebookInlayMultiOutput(val editor: Editor, parent: Disposable) : NotebookInlayState() {

  /** Page control for results viewing. */
  private val tabs: JBTabsImpl

  /** In some cases there could be even text ot html output. */
  private val inlayPages: List<NotebookInlayState> = ArrayList()

  private val heights: MutableMap<TabInfo, Int> = HashMap()

  var onChange: (() -> Unit)? = null

  // private var output: NotebookInlayOutput? = null

  private val disposable = Disposer.newDisposable()

  private var maxHeight: Int = -1

  private val project = editor.project!!

  private val tabsOutput: MutableSet<NotebookInlayOutput> = mutableSetOf()

  @Volatile
  private var isInViewport: Boolean = false

  init {
    Disposer.register(parent, disposable)
    tabs = JBTabsImpl(project, IdeFocusManager.getInstance(project), disposable)
    tabs.addListener(object : TabsListener {
      override fun selectionChanged(oldSelection: TabInfo?, newSelection: TabInfo?) {
        oldSelection?.onViewportChange(false)  // Definitely false
        newSelection?.onViewportChange(isInViewport)  // Might be true
        onChange?.invoke()
      }
    })

    tabs.component.isOpaque = false
    tabs.component.background = Gray.TRANSPARENT

    MouseWheelUtils.wrapMouseWheelListeners(tabs.component, parent)
    add(tabs.component, BorderLayout.CENTER)

    // To make it possible to use JLayeredPane as a parent of NotebookInlayState.
    addComponentListener(object : ComponentAdapter() {
      override fun componentResized(e: ComponentEvent) {
        tabs.component.bounds = Rectangle(0, 0, e.component.bounds.width, e.component.bounds.height)
      }
    })
  }

  fun setCurrentPage(currentPage: String) {
    val tabToSelect = tabs.tabs.find { it.text == currentPage }
    if(tabToSelect != null) {
      tabs.select(tabToSelect, false)
    }
  }

  fun onOutputs(inlayOutputs: List<InlayOutput>) {
    tabs.removeAllTabs()
    tabsOutput.clear()
    inlayOutputs.forEach { inlayOutput ->
      if (inlayOutput.type == "TABLE") {
        runAsyncInlay {
          val data = DataFrameCSVAdapter.fromCsvString(inlayOutput.data)
          invokeLater {
            NotebookInlayData(project, disposable, data).apply {
              addTab(inlayOutput)
              setupOnHeightCalculated()
              setDataFrame(data)
            }
          }
        }
      }
      else {
        NotebookInlayOutput(editor, disposable).apply {
          setupOnHeightCalculated()
          addData(inlayOutput.type, inlayOutput.data, inlayOutput.progressStatus)
          tabsOutput.add(this)
          addTab(inlayOutput)
        }
      }
    }
  }

  override fun updateProgressStatus(progressStatus: InlayProgressStatus) {
    tabsOutput.forEach { output ->
      output.updateProgressStatus(progressStatus)
    }
  }

  override fun clear() {
  }

  override fun getCollapsedDescription(): String {
    return "foooo"
  }

  override fun onViewportChange(isInViewport: Boolean) {
    this.isInViewport = isInViewport
    tabs.selectedInfo?.onViewportChange(isInViewport)
  }

  private fun TabInfo.onViewportChange(isInViewport: Boolean) {
    (component as NotebookInlayState?)?.onViewportChange(isInViewport)
  }

  private fun NotebookInlayState.setupOnHeightCalculated() {
    onHeightCalculated = {
      tabs.findInfo(this)?.let { tab ->
        updateMaxHeight(it + tabs.getTabLabel(tab).preferredSize.height)
      }
    }
  }

  private fun NotebookInlayState.addTab(inlayOutput: InlayOutput) {
    addTab(TabInfo(this).apply {
      inlayOutput.preview?.let {
        setIcon(it)
        text = ""
      }
      inlayOutput.title?.let {
        text = inlayOutput.title
      }
    }).apply {
      tabs.myInfo2Label[this]?.apply {
        if (inlayOutput.preferredWidth != 0) {
          preferredSize = Dimension(inlayOutput.preferredWidth, 0)
        }
      }
      if (tabs.selectedInfo == null) {
        tabs.select(this, false)
      }
    }
  }

  private fun addTab(tabInfo: TabInfo, select: Boolean = false): TabInfo {
    // We need to set empty DefaultActionGroup to move sideComponent to the right.
    tabInfo.setActions(DefaultActionGroup(), ActionPlaces.UNKNOWN)
    tabInfo.sideComponent = createTabToolbar(tabInfo)
    tabs.addTab(tabInfo)
    if (select) {
      tabs.select(tabInfo, false)
    }
    return tabInfo
  }

  private fun createTabToolbar(tabInfo: TabInfo): JComponent {
    val actionGroups = createTabActionGroups(tabInfo)
    val toolbar = ToolbarUtil.createActionToolbar(actionGroups)
    if (toolbar is ActionToolbarImpl) {
      toolbar.setForceMinimumSize(true)
    }
    return JPanel().apply {  // Align toolbar to top
      add(toolbar)
    }
  }

  private fun createTabActionGroups(tabInfo: TabInfo): List<List<AnAction>> {
    return mutableListOf<List<AnAction>>().also { groups ->
      (tabInfo.component as? ToolBarProvider)?.let { provider ->
        groups.add(provider.createActions())
      }
      groups.add(listOf(createClearAction()))
    }
  }

  private fun createClearAction(): AnAction {
    return ToolbarUtil.createAnActionButton("org.intellij.datavis.r.inlays.components.ClearOutputAction", clearAction::invoke)
  }

  private fun updateMaxHeight(height: Int) {
    if (maxHeight < height) {
      maxHeight = height
      onHeightCalculated?.invoke(maxHeight)
    }
  }
}
