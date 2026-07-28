package com.rocybyte.weisome.page.article.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.rocybyte.weisome.article.MarkdownInline
import com.rocybyte.weisome.article.WeiSomeLightCodeTheme

/** Renders inline Markdown with rounded code labels that participate in text wrapping. */
@Composable
internal fun InlineMarkdownText(
    lines: List<List<MarkdownInline>>,
    fontSize: TextUnit,
    lineHeight: TextUnit,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight? = null,
) {
    BoxWithConstraints(modifier) {
        val textMeasurer = rememberTextMeasurer()
        val density = LocalDensity.current
        val horizontalPadding = 4.dp
        val verticalPadding = 2.dp
        val codeStyle = LocalTextStyle.current.merge(
            TextStyle(
                color = WeiSomeLightCodeTheme.codeRgb.toComposeColor(),
                fontSize = fontSize,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal,
            ),
        )
        val maxContentWidth = with(density) {
            (maxWidth.toPx() - horizontalPadding.toPx() * 2f).coerceAtLeast(1f)
        }
        val inlineContent = mutableMapOf<String, InlineTextContent>()
        var codeIndex = 0
        val text = buildAnnotatedString {
            lines.forEachIndexed { lineIndex, inlines ->
                if (lineIndex > 0) append('\n')
                inlines.forEach { inline ->
                    when (inline) {
                        is MarkdownInline.Text -> append(inline.value)
                        is MarkdownInline.Bold -> withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(inline.value)
                        }
                        is MarkdownInline.Italic -> withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                            append(inline.value)
                        }
                        is MarkdownInline.Code -> {
                            val chunks = inlineCodeChunks(inline.value, maxContentWidth) { value ->
                                textMeasurer.measure(value, codeStyle, maxLines = 1).size.width.toFloat()
                            }
                            chunks.forEach { chunk ->
                                val id = "inline-code-${codeIndex++}"
                                val measuredSize = textMeasurer.measure(chunk, codeStyle, maxLines = 1).size
                                val placeholderWidth = with(density) {
                                    (measuredSize.width + horizontalPadding.roundToPx() * 2).toSp()
                                }
                                val placeholderHeight = with(density) {
                                    (measuredSize.height + verticalPadding.roundToPx() * 2).toSp()
                                }
                                inlineContent[id] = InlineTextContent(
                                    placeholder = Placeholder(
                                        width = placeholderWidth,
                                        height = placeholderHeight,
                                        placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter,
                                    ),
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize()
                                            .background(
                                                WeiSomeLightCodeTheme.backgroundRgb.toComposeColor(),
                                                RoundedCornerShape(4.dp),
                                            )
                                            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
                                        contentAlignment = Alignment.CenterStart,
                                    ) {
                                        Text(
                                            text = chunk,
                                            style = codeStyle,
                                            maxLines = 1,
                                        )
                                    }
                                }
                                appendInlineContent(id, chunk)
                            }
                        }
                    }
                }
            }
        }
        Text(
            text = text,
            inlineContent = inlineContent,
            fontSize = fontSize,
            lineHeight = lineHeight,
            fontWeight = fontWeight,
        )
    }
}

/** Splits only overlong code labels so ordinary labels remain atomic at line boundaries. */
internal fun inlineCodeChunks(
    value: String,
    maxContentWidth: Float,
    measureWidth: (String) -> Float,
): List<String> {
    if (value.isEmpty() || measureWidth(value) <= maxContentWidth) return listOf(value)
    val chunks = mutableListOf<String>()
    var start = 0
    while (start < value.length) {
        var low = start + 1
        var high = value.length
        var bestEnd = low
        while (low <= high) {
            val middle = (low + high) ushr 1
            if (measureWidth(value.substring(start, middle)) <= maxContentWidth) {
                bestEnd = middle
                low = middle + 1
            } else {
                high = middle - 1
            }
        }
        if (bestEnd < value.length && value[bestEnd - 1].isHighSurrogate() && value[bestEnd].isLowSurrogate()) {
            bestEnd = if (bestEnd == start + 1) bestEnd + 1 else bestEnd - 1
        }
        chunks += value.substring(start, bestEnd)
        start = bestEnd
    }
    return chunks
}
