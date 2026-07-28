package com.rocybyte.weisome.page.article.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownDocument

/** Renders a structured Markdown document using the WeChat preview styles. */
@Composable
internal fun WechatArticlePreview(document: MarkdownDocument, modifier: Modifier = Modifier) {
    Column(modifier) {
        document.blocks.forEach { block ->
            when (block) {
                is MarkdownBlock.Heading -> Heading(block)
                is MarkdownBlock.Paragraph -> Paragraph(block)
                is MarkdownBlock.ListBlock -> ListBlock(block)
                is MarkdownBlock.CodeBlock -> CodeBlock(block)
            }
        }
    }
}
