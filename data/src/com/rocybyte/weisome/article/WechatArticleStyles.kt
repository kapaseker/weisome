package com.rocybyte.weisome.article

internal object WechatArticleStyles {
    private fun heading(level: Int): Triple<Int, Int, Int> = when (level) {
        1 -> Triple(24, 24, 16)
        2 -> Triple(20, 20, 12)
        else -> Triple(18, 16, 8)
    }

    fun headingCss(level: Int): String {
        val (size, top, bottom) = heading(level)
        return "font-size: ${size}px; font-weight: 700; line-height: 1.4; margin: ${top}px 0 ${bottom}px;"
    }

    const val paragraphCss = "font-size: 16px; line-height: 1.75; margin: 0 0 16px;"
    const val listCss = "padding-left: 24px; margin: 0 0 16px;"
    const val listItemCss = "font-size: 16px; line-height: 1.75;"
}
