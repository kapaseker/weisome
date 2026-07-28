package com.rocybyte.weisome.page.article.widget

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.rocybyte.weisome.article.MarkdownBlock

/** Renders a heading block with the typography and margins for its normalized level. */
@Composable
internal fun Heading(block: MarkdownBlock.Heading) {
    InlineMarkdownText(
        lines = listOf(block.content),
        fontSize = WechatArticlePreviewStyles.headingFontSize(block.level),
        fontWeight = FontWeight.Bold,
        lineHeight = WechatArticlePreviewStyles.headingFontSize(block.level) * 1.4f,
        modifier = Modifier.padding(
            top = WechatArticlePreviewStyles.headingTopMargin(block.level),
            bottom = WechatArticlePreviewStyles.headingBottomMargin(block.level),
        ),
    )
}
