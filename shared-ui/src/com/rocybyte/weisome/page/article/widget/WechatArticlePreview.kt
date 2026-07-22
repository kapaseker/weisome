package com.rocybyte.weisome.page.article.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownDocument
import com.rocybyte.weisome.article.MarkdownInline

/** Renders a structured Markdown document using the WeChat preview styles. */
@Composable
internal fun WechatArticlePreview(document: MarkdownDocument, modifier: Modifier = Modifier) {
    Column(modifier) {
        document.blocks.forEach { block ->
            when (block) {
                is MarkdownBlock.Heading -> Text(
                    inlineText(block.content),
                    fontSize = WechatArticlePreviewStyles.headingFontSize(block.level),
                    fontWeight = FontWeight.Bold,
                    lineHeight = WechatArticlePreviewStyles.headingFontSize(block.level) * 1.4f,
                    modifier = Modifier.padding(
                        top = WechatArticlePreviewStyles.headingTopMargin(block.level),
                        bottom = WechatArticlePreviewStyles.headingBottomMargin(block.level),
                    ),
                )
                is MarkdownBlock.Paragraph -> Text(
                    block.lines.fold(AnnotatedString("")) { text, line ->
                        if (text.isEmpty()) inlineText(line) else text + AnnotatedString("\n") + inlineText(line)
                    },
                    fontSize = WechatArticlePreviewStyles.bodyFontSize,
                    lineHeight = WechatArticlePreviewStyles.bodyLineHeight,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
                is MarkdownBlock.ListBlock -> Column(Modifier.padding(bottom = 16.dp, start = 24.dp)) {
                    block.items.forEachIndexed { index, item ->
                        Row {
                            Text(if (block.ordered) "${index + 1}." else "•")
                            Spacer(Modifier.width(8.dp))
                            Text(
                                inlineText(item),
                                modifier = Modifier.weight(1f),
                                fontSize = WechatArticlePreviewStyles.bodyFontSize,
                                lineHeight = WechatArticlePreviewStyles.bodyLineHeight,
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Converts inline Markdown spans into styled Compose text. */
private fun inlineText(inlines: List<MarkdownInline>): AnnotatedString = buildAnnotatedString {
    inlines.forEach { inline ->
        when (inline) {
            is MarkdownInline.Text -> append(inline.value)
            is MarkdownInline.Bold -> withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(inline.value) }
            is MarkdownInline.Italic -> withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append(inline.value) }
        }
    }
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
