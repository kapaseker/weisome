package com.rocybyte.weisome.page.article.widget

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rocybyte.weisome.article.CodeTheme
import com.rocybyte.weisome.article.MarkdownThemeId

/** Typography and decoration spec for one heading level. */
internal data class HeadingSpec(
    val size: Float,
    val weight: FontWeight,
    val top: Int,
    val bottom: Int,
    val borderLeft: Boolean,
    val borderBottom: Boolean,
    val muted: Boolean,
    val lineHeightMultiplier: Float,
)

/**
 * Compose preview styles for one Markdown document theme.
 * HYDROGEN mirrors docs/theme/hydrogen/hydrogen.scss (DawnLck/juejin-markdown-theme-hydrogen@b3f86fb),
 * GITHUB mirrors docs/theme/github/github.scss (primer/css src/markdown, light values resolved).
 * Keep the data module HTML export (MarkdownExportStyles) aligned with these values.
 */
internal data class MarkdownPreviewStyles(
    val bodyColor: Color,
    val mutedColor: Color,
    val themeColor: Color,
    val linkColor: Color,
    val linkUnderlined: Boolean,
    val linkHasIcon: Boolean,
    val firstLetterCapitalized: Boolean,
    val h1HasPrefix: Boolean,
    val headingBorderColor: Color,
    val headingBottomBorderColor: Color,
    val bodyFontSize: androidx.compose.ui.unit.TextUnit,
    val bodyLineHeight: androidx.compose.ui.unit.TextUnit,
    val paragraphTopMargin: Int,
    val paragraphBottomMargin: Int,
    val quoteParagraphTopMargin: Int,
    val quoteParagraphBottomMargin: Int,
    val quoteColor: Color,
    val quoteBackground: Color,
    val quoteBorder: Color,
    val quoteHasBackground: Boolean,
    val quoteHasMarks: Boolean,
    val quoteHasHover: Boolean,
    val quotePaddingStart: Int,
    val quotePaddingEnd: Int,
    val quotePaddingTop: Int,
    val quotePaddingBottom: Int,
    val quoteVerticalMargin: Int,
    val quoteNestedVerticalMargin: Int,
    val inlineCodeColor: Color,
    val inlineCodeBackground: Color,
    val inlineCodeFontScale: Float,
    val inlineCodeCornerRadius: Dp,
    val codeBlockTopMargin: Int,
    val codeBlockBottomMargin: Int,
    val codeBlockCornerShape: androidx.compose.foundation.shape.RoundedCornerShape,
    val codeBlockPaddingVertical: Int,
    val codeBlockPaddingHorizontal: Int,
    val codeBlockFontSize: androidx.compose.ui.unit.TextUnit,
    val codeBlockLineHeight: androidx.compose.ui.unit.TextUnit,
    val strikethroughColor: Color,
    val tableBorderColor: Color,
    val tableBorderWidth: Dp,
    val tableHeaderBackground: Color,
    val tableHeaderColor: Color,
    val tableHeaderFontWeight: FontWeight,
    val tableStripeBackground: Color,
    val tableCellPaddingHorizontal: Int,
    val tableCellPaddingVertical: Int,
    val tableFontSize: androidx.compose.ui.unit.TextUnit,
    val tableLineHeight: androidx.compose.ui.unit.TextUnit,
    val ruleIsGradient: Boolean,
    val ruleGradient: List<Color>,
    val ruleSolidColor: Color,
    val ruleHeight: Dp,
    val ruleVerticalMargin: Int,
    val ruleHasLogo: Boolean,
    val listPaddingStart: Int,
    val listTopMargin: Int,
    val listBottomMargin: Int,
    val listItemTopMargin: Int,
    val orderedItemExtraPaddingStart: Int,
    val nestedListPaddingStart: Int,
    val nestedListTopMargin: Int,
    private val headingSpecs: List<HeadingSpec>,
) {
    /** Returns the heading spec for a level from 1 to 6. */
    fun headingSpec(level: Int): HeadingSpec = headingSpecs[(level - 1).coerceIn(0, headingSpecs.lastIndex)]

    /** Returns the configured font size for a heading level. */
    fun headingFontSize(level: Int) = headingSpec(level).size.sp

    /** Returns the configured font weight for a heading level. */
    fun headingFontWeight(level: Int) = headingSpec(level).weight

    /** Returns the configured top margin for a heading level. */
    fun headingTopMargin(level: Int) = headingSpec(level).top.dp

    /** Returns the configured bottom margin for a heading level. */
    fun headingBottomMargin(level: Int) = headingSpec(level).bottom.dp
}

/** Returns the preview style set for the requested Markdown theme. */
internal fun previewStylesFor(theme: MarkdownThemeId): MarkdownPreviewStyles = when (theme) {
    MarkdownThemeId.GITHUB -> GitHubPreviewStyles
    MarkdownThemeId.HYDROGEN -> HydrogenPreviewStyles
}

/** Provides the active Markdown preview styles to the block widgets. */
internal val LocalMarkdownPreviewStyles = staticCompositionLocalOf<MarkdownPreviewStyles> {
    error("MarkdownPreviewStyles is not provided")
}

/** Provides the active code theme palette to the code block widget. */
internal val LocalCodeTheme = staticCompositionLocalOf<CodeTheme> {
    error("CodeTheme is not provided")
}

/** Hydrogen preview styles; values mirror docs/theme/hydrogen/hydrogen.scss. */
internal val HydrogenPreviewStyles = MarkdownPreviewStyles(
    bodyColor = Color(0xDE2E2424),
    mutedColor = Color(0xFF59636E),
    themeColor = Color(0xFF1976D2),
    linkColor = Color(0xFF027FFF),
    linkUnderlined = false,
    linkHasIcon = true,
    firstLetterCapitalized = true,
    h1HasPrefix = true,
    headingBorderColor = Color(0xFF454545),
    headingBottomBorderColor = Color(0xFFD1D9E0),
    bodyFontSize = 16.sp,
    bodyLineHeight = 28.sp,
    paragraphTopMargin = 22,
    paragraphBottomMargin = 22,
    quoteParagraphTopMargin = 10,
    quoteParagraphBottomMargin = 10,
    quoteColor = Color(0xFF666666),
    quoteBackground = Color(0x1FC8C8C8),
    quoteBorder = Color(0xFFCBCBCB),
    quoteHasBackground = true,
    quoteHasMarks = true,
    quoteHasHover = true,
    quotePaddingStart = 27,
    quotePaddingEnd = 23,
    quotePaddingTop = 5,
    quotePaddingBottom = 1,
    quoteVerticalMargin = 22,
    quoteNestedVerticalMargin = 10,
    inlineCodeColor = Color(0xFFC0341D),
    inlineCodeBackground = Color(0xFFFBE5E1),
    inlineCodeFontScale = 0.87f,
    inlineCodeCornerRadius = 2.dp,
    codeBlockTopMargin = 22,
    codeBlockBottomMargin = 22,
    codeBlockCornerShape = androidx.compose.foundation.shape.RoundedCornerShape(0.dp, 4.dp, 0.dp, 4.dp),
    codeBlockPaddingVertical = 15,
    codeBlockPaddingHorizontal = 12,
    codeBlockFontSize = 12.sp,
    codeBlockLineHeight = 21.sp,
    strikethroughColor = Color(0x99000000),
    tableBorderColor = Color(0xFFC6C6C6),
    tableBorderWidth = 2.dp,
    tableHeaderBackground = Color(0xFFF6F6F6),
    tableHeaderColor = Color.Black,
    tableHeaderFontWeight = FontWeight.Normal,
    tableStripeBackground = Color(0xFFFCFCFC),
    tableCellPaddingHorizontal = 7,
    tableCellPaddingVertical = 12,
    tableFontSize = 12.sp,
    tableLineHeight = 24.sp,
    ruleIsGradient = true,
    ruleGradient = listOf(Color(0xFFDDDDDD), Color(0xFF999999), Color(0xFFDDDDDD)),
    ruleSolidColor = Color(0xFFD1D9E0),
    ruleHeight = 1.dp,
    ruleVerticalMargin = 32,
    ruleHasLogo = true,
    listPaddingStart = 28,
    listTopMargin = 16,
    listBottomMargin = 16,
    listItemTopMargin = 0,
    orderedItemExtraPaddingStart = 6,
    nestedListPaddingStart = 28,
    nestedListTopMargin = 3,
    headingSpecs = listOf(
        HeadingSpec(30f, FontWeight.Medium, 35, 5, borderLeft = false, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
        HeadingSpec(28f, FontWeight.Normal, 20, 10, borderLeft = true, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
        HeadingSpec(24f, FontWeight.Normal, 15, 10, borderLeft = false, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
        HeadingSpec(20f, FontWeight.Medium, 35, 10, borderLeft = false, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
        HeadingSpec(16f, FontWeight.Normal, 35, 10, borderLeft = false, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
        // hydrogen defines no h6 font-size; the body base of 16px is used instead.
        HeadingSpec(16f, FontWeight.Normal, 5, 10, borderLeft = false, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
    ),
)

/** GitHub preview styles; values mirror docs/theme/github/github.scss (primer/css markdown, light). */
internal val GitHubPreviewStyles = MarkdownPreviewStyles(
    bodyColor = Color(0xFF1F2328),
    mutedColor = Color(0xFF59636E),
    themeColor = Color(0xFF0969DA),
    linkColor = Color(0xFF0969DA),
    linkUnderlined = true,
    linkHasIcon = false,
    firstLetterCapitalized = false,
    h1HasPrefix = false,
    headingBorderColor = Color(0xFF454545),
    headingBottomBorderColor = Color(0xFFD1D9E0),
    bodyFontSize = 16.sp,
    bodyLineHeight = 24.sp,
    paragraphTopMargin = 0,
    paragraphBottomMargin = 16,
    quoteParagraphTopMargin = 0,
    quoteParagraphBottomMargin = 16,
    quoteColor = Color(0xFF59636E),
    // Only used as the inline-image placeholder fill; blockquotes render without a background.
    quoteBackground = Color(0x33AFB9C9),
    quoteBorder = Color(0xFFD1D9E0),
    quoteHasBackground = false,
    quoteHasMarks = false,
    quoteHasHover = false,
    quotePaddingStart = 16,
    quotePaddingEnd = 16,
    quotePaddingTop = 0,
    quotePaddingBottom = 0,
    quoteVerticalMargin = 16,
    quoteNestedVerticalMargin = 16,
    inlineCodeColor = Color(0xFF1F2328),
    inlineCodeBackground = Color(0x33AFB9C9),
    inlineCodeFontScale = 0.85f,
    inlineCodeCornerRadius = 6.dp,
    codeBlockTopMargin = 0,
    codeBlockBottomMargin = 16,
    codeBlockCornerShape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
    codeBlockPaddingVertical = 16,
    codeBlockPaddingHorizontal = 16,
    codeBlockFontSize = 13.6.sp,
    codeBlockLineHeight = 19.72.sp,
    strikethroughColor = Color(0xFF1F2328),
    tableBorderColor = Color(0xFFD1D9E0),
    tableBorderWidth = 1.dp,
    tableHeaderBackground = Color.Transparent,
    tableHeaderColor = Color(0xFF1F2328),
    tableHeaderFontWeight = FontWeight.SemiBold,
    tableStripeBackground = Color(0xFFF6F8FA),
    tableCellPaddingHorizontal = 13,
    tableCellPaddingVertical = 6,
    tableFontSize = 16.sp,
    tableLineHeight = 24.sp,
    ruleIsGradient = false,
    ruleGradient = emptyList(),
    ruleSolidColor = Color(0xFFD1D9E0),
    ruleHeight = 4.dp,
    ruleVerticalMargin = 24,
    ruleHasLogo = false,
    listPaddingStart = 32,
    listTopMargin = 0,
    listBottomMargin = 16,
    listItemTopMargin = 4,
    orderedItemExtraPaddingStart = 0,
    nestedListPaddingStart = 32,
    nestedListTopMargin = 0,
    headingSpecs = listOf(
        HeadingSpec(32f, FontWeight.SemiBold, 24, 16, borderLeft = false, borderBottom = true, muted = false, lineHeightMultiplier = 1.25f),
        HeadingSpec(24f, FontWeight.SemiBold, 24, 16, borderLeft = false, borderBottom = true, muted = false, lineHeightMultiplier = 1.25f),
        HeadingSpec(20f, FontWeight.SemiBold, 24, 16, borderLeft = false, borderBottom = false, muted = false, lineHeightMultiplier = 1.25f),
        HeadingSpec(16f, FontWeight.SemiBold, 24, 16, borderLeft = false, borderBottom = false, muted = false, lineHeightMultiplier = 1.25f),
        HeadingSpec(14f, FontWeight.SemiBold, 24, 16, borderLeft = false, borderBottom = false, muted = false, lineHeightMultiplier = 1.25f),
        HeadingSpec(13.6f, FontWeight.SemiBold, 24, 16, borderLeft = false, borderBottom = false, muted = true, lineHeightMultiplier = 1.25f),
    ),
)

/** Converts a packed RGB value to an opaque Compose color. */
internal fun Int.toComposeColor(): Color = Color(0xFF000000L or (toLong() and 0xFFFFFFL))
