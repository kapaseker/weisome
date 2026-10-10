package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.CodeTheme
import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownExportStyles

/** Renders one block, applying quote-context overrides when nested inside a blockquote. */
internal fun renderBlock(
    block: MarkdownBlock,
    inQuote: Boolean,
    styles: MarkdownExportStyles,
    codeTheme: CodeTheme,
    prev: MarkdownBlock? = null,
    atDocumentStart: Boolean = false,
): String =
    when (block) {
        is MarkdownBlock.Heading -> renderHeading(block, styles, atDocumentStart)
        // The lead rule only fires for a paragraph directly after a top-level h1 (claudette's `h1 + p`).
        is MarkdownBlock.Paragraph ->
            renderParagraph(block, inQuote, isLead = !inQuote && prev is MarkdownBlock.Heading && prev.level == 1, styles = styles)
        is MarkdownBlock.ListBlock -> renderListBlock(block, styles)
        is MarkdownBlock.CodeBlock -> renderCodeBlock(block, styles, codeTheme)
        is MarkdownBlock.BlockQuote -> renderBlockQuote(block, inQuote, styles, codeTheme)
        is MarkdownBlock.HorizontalRule -> renderHorizontalRule(styles)
        is MarkdownBlock.Table -> renderTable(block, styles)
    }
