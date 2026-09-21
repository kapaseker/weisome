package com.rocybyte.weisome.article

/** A presentation-neutral Markdown document used by the article preview and renderer. */
data class MarkdownDocument(val blocks: List<MarkdownBlock>)

sealed interface MarkdownBlock {
    data class Heading(val level: Int, val content: List<MarkdownInline>) : MarkdownBlock
    data class Paragraph(val lines: List<List<MarkdownInline>>) : MarkdownBlock
    data class ListBlock(val ordered: Boolean, val items: List<ListItem>) : MarkdownBlock
    data class CodeBlock(
        val language: CodeLanguage?,
        val code: String,
        val highlights: List<CodeHighlightSpan> = emptyList(),
    ) : MarkdownBlock
    data class BlockQuote(val blocks: List<MarkdownBlock>) : MarkdownBlock
    data object HorizontalRule : MarkdownBlock
    data class Table(
        val header: List<List<MarkdownInline>>,
        val rows: List<List<List<MarkdownInline>>>,
    ) : MarkdownBlock
}

/** One list entry with optional GFM task state and an optional nested sub-list. */
data class ListItem(
    val content: List<MarkdownInline>,
    val task: Boolean? = null,
    val child: MarkdownBlock.ListBlock? = null,
)

enum class CodeLanguage {
    Java,
    Kotlin,
    Rust,
}

data class CodeHighlightSpan(
    val start: Int,
    val endExclusive: Int,
    val foregroundRgb: Int,
)

data class CodeTheme(
    val backgroundRgb: Int,
    val codeRgb: Int,
    val keywordRgb: Int,
    val stringRgb: Int,
    val literalRgb: Int,
    val commentRgb: Int,
    val metadataRgb: Int,
    val multilineCommentRgb: Int,
    val punctuationRgb: Int,
    val markRgb: Int,
)

val WeiSomeLightCodeTheme = CodeTheme(
    backgroundRgb = 0xF6F8FA,
    codeRgb = 0x24292F,
    keywordRgb = 0xCF222E,
    stringRgb = 0x0A3069,
    literalRgb = 0x0550AE,
    commentRgb = 0x6E7781,
    metadataRgb = 0x8250DF,
    multilineCommentRgb = 0x6E7781,
    punctuationRgb = 0x24292F,
    markRgb = 0x953800,
)

sealed interface MarkdownInline {
    data class Text(val value: String) : MarkdownInline
    data class Bold(val value: String) : MarkdownInline
    data class Italic(val value: String) : MarkdownInline
    data class Code(val value: String) : MarkdownInline
    data class Link(val text: String, val url: String) : MarkdownInline
    data class Strikethrough(val value: String) : MarkdownInline
    data class Image(val alt: String, val url: String) : MarkdownInline
}
