package com.rocybyte.weisome.page.article.widget

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal object WechatArticlePreviewStyles {
    /** Returns font size and vertical margins for the requested heading level. */
    private fun heading(level: Int): Triple<Int, Int, Int> = when (level) {
        1 -> Triple(24, 24, 16)
        2 -> Triple(20, 20, 12)
        else -> Triple(18, 16, 8)
    }

    val bodyFontSize = 16.sp
    val bodyLineHeight = 28.sp

    /** Returns the configured font size for a heading level. */
    fun headingFontSize(level: Int) = heading(level).first.sp

    /** Returns the configured top margin for a heading level. */
    fun headingTopMargin(level: Int) = heading(level).second.dp

    /** Returns the configured bottom margin for a heading level. */
    fun headingBottomMargin(level: Int) = heading(level).third.dp
}

/** Converts a packed RGB value to an opaque Compose color. */
internal fun Int.toComposeColor(): Color = Color(0xFF000000L or (toLong() and 0xFFFFFFL))
