package com.rocybyte.weisome.page.article.widget

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.widget.WeiSomeText

/** Renders authored code lines without soft wrapping and exposes overflow through a local scrollbar. */
@Composable
internal fun CodeBlock(block: MarkdownBlock.CodeBlock) {
    val styles = LocalMarkdownPreviewStyles.current
    val codeTheme = LocalCodeTheme.current
    val horizontalScrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = styles.codeBlockTopMargin.dp, bottom = styles.codeBlockBottomMargin.dp)
            .background(codeTheme.backgroundRgb.toComposeColor(), styles.codeBlockCornerShape),
    ) {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val minimumTextWidth = (
                maxWidth -
                    (styles.codeBlockPaddingHorizontal * 2).dp
                ).coerceAtLeast(0.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(horizontalScrollState)
                    .padding(
                        top = styles.codeBlockPaddingVertical.dp,
                        bottom = styles.codeBlockPaddingVertical.dp,
                        start = styles.codeBlockPaddingHorizontal.dp,
                        end = styles.codeBlockPaddingHorizontal.dp,
                    ),
            ) {
                WeiSomeText(
                    text = highlightedCodeText(block),
                    color = codeTheme.codeRgb.toComposeColor(),
                    fontFamily = FontFamily.Monospace,
                    fontSize = styles.codeBlockFontSize,
                    lineHeight = styles.codeBlockLineHeight,
                    softWrap = false,
                    modifier = Modifier.widthIn(min = minimumTextWidth),
                )
            }
        }
        if (horizontalScrollState.maxValue > 0) {
            HorizontalScrollbar(
                adapter = rememberScrollbarAdapter(horizontalScrollState),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(top = 8.dp)
                    .height(8.dp),
            )
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
