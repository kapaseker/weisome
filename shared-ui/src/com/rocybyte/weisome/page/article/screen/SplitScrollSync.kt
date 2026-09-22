package com.rocybyte.weisome.page.article.screen

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.text.TextLayoutResult
import com.rocybyte.weisome.widget.scrollSurvivingSupersession
import kotlin.math.roundToInt

/**
 * Returns the index of the first range containing [offset], or -1 when no range covers it.
 * Ranges are the inclusive source-text character offsets recorded per top-level block.
 */
internal fun blockIndexForOffset(ranges: List<IntRange>, offset: Int): Int =
    ranges.indexOfFirst { offset in it }

/**
 * Returns the index of the topmost block visible at [scrollValue]: the block whose recorded
 * top offset is the largest one not exceeding the scroll position (with a 1px tolerance), or
 * -1 when no block qualifies.
 */
internal fun topBlockIndexForScroll(tops: Map<Int, Float>, scrollValue: Float): Int {
    var bestIndex = -1
    var bestTop = Float.NEGATIVE_INFINITY
    for ((index, top) in tops) {
        if (top <= scrollValue + 1f && top > bestTop) {
            bestIndex = index
            bestTop = top
        }
    }
    return bestIndex
}

/**
 * Keeps the split-view editor and preview panes aligned while scrolling: whichever side the
 * user scrolls, the other side scrolls to the corresponding top-level Markdown block.
 *
 * Alignment is block-granular: scrolling inside a long block snaps the other pane to that
 * block's top. ponytail: 块内不插值 — 升级路径：同时记录块底偏移，按块内源行比例对像素插值。
 *
 * Feedback loops are prevented by writing the exact target value of every programmatic scroll
 * into a marker BEFORE the (suspending) scroll runs, so the other side's observer always sees
 * the marker already in place when the value change arrives and skips it; only user-driven
 * changes propagate. Each scroll also survives supersession (see [scrollSurvivingSupersession])
 * so a user gesture cancelling an in-flight programmatic scroll never kills the observers.
 */
@Composable
internal fun SplitScrollSync(
    editorScroll: ScrollState,
    previewScroll: ScrollState,
    textLayout: State<TextLayoutResult?>,
    blockRanges: List<IntRange>,
    blockTops: Map<Int, Float>,
) {
    var lastEditorTarget by remember { mutableIntStateOf(Int.MIN_VALUE) }
    var lastPreviewTarget by remember { mutableIntStateOf(Int.MIN_VALUE) }
    // Remembered-updated so the long-lived collect lambdas always observe the latest values
    // even though blockRanges is a fresh list on every re-parse.
    val currentRanges by rememberUpdatedState(blockRanges)
    val currentTops by rememberUpdatedState(blockTops)

    /** Scrolls [previewScroll] so the block containing the editor's top visible line is at top. */
    LaunchedEffect(editorScroll, previewScroll) {
        snapshotFlow { editorScroll.value }.collect { value ->
            if (value == lastEditorTarget) return@collect
            val layout = textLayout.value ?: return@collect
            if (currentRanges.isEmpty() || currentTops.isEmpty()) return@collect
            val topLine = layout.multiParagraph.getLineForVerticalPosition(value.toFloat())
            val blockIndex = blockIndexForOffset(currentRanges, layout.multiParagraph.getLineStart(topLine))
            if (blockIndex >= 0) {
                val target = currentTops[blockIndex]?.roundToInt() ?: return@collect
                if (previewScroll.value != target) {
                    lastPreviewTarget = target
                    scrollSurvivingSupersession { previewScroll.scrollTo(target) }
                }
            }
        }
    }

    /** Scrolls [editorScroll] so the source line of the preview's top visible block is at top. */
    LaunchedEffect(editorScroll, previewScroll) {
        snapshotFlow { previewScroll.value }.collect { value ->
            if (value == lastPreviewTarget) return@collect
            val layout = textLayout.value ?: return@collect
            if (currentRanges.isEmpty() || currentTops.isEmpty()) return@collect
            val blockIndex = topBlockIndexForScroll(currentTops, value.toFloat())
            if (blockIndex in currentRanges.indices) {
                val visualLine = layout.multiParagraph.getLineForOffset(currentRanges[blockIndex].first)
                val target = layout.multiParagraph.getLineTop(visualLine).roundToInt()
                if (editorScroll.value != target) {
                    lastEditorTarget = target
                    scrollSurvivingSupersession { editorScroll.scrollTo(target) }
                }
            }
        }
    }
}
