package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.CodeTheme
import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownExportStyles

/** Renders a blockquote, adding the theme's decorative quote marks when enabled. */
internal fun renderBlockQuote(
    block: MarkdownBlock.BlockQuote,
    inQuote: Boolean,
    styles: MarkdownExportStyles,
    codeTheme: CodeTheme,
): String {
    val css = if (inQuote) styles.nestedBlockquoteCss else styles.blockquoteCss
    val inner = block.blocks.joinToString("\n") { renderBlock(it, inQuote = true, styles, codeTheme) }
    val marks = if (styles.quoteHasMarks) {
        val closeMark = if (styles.quoteHasClosingMark) "<span style=\"${styles.quoteCloseCss}\">\u201D</span>" else ""
        "<span style=\"${styles.quoteOpenCss}\">\u201C</span>$inner$closeMark"
    } else {
        inner
    }
    return "<blockquote style=\"$css\">$marks</blockquote>"
}
