package com.rocybyte.weisome.ui

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

@Composable
internal fun WechatArticlePreview(document: MarkdownDocument, modifier: Modifier = Modifier) {
    Column(modifier) {
        document.blocks.forEach { block ->
            when (block) {
                is MarkdownBlock.Heading -> {
                    Text(
                        inlineText(block.content),
                        fontSize = WechatArticleStyles.headingFontSize(block.level),
                        fontWeight = WechatArticleStyles.headingWeight,
                        lineHeight = WechatArticleStyles.headingFontSize(block.level) * 1.4f,
                        modifier = Modifier.padding(top = WechatArticleStyles.headingTopMargin(block.level), bottom = WechatArticleStyles.headingBottomMargin(block.level)),
                    )
                }
                is MarkdownBlock.Paragraph -> Text(
                    block.lines.fold(AnnotatedString("")) { text, line -> if (text.isEmpty()) inlineText(line) else text + AnnotatedString("\n") + inlineText(line) },
                    fontSize = WechatArticleStyles.bodyFontSize,
                    lineHeight = WechatArticleStyles.bodyLineHeight,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
                is MarkdownBlock.ListBlock -> Column(Modifier.padding(bottom = 16.dp, start = 24.dp)) {
                    block.items.forEachIndexed { index, item -> Row {
                        Text(if (block.ordered) "${index + 1}." else "•")
                        Spacer(Modifier.width(8.dp))
                        Text(inlineText(item), modifier = Modifier.weight(1f), fontSize = WechatArticleStyles.bodyFontSize, lineHeight = WechatArticleStyles.bodyLineHeight)
                    } }
                }
            }
        }
    }
}

private fun inlineText(inlines: List<MarkdownInline>): AnnotatedString = buildAnnotatedString {
    inlines.forEach { inline -> when (inline) {
        is MarkdownInline.Text -> append(inline.value)
        is MarkdownInline.Bold -> withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(inline.value) }
        is MarkdownInline.Italic -> withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append(inline.value) }
    } }
}
