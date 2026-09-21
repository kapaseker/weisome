package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.WechatArticleStyles

/** Renders a paragraph block while preserving authored line breaks and the first-letter rule. */
internal fun renderParagraph(block: MarkdownBlock.Paragraph, inQuote: Boolean = false): String {
    val css = if (inQuote) WechatArticleStyles.quoteParagraphCss else WechatArticleStyles.paragraphCss
    val lines = block.lines.mapIndexed { index, line ->
        if (index == 0) capitalizeFirstLetter(line) else line
    }
    return "<p style=\"$css\">${lines.joinToString("<br/>", transform = { renderInline(it) })}</p>"
}
