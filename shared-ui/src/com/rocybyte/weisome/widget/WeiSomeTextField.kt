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
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.rocybyte.weisome.ui.WeiSomeBorders
import com.rocybyte.weisome.ui.WeiSomeColors
import com.rocybyte.weisome.ui.WeiSomeShapes
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
            fieldValue = it
            onValueChange(it.text)
        },
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focused = it.isFocused }
            .then(
                if (focused) {
                    Modifier.border(2.dp, WeiSomeColors.primary, shape)
                } else {
                    Modifier.border(WeiSomeBorders.thin, WeiSomeColors.outlineVariant, shape)
                },
            )
            .background(
                if (focused) WeiSomeColors.surfaceContainerLowest else WeiSomeColors.surfaceContainerLow,
                shape,
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .then(
                if (scrollState != null) Modifier.verticalScroll(scrollState) else Modifier,
            ),
        textStyle = WeiSomeTypography.bodyMd.copy(color = WeiSomeColors.onSurface),
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
                        style = WeiSomeTypography.bodyMd,
                        color = WeiSomeColors.onSurfaceVariant.copy(alpha = 0.6f),
                    )
                }
                Box(Modifier.fillMaxSize()) { innerTextField() }
            }
        },
    )
}
