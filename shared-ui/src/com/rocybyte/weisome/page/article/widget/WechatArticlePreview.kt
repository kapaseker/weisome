package com.rocybyte.weisome.page.article.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownDocument
import com.rocybyte.weisome.article.MarkdownInline
import com.rocybyte.weisome.article.WeiSomeLightCodeTheme

/** Renders a structured Markdown document using the WeChat preview styles. */
@Composable
internal fun WechatArticlePreview(document: MarkdownDocument, modifier: Modifier = Modifier) {
    Column(modifier) {
        document.blocks.forEach { block ->
            when (block) {
                is MarkdownBlock.Heading -> InlineMarkdownText(
                    lines = listOf(block.content),
                    fontSize = WechatArticlePreviewStyles.headingFontSize(block.level),
                    fontWeight = FontWeight.Bold,
                    lineHeight = WechatArticlePreviewStyles.headingFontSize(block.level) * 1.4f,
                    modifier = Modifier.padding(
                        top = WechatArticlePreviewStyles.headingTopMargin(block.level),
                        bottom = WechatArticlePreviewStyles.headingBottomMargin(block.level),
                    ),
                )
                is MarkdownBlock.Paragraph -> InlineMarkdownText(
                    lines = block.lines,
                    fontSize = WechatArticlePreviewStyles.bodyFontSize,
                    lineHeight = WechatArticlePreviewStyles.bodyLineHeight,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
                is MarkdownBlock.ListBlock -> Column(Modifier.padding(bottom = 16.dp, start = 24.dp)) {
                    block.items.forEachIndexed { index, item ->
                        Row {
                            Text(if (block.ordered) "${index + 1}." else "•")
                            Spacer(Modifier.width(8.dp))
                            InlineMarkdownText(
                                lines = listOf(item),
                                modifier = Modifier.weight(1f),
                                fontSize = WechatArticlePreviewStyles.bodyFontSize,
                                lineHeight = WechatArticlePreviewStyles.bodyLineHeight,
                            )
                        }
                    }
                }
                is MarkdownBlock.CodeBlock -> Text(
                    text = highlightedCodeText(block),
                    color = WeiSomeLightCodeTheme.codeRgb.toComposeColor(),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .background(WeiSomeLightCodeTheme.backgroundRgb.toComposeColor())
                        .padding(16.dp),
                )
            }
        }
    }
}

/** Builds styled Compose text from the same normalized spans used by HTML export. */
internal fun highlightedCodeText(block: MarkdownBlock.CodeBlock): AnnotatedString = buildAnnotatedString {
    append(block.code)
    block.highlights.forEach { span ->
        val start = span.start.coerceIn(0, block.code.length)
        val endExclusive = span.endExclusive.coerceIn(start, block.code.length)
        if (start < endExclusive) {
            addStyle(
                style = SpanStyle(color = span.foregroundRgb.toComposeColor()),
                start = start,
                end = endExclusive,
            )
        }
    }
}

/** Converts a packed RGB value to an opaque Compose color. */
private fun Int.toComposeColor(): Color = Color(0xFF000000L or (toLong() and 0xFFFFFFL))

/** Renders inline Markdown with rounded code labels that participate in text wrapping. */
@Composable
private fun InlineMarkdownText(
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

private object WechatArticlePreviewStyles {
    /** Returns font size and vertical margins for the requested heading level. */
    private fun heading(level: Int): Triple<Int, Int, Int> = when (level) {
        1 -> Triple(24, 24, 16)
        2 -> Triple(20, 20, 12)
        else -> Triple(18, 16, 8)
    }

    val bodyFontSize = 16.sp
    val bodyLineHeight = 28.sp
    /** Returns the configured font size for a heading level. */
    fun headingFontSize(level: Int) = heading(level).first.sp

    /** Returns the configured top margin for a heading level. */
    fun headingTopMargin(level: Int) = heading(level).second.dp

    /** Returns the configured bottom margin for a heading level. */
    fun headingBottomMargin(level: Int) = heading(level).third.dp
}
