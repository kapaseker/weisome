package com.rocybyte.weisome.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp

internal object WeiSomeColors {
    val Primary = Color(0xFF0F766E)
    val OnPrimary = Color(0xFFFFFFFF)
    val Secondary = Color(0xFF475569)
    val Background = Color(0xFFFFFBFE)
    val OnBackground = Color(0xFF1C1B1F)
    val Surface = Color(0xFFFFFBFE)
}

internal object WechatArticleStyles {
    private fun heading(level: Int) = when (level) { 1 -> Triple(24, 24, 16); 2 -> Triple(20, 20, 12); else -> Triple(18, 16, 8) }
    fun headingCss(level: Int): String { val (size, top, bottom) = heading(level); return "font-size: ${size}px; font-weight: 700; line-height: 1.4; margin: ${top}px 0 ${bottom}px;" }
    const val paragraphCss = "font-size: 16px; line-height: 1.75; margin: 0 0 16px;"
    const val listCss = "padding-left: 24px; margin: 0 0 16px;"
    const val listItemCss = "font-size: 16px; line-height: 1.75;"
    val bodyFontSize = 16.sp
    val bodyLineHeight = 28.sp
    val headingFontSize = { level: Int -> heading(level).first.sp }
    val headingTopMargin = { level: Int -> heading(level).second.dp }
    val headingBottomMargin = { level: Int -> heading(level).third.dp }
    val headingWeight = FontWeight.Bold
}

internal object WeiSomeDimensions {
    val PagePadding = 24.dp
    val ContentSpacing = 16.dp
    val ContentMaxWidth = 900.dp
    const val EditorMinLines = 12
}
