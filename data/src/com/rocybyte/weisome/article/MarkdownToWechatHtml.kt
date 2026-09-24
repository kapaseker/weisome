package com.rocybyte.weisome.article

import com.rocybyte.weisome.article.html.renderBlock

object MarkdownToWechatHtml {
    /** Converts Markdown source into clipboard-ready WeChat HTML rendered with the given themes. */
    fun render(markdown: String, theme: MarkdownThemeId, codeTheme: CodeThemeId): String =
        render(MarkdownDocumentParser.parse(markdown), theme, codeTheme)

    /** Renders structured document blocks as styled HTML elements using both theme style sets. */
    internal fun render(document: MarkdownDocument, theme: MarkdownThemeId, codeTheme: CodeThemeId): String {
        val styles = exportStylesFor(theme)
        val palette = CodeThemes.forId(codeTheme)
        return document.blocks.joinToString("\n") { renderBlock(it, inQuote = false, styles, palette) }
    }
}
