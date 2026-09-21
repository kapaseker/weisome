package com.rocybyte.weisome.page.article.widget

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rocybyte.weisome.article.MarkdownBlock

/** Renders a paragraph block with hydrogen body typography and the first-letter rule. */
@Composable
internal fun Paragraph(block: MarkdownBlock.Paragraph, inQuote: Boolean = false) {
    val lines = block.lines.mapIndexed { index, line ->
        if (index == 0) capitalizeFirstLetter(line) else line
    }
    val color = if (inQuote) WechatArticlePreviewStyles.quoteColor else WechatArticlePreviewStyles.bodyColor
    InlineMarkdownText(
        lines = lines,
        fontSize = WechatArticlePreviewStyles.bodyFontSize,
        lineHeight = WechatArticlePreviewStyles.bodyLineHeight,
        color = color,
        modifier = if (inQuote) {
            Modifier.padding(top = 10.dp, bottom = 10.dp)
        } else {
            Modifier.padding(top = 22.dp, bottom = 22.dp)
        },
    )
}
