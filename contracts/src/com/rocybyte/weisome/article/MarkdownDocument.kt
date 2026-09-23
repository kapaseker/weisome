package com.rocybyte.weisome.article

/**
 * A presentation-neutral Markdown document used by the article preview and renderer.
 *
 * @property blockRanges inclusive character-offset ranges into the newline-normalized source
 * text, one per top-level block in [blocks], used for split-view scroll synchronization.
 */
data class MarkdownDocument(
    val blocks: List<MarkdownBlock>,
    val blockRanges: List<IntRange> = emptyList(),
)

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

/** Selectable Markdown document themes affecting preview and export rendering; GITHUB is the default. */
enum class MarkdownThemeId {
    GITHUB,
    HYDROGEN,
}

/** Selectable code-block highlight themes; GITHUB_LIGHT is the default. */
enum class CodeThemeId {
    GITHUB_LIGHT,
    DARCULA,
    MONOKAI,
    NOTEPAD,
    MATRIX,
    PASTEL,
    ATOM_ONE,
}

/** Palette per [CodeThemeId]; all themes render on the fixed light background #f6f8fa. */
object CodeThemes {
    private val background = 0xF6F8FA

    /** Returns the palette for the given theme id. */
    fun forId(id: CodeThemeId): CodeTheme = when (id) {
        CodeThemeId.GITHUB_LIGHT -> githubLight
        CodeThemeId.DARCULA -> CodeTheme(
            backgroundRgb = background,
            codeRgb = 0x121212,
            keywordRgb = 0xCC7832,
            stringRgb = 0x6A8759,
            literalRgb = 0x6897BB,
            commentRgb = 0x909090,
            metadataRgb = 0xBBB529,
            multilineCommentRgb = 0x629755,
            punctuationRgb = 0xCC7832,
            markRgb = 0x121212,
        )
        CodeThemeId.MONOKAI -> CodeTheme(
            backgroundRgb = background,
            codeRgb = 0x07070D,
            keywordRgb = 0xF92672,
            stringRgb = 0xE6DB74,
            literalRgb = 0xAE81FF,
            commentRgb = 0xFD971F,
            metadataRgb = 0xB8F4B8,
            multilineCommentRgb = 0xFD971F,
            punctuationRgb = 0x07070D,
            markRgb = 0x07070D,
        )
        CodeThemeId.NOTEPAD -> CodeTheme(
            backgroundRgb = background,
            codeRgb = 0x000080,
            keywordRgb = 0x0000FF,
            stringRgb = 0x808080,
            literalRgb = 0xFF8000,
            commentRgb = 0x008000,
            metadataRgb = 0x000080,
            multilineCommentRgb = 0x008000,
            punctuationRgb = 0xAA2C8C,
            markRgb = 0xAA2C8C,
        )
        CodeThemeId.MATRIX -> CodeTheme(
            backgroundRgb = background,
            codeRgb = 0x008500,
            keywordRgb = 0x008500,
            stringRgb = 0x269926,
            literalRgb = 0x39E639,
            commentRgb = 0x67E667,
            metadataRgb = 0x008500,
            multilineCommentRgb = 0x67E667,
            punctuationRgb = 0x008500,
            markRgb = 0x008500,
        )
        CodeThemeId.PASTEL -> CodeTheme(
            backgroundRgb = background,
            codeRgb = 0x20211F,
            keywordRgb = 0x729FCF,
            stringRgb = 0x93CF55,
            literalRgb = 0x8AE234,
            commentRgb = 0x888A85,
            metadataRgb = 0x5DB895,
            multilineCommentRgb = 0x888A85,
            punctuationRgb = 0xCB956D,
            markRgb = 0xCB956D,
        )
        CodeThemeId.ATOM_ONE -> CodeTheme(
            backgroundRgb = background,
            codeRgb = 0x383A42,
            keywordRgb = 0xA626A4,
            stringRgb = 0x50A14F,
            literalRgb = 0x986801,
            commentRgb = 0xA1A1A1,
            metadataRgb = 0xC18401,
            multilineCommentRgb = 0xA1A1A1,
            punctuationRgb = 0xE45649,
            markRgb = 0x526FFF,
        )
    }

    private val githubLight = CodeTheme(
        backgroundRgb = background,
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
}

sealed interface MarkdownInline {
    data class Text(val value: String) : MarkdownInline
    data class Bold(val value: String) : MarkdownInline
    data class Italic(val value: String) : MarkdownInline
    data class Code(val value: String) : MarkdownInline
    data class Link(val text: String, val url: String) : MarkdownInline
    data class Strikethrough(val value: String) : MarkdownInline
    data class Image(val alt: String, val url: String) : MarkdownInline
}
