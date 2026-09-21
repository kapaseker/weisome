package com.rocybyte.weisome.page.article.widget

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Compose preview styles mirroring the hydrogen theme.
 * Source of truth: docs/hydrogen.scss (DawnLck/juejin-markdown-theme-hydrogen@b3f86fb).
 * Keep the data module HTML export (WechatArticleStyles) aligned with these values.
 */
internal object WechatArticlePreviewStyles {
    /** hydrogen $theme-color used by heading prefixes and hover borders. */
    val themeColor = Color(0xFF1976D2)

    /** .markdown-body color: rgba(46, 36, 36, 0.87). */
    val bodyColor = Color(0xDE2E2424)

    /** $color-link used for anchors and the link icon. */
    val linkColor = Color(0xFF027FFF)

    /** blockquote text color ($grey3). */
    val quoteColor = Color(0xFF666666)

    /** blockquote background: rgba(200, 200, 200, 0.12). */
    val quoteBackground = Color(0x1FC8C8C8)

    /** blockquote border and decorative quote marks ($grey4). */
    val quoteBorder = Color(0xFFCBCBCB)

    /** h2 resting border color ($grey2). */
    val headingBorderColor = Color(0xFF454545)

    /** inline code: #c0341d on #fbe5e1. */
    val inlineCodeColor = Color(0xFFC0341D)
    val inlineCodeBackground = Color(0xFFFBE5E1)

    /** code block: #333 on #f8f8f8. */
    val codeBlockColor = Color(0xFF333333)
    val codeBlockBackground = Color(0xFFF8F8F8)

    /** del: rgba(0, 0, 0, 0.6). */
    val strikethroughColor = Color(0x99000000)

    /** table border and header/stripe fills. */
    val tableBorder = Color(0xFFC6C6C6)
    val tableHeaderBackground = Color(0xFFF6F6F6)
    val tableStripeBackground = Color(0xFFFCFCFC)

    /** hr gradient stops. */
    val ruleGradient = listOf(Color(0xFFDDDDDD), Color(0xFF999999), Color(0xFFDDDDDD))

    val bodyFontSize = 16.sp

    /** Body line height: 16px * 1.75. */
    val bodyLineHeight = 28.sp

    private data class HeadingSpec(
        val size: Int,
        val weight: FontWeight,
        val top: Int,
        val bottom: Int,
        val borderLeft: Boolean,
    )

    /** Returns the hydrogen typography for a heading level from 1 to 6. */
    private fun heading(level: Int): HeadingSpec = when (level) {
        1 -> HeadingSpec(30, FontWeight.Medium, 35, 5, borderLeft = false)
        2 -> HeadingSpec(28, FontWeight.Normal, 20, 10, borderLeft = true)
        3 -> HeadingSpec(24, FontWeight.Normal, 15, 10, borderLeft = false)
        4 -> HeadingSpec(20, FontWeight.Medium, 35, 10, borderLeft = false)
        5 -> HeadingSpec(16, FontWeight.Normal, 35, 10, borderLeft = false)
        // hydrogen defines no h6 font-size; the body base of 16px is used instead.
        else -> HeadingSpec(16, FontWeight.Normal, 5, 10, borderLeft = false)
    }

    /** Returns the configured font size for a heading level. */
    fun headingFontSize(level: Int) = heading(level).size.sp

    /** Returns the configured font weight for a heading level. */
    fun headingFontWeight(level: Int) = heading(level).weight

    /** Returns the configured top margin for a heading level. */
    fun headingTopMargin(level: Int) = heading(level).top.dp

    /** Returns the configured bottom margin for a heading level. */
    fun headingBottomMargin(level: Int) = heading(level).bottom.dp

    /** Reports whether the heading level renders the hydrogen left border. */
    fun headingHasBorder(level: Int) = heading(level).borderLeft
}

/** Converts a packed RGB value to an opaque Compose color. */
internal fun Int.toComposeColor(): Color = Color(0xFF000000L or (toLong() and 0xFFFFFFL))
