package com.rocybyte.weisome.page.article.widget

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rocybyte.weisome.article.MarkdownBlock

/** Renders a paragraph block with shared body typography and trailing spacing. */
@Composable
internal fun Paragraph(block: MarkdownBlock.Paragraph) {
    InlineMarkdownText(
        lines = block.lines,
        fontSize = WechatArticlePreviewStyles.bodyFontSize,
        lineHeight = WechatArticlePreviewStyles.bodyLineHeight,
        modifier = Modifier.padding(bottom = 16.dp),
    )
}
