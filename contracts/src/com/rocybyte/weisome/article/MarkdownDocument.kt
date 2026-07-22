package com.rocybyte.weisome.article

/** A presentation-neutral Markdown document used by the article preview and renderer. */
data class MarkdownDocument(val blocks: List<MarkdownBlock>)

sealed interface MarkdownBlock {
    data class Heading(val level: Int, val content: List<MarkdownInline>) : MarkdownBlock
    data class Paragraph(val lines: List<List<MarkdownInline>>) : MarkdownBlock
    data class ListBlock(val ordered: Boolean, val items: List<List<MarkdownInline>>) : MarkdownBlock
}

sealed interface MarkdownInline {
    data class Text(val value: String) : MarkdownInline
    data class Bold(val value: String) : MarkdownInline
    data class Italic(val value: String) : MarkdownInline
}
