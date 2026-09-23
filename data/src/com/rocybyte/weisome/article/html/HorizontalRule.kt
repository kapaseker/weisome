package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.MarkdownExportStyles

/** Renders a thematic break; themes with a logo decoration add the centered logo element. */
internal fun renderHorizontalRule(styles: MarkdownExportStyles): String {
    val logo = if (styles.hrHasLogo) "<div style=\"${styles.hrLogoCss}\"></div>" else ""
    return "<div style=\"${styles.hrCss}\">$logo</div>"
}
