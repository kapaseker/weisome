package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.MarkdownExportStyles

/** Renders a thematic break with the active theme's line or bar style. */
internal fun renderHorizontalRule(styles: MarkdownExportStyles): String =
    "<div style=\"${styles.hrCss}\"></div>"
