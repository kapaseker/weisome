package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownExportStyles

/** Renders a heading block as a styled heading element with the theme's decorations. */
internal fun renderHeading(block: MarkdownBlock.Heading, styles: MarkdownExportStyles): String {
    val content =
        if (styles.firstLetterCapitalized && block.level in 2..3) capitalizeFirstLetter(block.content) else block.content
    val prefix = if (block.level == 1 && styles.h1HasPrefix) {
        "<span style=\"${styles.h1PrefixCss}\">#</span>"
    } else {
        ""
    }
    return "<h${block.level} style=\"${styles.headingCss(block.level)}\">$prefix${renderInline(content, styles)}</h${block.level}>"
}
