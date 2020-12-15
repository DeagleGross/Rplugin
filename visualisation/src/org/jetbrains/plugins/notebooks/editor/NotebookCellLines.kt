package org.jetbrains.plugins.notebooks.editor

import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiDocumentManager
import com.intellij.util.EventDispatcher
import org.jetbrains.annotations.TestOnly
import java.util.*

/**
 * Incrementally iterates over Notebook document, calculates line ranges of cells using lexer.
 * Fast enough for running in EDT, but could be used in any other thread.
 *
 * Note: there's a difference between this model and the PSI model.
 * If a document starts not with a cell marker, this class treat the text before the first cell marker as a raw cell.
 * PSI model treats such cell as a special "stem" cell which is not a Jupyter cell at all.
 * We haven't decided which model is correct and which should be fixed. So, for now avoid using stem cells in tests,
 * while UI of PyCharm DS doesn't allow to create a stem cell at all.
 */
interface NotebookCellLines{
  enum class CellType {
    CODE, MARKDOWN, RAW
  }

  data class Marker(
    val ordinal: Int,
    val type: CellType,
    val offset: Int,
    val length: Int
  ) : Comparable<Marker> {
    override fun compareTo(other: Marker): Int = offset - other.offset
  }

  data class Interval(
    val ordinal: Int,
    val type: CellType,
    val lines: IntRange
  ) : Comparable<Interval> {
    override fun compareTo(other: Interval): Int = lines.first - other.lines.first
  }

  interface IntervalListener : EventListener {
    /**
     * Called when amount of intervals is changed, or types of some intervals are changed,
     * or line counts of intervals is changed.
     *
     * Intervals that were just shifted are included neither [oldIntervals], nor in [newIntervals].
     *
     * Intervals in the lists are valid only until the document changes. Check their validity
     * when postponing handling of intervals.
     *
     * It is guaranteed that:
     * * At least one of [oldIntervals] and [newIntervals] is not empty.
     * * Ordinals from every list defines an arithmetical progression where
     *   every next element has ordinal of the previous element incremented by one.
     * * If both lists are not empty, the first elements of both lists has the same ordinal.
     * * Both lists don't contain any interval that has been only shifted, shrank or enlarged.
     *
     * See `NotebookCellLinesTest` for examples of calls for various changes.
     */
    fun segmentChanged(oldIntervals: List<Interval>, newIntervals: List<Interval>)
  }

  fun getIterator(ordinal: Int): ListIterator<Interval>

  fun getIterator(interval: Interval): ListIterator<Interval>

  fun markersIterator(startOffset: Int = 0): ListIterator<Marker>

  fun intervalsIterator(startLine: Int = 0): ListIterator<Interval>

  val intervalListeners: EventDispatcher<IntervalListener>

  val modificationStamp: Long

  companion object {
    fun get(editor: Editor): NotebookCellLines {
      val psiDocumentManager = PsiDocumentManager.getInstance(editor.project!!)
      val document = editor.document
      val psiFile = psiDocumentManager.getPsiFile(document) ?: error("document ${document} doesn't have PSI file")
      return NotebookCellLinesProvider.forLanguage(psiFile.language).create(document)
    }

    /** It's uneasy to change a registry value inside tests. */   // TODO Lies! See SshX11ForwardingTest.
    @TestOnly
    var overriddenBinarySearchThreshold: Int? = null
  }
}