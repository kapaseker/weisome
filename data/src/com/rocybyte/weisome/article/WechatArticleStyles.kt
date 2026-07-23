package com.rocybyte.weisome.article

internal object WechatArticleStyles {
    /** Returns font size and vertical margins for a normalized heading level. */
    private fun heading(level: Int): Triple<Int, Int, Int> = when (level) {
        1 -> Triple(24, 24, 16)
        2 -> Triple(20, 20, 12)
        else -> Triple(18, 16, 8)
    }

    /** Builds the inline CSS used to render a heading at the requested level. */
    fun headingCss(level: Int): String {
        val (size, top, bottom) = heading(level)
        return "font-size: ${size}px; font-weight: 700; line-height: 1.4; margin: ${top}px 0 ${bottom}px;"
    }

    const val paragraphCss = "font-size: 16px; line-height: 1.75; margin: 0 0 16px;"
    const val listCss = "padding-left: 24px; margin: 0 0 16px;"
    const val listItemCss = "font-size: 16px; line-height: 1.75;"
    val inlineCodeCss = "color: #${WeiSomeLightCodeTheme.codeRgb.toString(16).padStart(6, '0')}; background: #${WeiSomeLightCodeTheme.backgroundRgb.toString(16).padStart(6, '0')}; padding: 2px 4px; border-radius: 4px; font-family: inherit; font-size: inherit; font-weight: 400; font-style: normal; box-decoration-break: clone; -webkit-box-decoration-break: clone; overflow-wrap: anywhere;"
    val codeBlockCss = "background: #${WeiSomeLightCodeTheme.backgroundRgb.toString(16).padStart(6, '0')}; color: #${WeiSomeLightCodeTheme.codeRgb.toString(16).padStart(6, '0')}; font-family: monospace; font-size: 14px; line-height: 1.6; padding: 16px; margin: 0 0 16px; white-space: pre-wrap; word-break: break-word;"
}
