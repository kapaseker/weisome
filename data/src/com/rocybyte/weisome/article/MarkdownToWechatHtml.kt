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
        // prev feeds the sibling-sensitive rules (claudette's h1 + p lead paragraph);
        // atDocumentStart drops the leading block's top margin for themes with a `:first-child` rule.
        val content = document.blocks
            .mapIndexed { index, block ->
                renderBlock(
                    block = block,
                    inQuote = false,
                    styles = styles,
                    codeTheme = palette,
                    prev = document.blocks.getOrNull(index - 1),
                    atDocumentStart = index == 0,
                )
            }
            .joinToString("\n")
        return if (theme == MarkdownThemeId.CHOCOLATE && content.isNotEmpty()) {
            "<section style=\"overflow: hidden; " +
                "background-image: linear-gradient(90deg, rgba(72, 42, 10, 0.05) 5%, rgba(72, 42, 10, 0) 5%), " +
                "linear-gradient(360deg, rgba(72, 42, 10, 0.05) 5%, rgba(72, 42, 10, 0) 5%); " +
                "background-size: 20px 20px; background-position: center center;\">$content</section>"
        } else {
            content
        }
    }
}
