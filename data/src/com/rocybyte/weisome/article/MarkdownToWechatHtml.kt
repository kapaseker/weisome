package com.rocybyte.weisome.article

import com.rocybyte.weisome.article.html.renderCodeBlock
import com.rocybyte.weisome.article.html.renderHeading
import com.rocybyte.weisome.article.html.renderListBlock
import com.rocybyte.weisome.article.html.renderParagraph

object MarkdownToWechatHtml {
    /** Converts Markdown source into clipboard-ready WeChat HTML. */
    fun render(markdown: String): String = render(MarkdownDocumentParser.parse(markdown))

    /** Renders structured document blocks as styled HTML elements. */
    internal fun render(document: MarkdownDocument): String = document.blocks.joinToString("\n") { block ->
        when (block) {
            is MarkdownBlock.Heading -> renderHeading(block)
            is MarkdownBlock.Paragraph -> renderParagraph(block)
            is MarkdownBlock.ListBlock -> renderListBlock(block)
            is MarkdownBlock.CodeBlock -> renderCodeBlock(block)
        }
    }
}
