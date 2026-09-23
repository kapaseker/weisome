package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownExportStyles

/** Renders a paragraph block while preserving authored line breaks and the theme's first-letter rule. */
internal fun renderParagraph(block: MarkdownBlock.Paragraph, inQuote: Boolean, styles: MarkdownExportStyles): String {
    val css = if (inQuote) styles.quoteParagraphCss else styles.paragraphCss
    val lines = block.lines.mapIndexed { index, line ->
        if (index == 0 && styles.firstLetterCapitalized) capitalizeFirstLetter(line) else line
    }
    return "<p style=\"$css\">${lines.joinToString("<br/>", transform = { renderInline(it, styles) })}</p>"
}
