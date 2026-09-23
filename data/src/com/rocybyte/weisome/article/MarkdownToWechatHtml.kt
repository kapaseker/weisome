package com.rocybyte.weisome.article

import com.rocybyte.weisome.article.html.renderBlock

object MarkdownToWechatHtml {
    /** Converts Markdown source into clipboard-ready WeChat HTML rendered with the given theme. */
    fun render(markdown: String, theme: MarkdownThemeId): String =
        render(MarkdownDocumentParser.parse(markdown), theme)

    /** Renders structured document blocks as styled HTML elements using the theme's export styles. */
    internal fun render(document: MarkdownDocument, theme: MarkdownThemeId): String {
        val styles = exportStylesFor(theme)
        return document.blocks.joinToString("\n") { renderBlock(it, inQuote = false, styles) }
    }
}
