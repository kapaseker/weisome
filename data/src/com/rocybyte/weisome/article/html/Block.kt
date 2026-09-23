package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownExportStyles

/** Renders one block, applying quote-context overrides when nested inside a blockquote. */
internal fun renderBlock(block: MarkdownBlock, inQuote: Boolean, styles: MarkdownExportStyles): String =
    when (block) {
        is MarkdownBlock.Heading -> renderHeading(block, styles)
        is MarkdownBlock.Paragraph -> renderParagraph(block, inQuote, styles)
        is MarkdownBlock.ListBlock -> renderListBlock(block, styles)
        is MarkdownBlock.CodeBlock -> renderCodeBlock(block, styles)
        is MarkdownBlock.BlockQuote -> renderBlockQuote(block, inQuote, styles)
        is MarkdownBlock.HorizontalRule -> renderHorizontalRule(styles)
        is MarkdownBlock.Table -> renderTable(block, styles)
    }
