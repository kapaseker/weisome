package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.MarkdownBlock

/** Renders one block, applying quote-context overrides when nested inside a blockquote. */
internal fun renderBlock(block: MarkdownBlock, inQuote: Boolean): String = when (block) {
    is MarkdownBlock.Heading -> renderHeading(block)
    is MarkdownBlock.Paragraph -> renderParagraph(block, inQuote)
    is MarkdownBlock.ListBlock -> renderListBlock(block)
    is MarkdownBlock.CodeBlock -> renderCodeBlock(block)
    is MarkdownBlock.BlockQuote -> renderBlockQuote(block, inQuote)
    is MarkdownBlock.HorizontalRule -> renderHorizontalRule()
    is MarkdownBlock.Table -> renderTable(block)
}
