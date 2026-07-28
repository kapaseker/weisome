package com.rocybyte.weisome.page.article.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rocybyte.weisome.article.MarkdownBlock

/** Renders an ordered or unordered list block with inline Markdown item content. */
@Composable
internal fun ListBlock(block: MarkdownBlock.ListBlock) {
    Column(Modifier.padding(bottom = 16.dp, start = 24.dp)) {
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
}
