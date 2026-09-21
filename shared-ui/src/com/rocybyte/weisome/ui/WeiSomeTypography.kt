package com.rocybyte.weisome.ui

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/**
 * Typography tokens from DESIGN.md. Fonts resolve to the system sans until
 * Plus Jakarta Sans ships; swapping the shared [fontFamily] value upgrades every style at once.
 */
internal object WeiSomeTypography {
    private val fontFamily = FontFamily.SansSerif

    val display = TextStyle(
        fontFamily = fontFamily,
        fontSize = 64.sp,
        fontWeight = FontWeight.ExtraBold,
        lineHeight = 70.4.sp,
        letterSpacing = (-0.04).em,
    )

    val h1 = TextStyle(
        fontFamily = fontFamily,
        fontSize = 48.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 57.6.sp,
        letterSpacing = (-0.03).em,
    )

    val h2 = TextStyle(
        fontFamily = fontFamily,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 38.4.sp,
        letterSpacing = (-0.02).em,
    )

    val h3 = TextStyle(
        fontFamily = fontFamily,
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 31.2.sp,
        letterSpacing = (-0.02).em,
    )

    val bodyLg = TextStyle(
        fontFamily = fontFamily,
        fontSize = 18.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 28.8.sp,
        letterSpacing = (-0.01).em,
    )

    val bodyMd = TextStyle(
        fontFamily = fontFamily,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 25.6.sp,
        letterSpacing = (-0.01).em,
    )

    val labelSm = TextStyle(
        fontFamily = fontFamily,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 15.6.sp,
        letterSpacing = 0.02.em,
    )
}

/** Ambient text style inherited by WeiSomeText when no explicit style is given. */
internal val LocalWeiSomeTextStyle = staticCompositionLocalOf { WeiSomeTypography.bodyMd }
