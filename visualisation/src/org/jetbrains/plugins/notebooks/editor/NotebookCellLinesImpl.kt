package org.jetbrains.plugins.notebooks.editor

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.diagnostic.Attachment
import com.intellij.openapi.diagnostic.RuntimeExceptionWithAttachments
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.diagnostic.trace
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.registry.Registry
import com.intellij.util.EventDispatcher
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.containers.addIfNotNull
import org.jetbrains.annotations.TestOnly
import org.jetbrains.concurrency.AsyncPromise
import org.jetbrains.concurrency.Promise
import org.jetbrains.plugins.notebooks.editor.NotebookCellLines.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min


class NotebookCellLinesImpl private constructor(private val document: Document,
                                                private val cellTypeAwareLexerProvider: NotebookCellLinesLexer) : NotebookCellLines {
  private val markerCache = mutableListOf<Marker>()
  private val intervalCache = mutableListOf<Interval>()

  override val intervalListeners = EventDispatcher.create(IntervalListener::class.java)

  override var modificationStamp: Long = 0
    private set

  override fun markersIterator(startOffset: Int): ListIterator<Marker> {
    ApplicationManager.getApplication().assertReadAccessAllowed()
    return markerCache.listIterator(getMarkerUpperBound(startOffset))
  }

  override fun intervalsIterator(startLine: Int): ListIterator<Interval> {
    ApplicationManager.getApplication().assertReadAccessAllowed()
    var fromIndex =
      if (intervalCache.size < BINARY_SEARCH_THRESHOLD) 0
      else Collections
        .binarySearch(intervalCache, Interval(-1, CellType.RAW, startLine..startLine))
        .let { if (it < 0) -it - 2 else it - 1 }
        .coerceAtLeast(0)
    while (fromIndex < intervalCache.size && intervalCache[fromIndex].lines.last < startLine) {
      ++fromIndex
    }
    return intervalCache.listIterator(fromIndex)
  }

  override fun getIterator(ordinal: Int): ListIterator<Interval> {
    return intervalCache.listIterator(ordinal)
  }

  override fun getIterator(interval: Interval): ListIterator<Interval> {
    check(interval == intervalCache[interval.ordinal])
    return intervalCache.listIterator(interval.ordinal)
  }

  private fun initialize(it: AsyncPromise<NotebookCellLines>, useDocumentListener: Boolean) {
    runReadAction {
      if (useDocumentListener)
        document.addDocumentListener(documentListener)

      initializeEmptyLists()
    }
    LOG.trace {
      commonErrorAttachments(null)
        .joinToString(separator = "\n", prefix = "NotebookCellLines has been initialised for ${document}\n", postfix = "=== end") {
          "=== ${it.name}:\n${it.displayText}\n"
        }
    }
    it.setResult(this)
  }

  private fun initializeEmptyLists() {
    wrapErrors(null) {
      markerCache.addAll(cellTypeAwareLexerProvider.markerSequence(document.immutableCharSequence, 0, 0))
      intervalCache.addAll(
        adjustedMarkers(markerCache, 0, markerCache, document.textLength).asSequence().zipWithNext(markersToInterval(document)))
    }
    checkIntegrity(null)
  }

  private fun shiftOffsetToMarkerStart(text: CharSequence, initialOffset: Int): Int {
    val interestingTextAbsoluteOffset = max(0, initialOffset - cellTypeAwareLexerProvider.longestTokenLength + 1)
    val interestingText = text.subSequence(
      interestingTextAbsoluteOffset,
      min(text.length, initialOffset + cellTypeAwareLexerProvider.longestTokenLength - 1)
    )
    return cellTypeAwareLexerProvider.markerSequence(interestingText, 0, interestingTextAbsoluteOffset)
             .firstOrNull()?.offset?.takeIf { it < initialOffset } ?: initialOffset
  }

  private val documentListener = object : DocumentListener {
    override fun documentChanged(event: DocumentEvent) {
      ApplicationManager.getApplication().assertWriteAccessAllowed()

      if (cellTypeAwareLexerProvider.shouldParseWholeFile()) {
        markerCache.clear()
        val oldIntervals = intervalCache.toList()
        intervalCache.clear()
        initializeEmptyLists()
        notifyChangedIntervals(oldIntervals, intervalCache.toList())
      }
      else {
        wrapErrors(event) {
          updateIntervals(
            updateMarkers(
              startOffset = event.offset,
              oldLength = event.oldLength,
              newLength = event.newLength,
            )
          )
        }
      }

      checkIntegrity(event)
    }
  }

  /**
   * @param firstIntervalOrdinal The start index of [intervalCache] where intervals should change.
   * @param markerSizeDiff Difference between new adjusted markers count and old adjusted markers.
   * @param adjustedNewMarkers New markers that also can contain additional bogus markers, not existing in [markerCache] but required
   *  for constricting intervals.
   */
  private class UpdateMarkersResult(
    val firstIntervalOrdinal: Int,
    val markerSizeDiff: Int,
    val adjustedNewMarkers: List<Marker>,
  )

  private fun updateMarkers(
    startOffset: Int,
    oldLength: Int,
    newLength: Int,
  ): UpdateMarkersResult {
    // TODO: the logic here seems to be too complex,
    //  it worth to try reparse the whole set of lines affected plus an additional line at the end
    // Reparse from line start to get the whole marker
    val startDocumentOffset = document.run {
      getLineStartOffset(getLineNumber(startOffset))
    }

    val markerCacheCutStart = getMarkerUpperBound(startDocumentOffset)

    // Also, the document change may cut half of a marker at the end. Detect that and widen the text slice if needed.
    val endOffsetIncrement =
      markerCache
        .getOrNull(getMarkerUpperBound(startOffset + oldLength))
        ?.takeIf {
          // -1 because the document is changed until previousEndOffset excluding the end.
          (startOffset + oldLength - it.offset - 1) in 0 until it.length
        }
        ?.let { startOffset + oldLength - it.offset + it.length }
      ?: 0

    val previousEndDocumentOffset = startOffset + oldLength + endOffsetIncrement
    val endDocumentOffset = startOffset + newLength + endOffsetIncrement

    val markerCacheCutEndExclusive =
      markerCache
        .subList(markerCacheCutStart, markerCache.size)
        .withIndex()
        .takeWhile { it.value.offset < previousEndDocumentOffset }
        .lastOrNull()
        ?.let { markerCacheCutStart + it.index + 1 }
      ?: markerCacheCutStart

    val adjustedOldMarkersSize =
      (
        // markerCache contains real markers, as seen by the lexer. Intervals are constructed by combining two adjacent markers.
        // A bogus interval at the start should be created in order to generate the interval above the first real marker.
        // However, the bogus marker isn't needed if there's a marker right at the document start.
        (if (markerCacheCutStart != 0 || markerCache.getOrNull(0)?.offset != 0) 1 else 0) +

        markerCacheCutEndExclusive - markerCacheCutStart

        // Intervals are constructed by combining two adjacent markers. A bogus interval at the end should be created in order to
        // generate the interval below the last marker.
        + 1
      )

    val endNewline = document.getText(TextRange(endDocumentOffset, min(endDocumentOffset + 1, document.textLength))) == "\n"
    val terminalNewlineSymbol = if (endNewline) 1 else 0

    val newMarkers = cellTypeAwareLexerProvider.markerSequence(
      chars = document.immutableCharSequence.subSequence(startDocumentOffset, endDocumentOffset + terminalNewlineSymbol),
      ordinalIncrement = markerCacheCutStart,
      offsetIncrement = startDocumentOffset,
    ).toList()

    val diff = newLength - oldLength
    if (newMarkers != markerCache.subList(markerCacheCutStart, markerCacheCutEndExclusive)) {
      ++modificationStamp

      markerCache.substitute(markerCacheCutStart, markerCacheCutEndExclusive, newMarkers)
      for (idx in markerCacheCutStart + newMarkers.size until markerCache.size) {
        markerCache[idx] = markerCache[idx].let { it.copy(offset = it.offset + diff, ordinal = idx) }
      }
    }
    else if (diff != 0 && markerCacheCutStart < markerCache.size) {
      // No markers changed, but some markers might have shifted. Shifting all markers after the document event offset
      // (not after the adjusted offset).
      val start = markerCacheCutStart + if (markerCache[markerCacheCutStart].offset < startOffset) 1 else 0
      for (index in start until markerCache.size) {
        markerCache[index] = markerCache[index].let { it.copy(offset = it.offset + diff) }
      }
    }

    val adjustedNewMarkers = adjustedMarkers(markerCache, markerCacheCutStart, newMarkers, document.textLength)
    return UpdateMarkersResult(
      firstIntervalOrdinal = max(0, markerCacheCutStart - if (markerCacheCutStart == 0 && markerCache.getOrNull(0)?.offset != 0) 0 else 1),
      markerSizeDiff = adjustedNewMarkers.size - adjustedOldMarkersSize,
      adjustedNewMarkers = adjustedNewMarkers,
    )
  }

  private fun updateIntervals(updateMarkersResult: UpdateMarkersResult) {
    val firstIntervalOrdinal = updateMarkersResult.firstIntervalOrdinal
    val markerSizeDiff = updateMarkersResult.markerSizeDiff
    val newMarkers = updateMarkersResult.adjustedNewMarkers

    val oldIntervals = intervalCache.subList(
      firstIntervalOrdinal,
      // Interval is an entity between two adjacent markers. Amount of intervals is always less than amount of *adjusted* markers by one.
      firstIntervalOrdinal + newMarkers.size - markerSizeDiff - 1,
    ).toList()
    val newIntervals = newMarkers.zipWithNext(markersToInterval(document))
    if (oldIntervals != newIntervals) {
      val lineDiff =
        (newIntervals.takeIf { it.isNotEmpty() }?.run { last().lines.last - first().lines.first + 1 } ?: 0) -
        (oldIntervals.takeIf { it.isNotEmpty() }?.run { last().lines.last - first().lines.first + 1 } ?: 0)

      intervalCache.substitute(firstIntervalOrdinal, firstIntervalOrdinal + oldIntervals.size, newIntervals)
      for (idx in firstIntervalOrdinal + newIntervals.size until intervalCache.size) {
        intervalCache[idx] = intervalCache[idx].let {
          it.copy(
            ordinal = idx,
            lines = (it.lines.first + lineDiff)..(it.lines.last + lineDiff),
          )
        }
      }

      // If one marker is just converted to another one, lists will be trimmed at left by one.
      notifyChangedIntervals(oldIntervals, newIntervals)
    }
  }

  private fun getMarkerUpperBound(offset: Int): Int {
    val startIndex =
      if (markerCache.size < BINARY_SEARCH_THRESHOLD) 0
      else Collections.binarySearch(markerCache, Marker(-1, CellType.RAW, offset, 0)).let { if (it < 0) -it - 1 else it }
    return markerCache
             .subList(startIndex, markerCache.size)
             .withIndex()
             .dropWhile { it.value.offset < offset }
             .firstOrNull()
             ?.let { it.index + startIndex }
           ?: markerCache.size
  }

  private fun notifyChangedIntervals(oldIntervals: List<Interval>, newIntervals: List<Interval>) {
    val (trimmedOld, trimmedNew) = trimLists(oldIntervals, newIntervals) { o, n ->
      o.type == n.type &&
      o.lines.last - o.lines.first == n.lines.last - n.lines.first
    }

    if (trimmedOld != trimmedNew) {
      LOG.trace {
        commonErrorAttachments(null)
          .joinToString(separator = "\n", prefix = "Segments in ${document} has changed\n", postfix = "=== end") {
            "=== ${it.name}:\n${it.displayText}\n"
          }
      }
      intervalListeners.multicaster.segmentChanged(trimmedOld, trimmedNew)
    }
  }

  private fun checkIntegrity(event: DocumentEvent?) {  // TODO It's expensive. Should be deleted later, or covered by a flag.
    val problems = mutableListOf<String>()
    for ((idx, marker) in markerCache.withIndex()) {
      if (marker.ordinal != idx)
        problems += "$marker: expected ordinal $idx"

      if (idx < markerCache.size - 1 && marker.offset + marker.length > markerCache[idx + 1].offset)
        problems += "$marker overlaps with ${markerCache[idx + 1]}"

      if (marker.offset < 0 || marker.offset + marker.length > document.textLength)
        problems += "$marker is out of document contents"

      if (marker.length < 0)
        problems += "$marker length is negative"
    }

    for ((idx, interval) in intervalCache.withIndex()) {
      if (interval.ordinal != idx)
        problems += "$interval: expected ordinal $idx"

      if (idx < intervalCache.size - 1 && interval.lines.last + 1 != intervalCache[idx + 1].lines.first)
        problems += "No junction between $interval and ${intervalCache[idx + 1]}"

      if (idx == 0 && interval.lines.first != 0)
        problems += "The first $interval is expected to start with line 0"

      if (idx == intervalCache.size - 1 && interval.lines.last + 1 != document.lineCount.coerceAtLeast(1))
        problems += "The last $interval is expected to end with line ${document.lineCount}"
    }

    if (problems.isNotEmpty()) {
      LOG.error(
        "Integrity failure: ${problems[0]}",
        Attachment("all_errors.txt", problems.joinToString("\n")),
        *commonErrorAttachments(event),
      )
    }
  }

  private fun commonErrorAttachments(event: DocumentEvent?): Array<Attachment> = listOfNotNull(
    Attachment("all_markers.txt", markerCache.withIndex().joinToString("\n") { (idx, marker) -> "$idx: $marker" })
      .apply { isIncluded = true },
    Attachment("all_intervals.txt", intervalCache.withIndex().joinToString("\n") { (idx, interval) -> "$idx: $interval" })
      .apply { isIncluded = true },
    Attachment("document.txt", document.text)
      .apply { isIncluded = false },
    event?.run {
      Attachment("event_old_fragment.txt", oldFragment.toString())
        .apply { isIncluded = false }
    },
    event?.run {
      Attachment("event_new_fragment.txt", oldFragment.toString())
        .apply { isIncluded = false }
    },
    event?.run {
      Attachment("event.txt", """
        Start offset: $offset
        Old length: $oldLength
        New length: $newLength
        """.trimIndent())
        .apply { isIncluded = true }
    },
  ).toTypedArray()

  private inline fun <T> wrapErrors(event: DocumentEvent?, handler: () -> T): T =
    try {
      handler()
    }
    catch (err: Throwable) {
      err.addSuppressed(RuntimeExceptionWithAttachments("", *commonErrorAttachments(event)))
      throw err
    }

  companion object {
    private val LOG = logger<NotebookCellLinesImpl>()
    private val map = ContainerUtil.createConcurrentWeakMap<Document, Promise<NotebookCellLines>>()

    // TODO Maybe get rid of the linear or binary search? It looks like an over-optimization.
    private val BINARY_SEARCH_THRESHOLD: Int
      get() =
        NotebookCellLines.overriddenBinarySearchThreshold
        ?: Registry.intValue("pycharm.ds.notebook.editor.ui.binary.search.threshold")

    fun get(document: Document, lexerProvider: NotebookCellLinesLexer): NotebookCellLines {
      val promise = AsyncPromise<NotebookCellLines>()
      val actualPromise = map.putIfAbsent(document, promise)
                          ?: promise.also { NotebookCellLinesImpl(document, lexerProvider).initialize(it, useDocumentListener = true) }
      return actualPromise.blockingGet(1, TimeUnit.MILLISECONDS)!!
    }

    @TestOnly
    fun getForSingleUsage(document: Document, lexerProvider: NotebookCellLinesLexer): NotebookCellLines {
      val promise = AsyncPromise<NotebookCellLines>()
      NotebookCellLinesImpl(document, lexerProvider).initialize(promise, useDocumentListener = false)
      return promise.blockingGet(1, TimeUnit.MILLISECONDS)!!
    }
  }
}

private fun <T> MutableList<T>.substitute(start: Int, end: Int, pattern: List<T>) {
  val sizeDiff = pattern.size + start - end
  when {
    sizeDiff == 0 -> {
      for ((idx, value) in pattern.withIndex()) {
        this[start + idx] = value
      }
    }
    sizeDiff > 0 -> {
      addAll(start, pattern.subList(0, sizeDiff))
      for (idx in sizeDiff until pattern.size) {
        this[start + idx] = pattern[idx]
      }
    }
    else -> {
      subList(start, start - sizeDiff).clear()
      for ((idx, value) in pattern.withIndex()) {
        this[start + idx] = value
      }
    }
  }
}

private fun adjustedMarkers(markers: List<Marker>,
                            startOrdinal: Int,
                            sublist: List<Marker>,
                            documentTextLength: Int): List<Marker> = mutableListOf<Marker>().also { result ->
  // markerCache contains real markers, as seen by the lexer. Intervals are constructed by combining two adjacent markers.
  // A bogus interval at the start should be created in order to generate the interval above the first real marker.
  // However, the bogus marker isn't needed if there's a marker right at the document start.
  val ephemeralStart =
    startOrdinal == 0 &&
    markers.getOrNull(0)?.offset != 0 &&
    sublist.firstOrNull().let { it?.ordinal != 0 || it.offset != 0 }
  result.addIfNotNull(
    when {
      ephemeralStart -> Marker(0, CellType.RAW, 0, 0)
      sublist.firstOrNull()?.ordinal == 0 -> null
      else -> markers.getOrNull(startOrdinal - 1)
    })

  val ordinalShift = result.firstOrNull()?.ordinal?.let { it + 1 } ?: 0
  for ((index, marker) in sublist.withIndex()) {
    result.add(marker.copy(ordinal = index + ordinalShift))
  }

  val lastOrdinal = (result.lastOrNull()?.ordinal ?: startOrdinal) + (if (ephemeralStart) 0 else 1)
  result.add(
    markers.getOrNull(lastOrdinal)
    // Intervals are constructed by combining two adjacent markers. A bogus interval at the end should be created in order to
    // generate the interval below the last marker.
    // +1 -- later it'll be decreased back while constructing the last interval.
    ?: Marker(lastOrdinal, CellType.RAW, documentTextLength + 1, 0)
  )
}

private fun markersToInterval(document: Document) = { a: Marker, b: Marker ->
  Interval(a.ordinal, a.type, document.getLineNumber(a.offset)..document.getLineNumber(max(0, b.offset - 1)))
}