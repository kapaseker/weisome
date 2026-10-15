package com.rocybyte.weisome.widget

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.rocybyte.weisome.ui.WeiSomeBorders
import com.rocybyte.weisome.ui.WeiSomeColors
import com.rocybyte.weisome.ui.WeiSomeShapes
import com.rocybyte.weisome.ui.WeiSomeSpacing
import com.rocybyte.weisome.ui.WeiSomeTypography
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlin.math.roundToInt

/**
 * Returns the minimal scroll offset bringing the [top]..[bottom] pixel span into the visible
 * window [scrollValue]..[scrollValue] + [viewport], or null when it is already fully visible.
 */
internal fun bringIntoViewScroll(top: Float, bottom: Float, scrollValue: Int, viewport: Int): Int? {
    if (viewport <= 0) return null
    return when {
        top < scrollValue -> top.roundToInt()
        bottom > scrollValue + viewport -> (bottom - viewport).roundToInt()
        else -> null
    }
}

/**
 * Runs a programmatic scroll while surviving supersession: a newer scroll session (usually a
 * user gesture on the same axis) cancels the in-flight one with [CancellationException]; that
 * must not kill the calling observer coroutine, so the cancellation is swallowed unless the
 * coroutine itself is stopping.
 */
internal suspend fun scrollSurvivingSupersession(scroll: suspend () -> Unit) {
    try {
        scroll()
    } catch (error: CancellationException) {
        currentCoroutineContext().ensureActive()
    }
}

/** 一次缩进插入或反缩进删除的空格数,也是全文唯一的水平缩进单位。 */
private const val INDENT_SPACES = 4

/** [INDENT_SPACES] 个空格构成的缩进单元。 */
private const val INDENT = "    "

/** 代码块围栏标记,与 CommonMark 一致。 */
private const val FENCE = "```"

/** 无序列表行前缀:跳过缩进后以 `- `、`* ` 或 `+ ` 开头。 */
private val UNORDERED_MARKER = Regex("^[-*+] ")

/** 有序列表行前缀:跳过缩进后以「数字 + `. `」开头。 */
private val ORDERED_MARKER = Regex("^\\d+\\. ")

/**
 * Returns the 0-based line index containing [offset], i.e. the number of line breaks before it.
 * An offset sitting on a line break belongs to the line that break terminates.
 */
private fun lineIndexAt(text: String, offset: Int): Int {
    var lines = 0
    for (index in 0 until offset.coerceIn(0, text.length)) {
        if (text[index] == '\n') lines++
    }
    return lines
}

/**
 * Returns the start offset of every line; a text ending with a line break yields a trailing empty
 * line whose start equals the text length.
 */
private fun lineStarts(text: String): List<Int> {
    val starts = mutableListOf(0)
    for (index in text.indices) {
        if (text[index] == '\n') starts += index + 1
    }
    return starts
}

/** Returns the index just past the last character of [line], excluding that line's line break. */
private fun lineEnd(text: String, starts: List<Int>, line: Int): Int =
    if (line < starts.lastIndex) starts[line + 1] - 1 else text.length

/** Returns the text of [line] without its trailing line break. */
private fun lineText(text: String, starts: List<Int>, line: Int): String =
    text.substring(starts[line], lineEnd(text, starts, line))

/**
 * Indents or unindents every line the current selection covers, returning the updated text and
 * selection.
 *
 * Affected lines run from the line of the selection start through the line of the selection end,
 * both included, so a selection whose end rests at column zero still indents that final line. A
 * collapsed selection therefore acts on the caret's single line. Indenting inserts [INDENT_SPACES]
 * spaces at each affected line start; unindenting removes up to [INDENT_SPACES] leading spaces from
 * each affected line and leaves lines without leading spaces untouched. The selection is remapped by
 * the inserted or removed character counts so it keeps covering the same content.
 */
internal fun applyLineIndent(value: TextFieldValue, indent: Boolean): TextFieldValue {
    val text = value.text
    val start = minOf(value.selection.start, value.selection.end)
    val end = maxOf(value.selection.start, value.selection.end)
    val starts = lineStarts(text)
    val affected = lineIndexAt(text, start)..lineIndexAt(text, end)

    if (indent) {
        val builder = StringBuilder(text)
        for (line in affected.reversed()) {
            builder.insert(starts[line], INDENT)
        }
        val newText = builder.toString()
        fun mapOffset(offset: Int): Int =
            (offset + INDENT_SPACES * affected.count { starts[it] <= offset })
                .coerceIn(0, newText.length)
        return TextFieldValue(newText, TextRange(mapOffset(start), mapOffset(end)))
    }

    val removals = IntArray(starts.size)
    for (line in affected) {
        var removed = 0
        while (removed < INDENT_SPACES &&
            starts[line] + removed < text.length &&
            text[starts[line] + removed] == ' '
        ) {
            removed++
        }
        removals[line] = removed
    }
    val prefix = IntArray(starts.size + 1)
    for (line in starts.indices) {
        prefix[line + 1] = prefix[line] + removals[line]
    }
    val builder = StringBuilder(text)
    for (line in affected.reversed()) {
        if (removals[line] > 0) builder.delete(starts[line], starts[line] + removals[line])
    }
    val newText = builder.toString()
    fun mapOffset(offset: Int): Int {
        val line = lineIndexAt(text, offset)
        val before = prefix[line]
        val mapped = if (offset >= starts[line] + removals[line]) {
            offset - before - removals[line]
        } else {
            starts[line] - before
        }
        return mapped.coerceIn(0, newText.length)
    }
    return TextFieldValue(newText, TextRange(mapOffset(start), mapOffset(end)))
}

/**
 * Expands every tab in [value] to [INDENT_SPACES] spaces and remaps the selection by the same
 * offsets. Returns [value] unchanged when it holds no tab so ordinary typing does not rebuild state.
 */
internal fun expandTabs(value: TextFieldValue): TextFieldValue {
    val text = value.text
    if ('\t' !in text) return value
    val expanded = text.replace("\t", INDENT)
    fun mapOffset(offset: Int): Int {
        var tabs = 0
        for (index in 0 until offset.coerceIn(0, text.length)) {
            if (text[index] == '\t') tabs++
        }
        return offset + tabs * (INDENT_SPACES - 1)
    }
    return TextFieldValue(expanded, TextRange(mapOffset(value.selection.start), mapOffset(value.selection.end)))
}

/**
 * Rebuilds [value] with the lines named by [replacements] swapped for their new text and remaps the
 * selection onto the rebuilt text. Lines absent from [replacements] keep their text.
 *
 * Every edit happens at a fixed column inside its own line, so an offset only shifts by the length
 * delta of the line it sits on; the deltas of earlier lines are already folded into the new line
 * starts. The shifted column is clamped into the new line so a caret beyond a shortened line's end
 * still lands inside it.
 */
internal fun rewriteLines(value: TextFieldValue, replacements: Map<Int, String>): TextFieldValue {
    if (replacements.isEmpty()) return value
    val text = value.text
    val starts = lineStarts(text)
    val lines = List(starts.size) { line -> lineText(text, starts, line) }
    val newLines = List(lines.size) { line -> replacements[line] ?: lines[line] }

    var cursor = 0
    val newStarts = IntArray(newLines.size) { line ->
        val start = cursor
        cursor += newLines[line].length + 1
        start
    }
    val newText = newLines.joinToString("\n")

    fun mapOffset(offset: Int): Int {
        val line = lineIndexAt(text, offset)
        val column = offset - starts[line] + newLines[line].length - lines[line].length
        return (newStarts[line] + column.coerceIn(0, newLines[line].length)).coerceIn(0, newText.length)
    }
    return TextFieldValue(
        newText,
        TextRange(mapOffset(value.selection.start), mapOffset(value.selection.end)),
    )
}

/**
 * Wraps the selection in [marker], or unwraps it when the selection is already wrapped.
 *
 * A collapsed selection inserts the pair and leaves the caret between them. A non-collapsed
 * selection is unwrapped when it carries the markers itself, or when the two markers sit directly
 * around it; otherwise the pair is inserted and the selection keeps covering the same text.
 */
internal fun toggleInlineMarker(value: TextFieldValue, marker: String): TextFieldValue {
    val text = value.text
    val start = minOf(value.selection.start, value.selection.end)
    val end = maxOf(value.selection.start, value.selection.end)
    val size = marker.length

    if (start == end) {
        val inserted = text.substring(0, start) + marker + marker + text.substring(start)
        return TextFieldValue(inserted, TextRange(start + size))
    }

    val selected = text.substring(start, end)
    if (selected.length >= size * 2 && selected.startsWith(marker) && selected.endsWith(marker)) {
        val inner = selected.substring(size, selected.length - size)
        val rebuilt = text.substring(0, start) + inner + text.substring(end)
        return TextFieldValue(rebuilt, TextRange(start, start + inner.length))
    }

    if (start >= size &&
        end + size <= text.length &&
        text.regionMatches(start - size, marker, 0, size) &&
        text.regionMatches(end, marker, 0, size)
    ) {
        val rebuilt = text.substring(0, start - size) + selected + text.substring(end + size)
        return TextFieldValue(rebuilt, TextRange(start - size, end - size))
    }

    val wrapped = text.substring(0, start) + marker + selected + marker + text.substring(end)
    return TextFieldValue(wrapped, TextRange(start + size, end + size))
}

/**
 * Wraps the lines the selection covers in a code fence, or removes the fence from the block an
 * already fenced selection belongs to. The selection is expanded to whole lines first, and
 * afterwards covers the fenced block's content without its fences.
 *
 * A block counts as fenced when the fences sit directly around the selection, which keeps a second
 * press of the shortcut unwrapping the result of the first, or when the selected lines start and end
 * with a fence. An empty line in the selection therefore yields a fence pair around an empty line
 * with the caret on that line, and pressing the shortcut again inside it removes both fences.
 */
internal fun toggleCodeFence(value: TextFieldValue): TextFieldValue {
    val text = value.text
    val start = minOf(value.selection.start, value.selection.end)
    val end = maxOf(value.selection.start, value.selection.end)
    val starts = lineStarts(text)
    val first = lineIndexAt(text, start)
    val last = lineIndexAt(text, end)
    val selectedLines = (first..last).map { line -> lineText(text, starts, line) }

    val before = if (first > 0) lineText(text, starts, first - 1) else null
    val after = if (last < starts.lastIndex) lineText(text, starts, last + 1) else null
    val outerFenced = before != null &&
        after != null &&
        before.trimStart().startsWith(FENCE) &&
        after.trim() == FENCE
    val innerFenced = selectedLines.size >= 2 &&
        selectedLines.first().trimStart().startsWith(FENCE) &&
        selectedLines.last().trim() == FENCE

    val from = if (outerFenced) first - 1 else if (innerFenced) first else -1
    if (from < 0) {
        val block = text.substring(starts[first], lineEnd(text, starts, last))
        val wrapped = FENCE + "\n" + block + "\n" + FENCE
        val contentStart = starts[first] + FENCE.length + 1
        val rebuilt = text.substring(0, starts[first]) + wrapped + text.substring(lineEnd(text, starts, last))
        return TextFieldValue(rebuilt, TextRange(contentStart, contentStart + block.length))
    }

    val to = if (outerFenced) last + 1 else last
    // 只保留围栏两行之间的内容,围栏所在两行连同其换行一并删除。
    val content = (from + 1 until to).joinToString("\n") { line -> lineText(text, starts, line) }
    val rebuilt = text.substring(0, starts[from]) + content + text.substring(lineEnd(text, starts, to))
    return TextFieldValue(rebuilt, TextRange(starts[from], starts[from] + content.length))
}

/**
 * Adds an unordered or ordered list prefix to every line the selection covers, or removes the
 * prefix from all of them when they already carry one.
 *
 * The prefix is written after each line's leading spaces so nested items indented with Tab stay
 * intact. Adding a prefix first drops a marker of the other kind, so switching between the two list
 * kinds on the same lines replaces the marker instead of nesting them, and ordered lines are
 * renumbered from one in selection order.
 */
internal fun toggleList(value: TextFieldValue, ordered: Boolean): TextFieldValue {
    val text = value.text
    val start = minOf(value.selection.start, value.selection.end)
    val end = maxOf(value.selection.start, value.selection.end)
    val starts = lineStarts(text)
    val first = lineIndexAt(text, start)
    val last = lineIndexAt(text, end)
    val lines = (first..last).map { line -> lineText(text, starts, line) }
    val marker = if (ordered) ORDERED_MARKER else UNORDERED_MARKER
    val prefixed = lines.all { marker.containsMatchIn(it.trimStart()) }

    val replacements = (first..last).associateWith { line ->
        val own = lines[line - first]
        val indent = own.takeWhile { it == ' ' }
        val body = own.trimStart()
        if (prefixed) {
            indent + marker.replaceFirst(body, "")
        } else {
            val bare = ORDERED_MARKER.replaceFirst(UNORDERED_MARKER.replaceFirst(body, ""), "")
            indent + if (ordered) "${line - first + 1}. $bare" else "- $bare"
        }
    }
    return rewriteLines(value, replacements)
}

/**
 * Returns the value [event] should produce for the Markdown formatting shortcut it triggers, or
 * null when the event is not a KeyDown for one of the supported combinations.
 *
 * Bold and italic take the primary modifier alone; code, code block, and both list kinds take it
 * together with Shift. The modifier set must match exactly: Alt, or any unlisted Shift state, makes
 * the event fall through so system and IME combinations keep working.
 */
private fun markdownShortcut(value: TextFieldValue, event: KeyEvent): TextFieldValue? {
    if (event.type != KeyEventType.KeyDown) return null
    if (!(event.isCtrlPressed || event.isMetaPressed) || event.isAltPressed) return null
    val shift = event.isShiftPressed
    return when {
        event.key == Key.B && !shift -> toggleInlineMarker(value, "**")
        event.key == Key.I && !shift -> toggleInlineMarker(value, "*")
        event.key == Key.K && shift -> toggleInlineMarker(value, "`")
        event.key == Key.C && shift -> toggleCodeFence(value)
        event.key == Key.U && shift -> toggleList(value, ordered = false)
        event.key == Key.O && shift -> toggleList(value, ordered = true)
        else -> null
    }
}

/**
 * Text field per the DESIGN.md input spec: light gray resting surface that turns white
 * on focus with a 2px primary border.
 *
 * @param scrollState when non-null, takes over the field scrolling: the text content lays out
 * at its full height and the supplied state scrolls it, enabling external scroll observation
 * and control, and the caret is automatically scrolled back into view whenever it leaves the
 * visible area. When null the field scrolls internally.
 * @param onTextLayout invoked with the text layout result whenever the text is re-laid-out.
 */
@Composable
internal fun WeiSomeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    minLines: Int = 1,
    scrollState: ScrollState? = null,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
) {
    var focused by remember { mutableStateOf(false) }
    val shape = WeiSomeShapes.default
    // The value overload is required to observe the caret: the String overload hides selection.
    var fieldValue by remember { mutableStateOf(TextFieldValue(value)) }
    if (value != fieldValue.text) fieldValue = TextFieldValue(value)
    var textLayout by remember { mutableStateOf<TextLayoutResult?>(null) }

    /** Scrolls the externally-controlled viewport so the caret or selection edges are visible. */
    LaunchedEffect(fieldValue.selection, textLayout) {
        val scroll = scrollState ?: return@LaunchedEffect
        val layout = textLayout ?: return@LaunchedEffect
        val target = bringIntoViewScroll(
            top = minOf(
                layout.getCursorRect(fieldValue.selection.start).top,
                layout.getCursorRect(fieldValue.selection.end).top,
            ),
            bottom = maxOf(
                layout.getCursorRect(fieldValue.selection.start).bottom,
                layout.getCursorRect(fieldValue.selection.end).bottom,
            ),
            scrollValue = scroll.value,
            viewport = scroll.viewportSize,
        ) ?: return@LaunchedEffect
        scrollSurvivingSupersession { scroll.scrollTo(target) }
    }

    // 高度由调用方决定（wrap-content 或 fillMax），不在内部 weight 撑满。
    BasicTextField(
        value = fieldValue,
        onValueChange = {
            val expanded = expandTabs(it)
            fieldValue = expanded
            onValueChange(expanded.text)
        },
        modifier = modifier
            .fillMaxWidth()
            // Tab / Shift+Tab 缩进当前选区覆盖的所有行;其余组合键交给 Markdown 格式化快捷键。
            // 两者都只在键位完全匹配时吞掉事件,避免误吞系统与输入法快捷键。
            .onPreviewKeyEvent { event ->
                when {
                    event.type == KeyEventType.KeyDown &&
                        event.key == Key.Tab &&
                        !event.isCtrlPressed &&
                        !event.isAltPressed &&
                        !event.isMetaPressed -> {
                        fieldValue = applyLineIndent(fieldValue, indent = !event.isShiftPressed)
                        onValueChange(fieldValue.text)
                        true
                    }

                    else -> markdownShortcut(fieldValue, event)?.let { shortcut ->
                        fieldValue = shortcut
                        onValueChange(shortcut.text)
                        true
                    } ?: false
                }
            }
            .onFocusChanged { focused = it.isFocused }
            .then(
                if (focused) {
                    Modifier.border(WeiSomeBorders.focusBorder, WeiSomeColors.primary, shape)
                } else {
                    Modifier.border(WeiSomeBorders.thin, WeiSomeColors.outlineVariant, shape)
                },
            )
            .background(
                if (focused) WeiSomeColors.surfaceContainerLowest else WeiSomeColors.surfaceContainerLow,
                shape,
            )
            .padding(horizontal = WeiSomeSpacing.controlPadding, vertical = WeiSomeSpacing.stackSmall)
            .then(
                if (scrollState != null) Modifier.verticalScroll(scrollState) else Modifier,
            ),
        textStyle = WeiSomeTypography.bodyMedium.copy(color = WeiSomeColors.onSurface),
        cursorBrush = SolidColor(WeiSomeColors.primary),
        minLines = minLines,
        onTextLayout = {
            textLayout = it
            onTextLayout?.invoke(it)
        },
        decorationBox = { innerTextField ->
            Box(Modifier.fillMaxSize()) {
                if (value.isEmpty() && placeholder != null) {
                    WeiSomeText(
                        text = placeholder,
                        style = WeiSomeTypography.bodyMedium,
                        color = WeiSomeColors.onSurfaceVariant.copy(alpha = 0.6f),
                    )
                }
                Box(Modifier.fillMaxSize()) { innerTextField() }
            }
        },
    )
}
