package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownExportStyles

/** Renders a heading block as a styled heading element with the theme's decorations. */
internal fun renderHeading(block: MarkdownBlock.Heading, styles: MarkdownExportStyles): String {
    val content =
        if (styles.firstLetterCapitalized && block.level in 2..3) capitalizeFirstLetter(block.content) else block.content
    val prefix = styles.headingPrefixHtml(block.level)
    val suffix = styles.headingSuffixHtml(block.level)
    return "<h${block.level} style=\"${styles.headingCss(block.level)}\">$prefix${renderInline(content, styles)}$suffix</h${block.level}>"
}
