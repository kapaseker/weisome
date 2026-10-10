package com.rocybyte.weisome.page.article.widget

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rocybyte.weisome.article.ChocolateThemeIconPngBase64
import com.rocybyte.weisome.article.CodeTheme
import com.rocybyte.weisome.article.MarkdownThemeId
import com.rocybyte.weisome.article.MarkdownInline
import java.util.Base64
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Image

/** Typography and decoration spec for one heading level. */
internal data class HeadingSpec(
    val size: Float,
    val weight: FontWeight,
    val top: Int,
    val bottom: Int,
    val borderLeft: Boolean,
    val borderWidth: Dp,
    val borderBottom: Boolean,
    val muted: Boolean,
    val lineHeightMultiplier: Float,
    /** Absolute line height overriding [lineHeightMultiplier] × size (yu's even-valued px line-heights). */
    val lineHeight: androidx.compose.ui.unit.TextUnit? = null,
    val borderBottomWidth: Dp = 1.dp,
    /** Gap between the text row and the bottom border, mirroring the export's padding-bottom; unused without [borderBottom]. */
    val borderBottomGap: Dp = 0.dp,
    val prefixEmoji: String = "",
    val prefixEmojiSize: Float = 0f,
    val prefixEmojiLeft: Int = 0,
    /** Renders the text uppercased (claudette's small uppercase labels). */
    val uppercase: Boolean = false,
    /** Renders a clay-sized accent dot before the text (claudette's h3 marker). */
    val prefixDot: Boolean = false,
    /** Width of the heading's bottom rule; 0 paints a full-width border, >0 paints a short rounded rule (claudette's h1 underline). */
    val shortRuleWidth: Dp = 0.dp,
    /** Color of the heading's bottom rule; [androidx.compose.ui.graphics.Color.Unspecified] falls back to the theme's shared border color. */
    val borderBottomColor: Color = Color.Unspecified,
    /** Gap between the painted left border and the text; defaults to the 10dp every pre-existing bordered theme uses. */
    val borderLeftTextGap: Dp = 10.dp,
)

/**
 * Compose preview styles for one Markdown document theme.
 * HYDROGEN mirrors docs/theme/hydrogen/hydrogen.scss (DawnLck/juejin-markdown-theme-hydrogen@b3f86fb),
 * GITHUB mirrors docs/theme/github/github.scss (primer/css src/markdown, light values resolved),
 * SMART_BLUE mirrors docs/theme/smart-blue/smart-blue.css (cumt-robin/juejin-markdown-theme-smart-blue@f740565).
 * TYPORA_PAPER mirrors docs/theme/typora-paper at lisitan/esther-obsidian-typora-themes@8c4f912.
 * RIM mirrors docs/theme/rim/rim.css at Rimseg/typora-theme-rim@f0d54ef.
 * CHOCOLATE mirrors docs/theme/chocolate/chocolate.scss at qklhk/juejin-markdown-theme-qklhk@4f2a290.
 * YU mirrors docs/theme/yu/yu.scss at jianghurong/juejin-markdown-theme-yu@1e3096f.
 * CYANOSIS mirrors docs/theme/cyanosis/cyanosis.scss at linxsbox/juejin-markdown-theme-cyanosis@6b814ea.
 * CYAN mirrors docs/theme/cyan/channing-cyan.scss at ChanningHan/juejin-markdown-theme-channing-cyan@c843c2f.
 * V_GREEN mirrors docs/theme/v-green/v-green.scss at DawnLck/juejin-markdown-theme-v-green@015f88b.
 * CLAUDETTE mirrors docs/theme/claudette/claudette.css at CookPiu/typora-theme-claudette@0a6c75a (light variant).
 * Keep the data module HTML export (MarkdownExportStyles) aligned with these values.
 */
internal data class MarkdownPreviewStyles(
    val bodyColor: Color,
    val mutedColor: Color,
    val headingColor: Color,
    val themeColor: Color,
    val linkColor: Color,
    val boldColor: Color?,
    val linkUnderlined: Boolean,
    val linkHasIcon: Boolean,
    val firstLetterCapitalized: Boolean,
    val h1HasPrefix: Boolean,
    val h1Centered: Boolean,
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
    val inlineCodeBorderColor: Color = Color.Transparent,
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
    val listPaddingStart: Int,
    val listTopMargin: Int,
    val listBottomMargin: Int,
    val listItemTopMargin: Int,
    val orderedItemExtraPaddingStart: Int,
    val nestedListPaddingStart: Int,
    val nestedListTopMargin: Int,
    private val headingSpecs: List<HeadingSpec>,
    val h1AccentBar: Boolean = false,
    val h2AccentDots: Boolean = false,
    /** Optional decorative bitmap rendered before h1/h2 text (chocolate's piece icon). */
    val h1Icon: ImageBitmap? = null,
    val h2Icon: ImageBitmap? = null,
    val quoteBorderWidth: Dp = 4.dp,
    val quoteCornerRadius: Dp = 0.dp,
    val quoteShadowElevation: Dp = 0.dp,
    val quoteMarkColor: Color = Color.Unspecified,
    val quoteMarkFontSize: androidx.compose.ui.unit.TextUnit = 24.sp,
    val quoteHasClosingMark: Boolean = true,
    val quoteMinHeight: Dp = 0.dp,
    /** Nested-quote background; null reuses [quoteBackground]. */
    val quoteNestedBackground: Color? = null,
    /** Nested-quote shadow; null reuses [quoteShadowElevation]. */
    val quoteNestedShadowElevation: Dp? = null,
    /** Nested-quote bottom margin; null reuses [quoteNestedVerticalMargin]. */
    val quoteNestedBottomMargin: Int? = null,
    /** When set, the opening mark's ink top is anchored this far below the card top, matching the export CSS; null keeps the legacy top-left offset. */
    val quoteMarkInkTop: Dp? = null,
    val quoteMarkWeight: FontWeight = FontWeight.ExtraBold,
    /** Glyphs of the decorative opening/closing quote marks (quoteHasMarks only). */
    val quoteOpenMark: String = "\u201C",
    val quoteCloseMark: String = "\u201D",
    /** Whether [quoteBorder] strokes the full rounded box instead of only the start edge. */
    val quoteHasBoxBorder: Boolean = false,
    val codeBlockHasWindowHeader: Boolean = false,
    val codeBlockFrameColor: Color = Color.Transparent,
    val codeBlockBorderColor: Color = Color.Transparent,
    val codeBlockBorderWidth: Dp = 0.dp,
    val codeBlockShadowElevation: Dp = 0.dp,
    val ruleWidthFraction: Float = 1f,
    val ruleHasPaperAccents: Boolean = false,
    val unorderedMarkerColor: Color = Color.Unspecified,
    val orderedMarkerColor: Color = Color.Unspecified,
    val imageCornerRadius: Dp = 2.dp,
    val imageShadowElevation: Dp = 0.dp,
    val tableCornerRadius: Dp = 0.dp,
    val tableShadowElevation: Dp = 0.dp,
    /** Renders h1–h4 headings with [androidx.compose.ui.text.font.FontFamily.Serif] (claudette's serif headings). */
    val headingFontSerif: Boolean = false,
    /** Renders blockquote text with a serif font family (claudette's serif quote). */
    val quoteFontSerif: Boolean = false,
    /** Lead-paragraph font size applied to the paragraph directly after an h1; null disables the lead style. */
    val leadFontSize: androidx.compose.ui.unit.TextUnit? = null,
    /** Lead-paragraph line height; unused without [leadFontSize]. */
    val leadLineHeight: androidx.compose.ui.unit.TextUnit? = null,
    /** Lead-paragraph text color; unused without [leadFontSize]. */
    val leadColor: Color = Color.Unspecified,
    /** Text glyph appended after links instead of the hydrogen SVG icon (claudette's ↗ arrow); null keeps the SVG icon. */
    val linkIconText: String? = null,
    /** Ink color of [linkIconText]. */
    val linkIconTextColor: Color = Color.Unspecified,
    /** Font size of [linkIconText]. */
    val linkIconTextSize: androidx.compose.ui.unit.TextUnit = 12.sp,
    /** Renders a centered glyph over the rule line (claudette's hr asterisk); null keeps a plain rule. */
    val ruleCenterGlyph: String? = null,
    /** Renders table header text uppercased (claudette's uppercase column headers). */
    val tableHeaderUppercase: Boolean = false,
    /** Header-cell font size; null reuses [tableFontSize] (claudette's 12px headers over 15px cells). */
    val tableHeaderFontSize: androidx.compose.ui.unit.TextUnit? = null,
    /** Renders the code block's language as a corner pill label when the block declares one. */
    val codeBlockHasLangLabel: Boolean = false,
    /** Pill background of the code-block language label. */
    val codeBlockLangLabelBackground: Color = Color.Unspecified,
    /** Pill text color of the code-block language label. */
    val codeBlockLangLabelTextColor: Color = Color.Unspecified,
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

    /** Returns the decorative bitmap rendered before h1/h2 text, or null for other levels. */
    fun iconFor(level: Int): ImageBitmap? = when (level) {
        1 -> h1Icon
        2 -> h2Icon
        else -> null
    }
}

/** Returns the preview style set for the requested Markdown theme. */
internal fun previewStylesFor(theme: MarkdownThemeId): MarkdownPreviewStyles = when (theme) {
    MarkdownThemeId.GITHUB -> GitHubPreviewStyles
    MarkdownThemeId.HYDROGEN -> HydrogenPreviewStyles
    MarkdownThemeId.SMART_BLUE -> SmartBluePreviewStyles
    MarkdownThemeId.TYPORA_PAPER -> TyporaPaperPreviewStyles
    MarkdownThemeId.RIM -> RimPreviewStyles
    MarkdownThemeId.CHOCOLATE -> ChocolatePreviewStyles
    MarkdownThemeId.YU -> YuPreviewStyles
    MarkdownThemeId.CYANOSIS -> CyanosisPreviewStyles
    MarkdownThemeId.CYAN -> CyanPreviewStyles
    MarkdownThemeId.V_GREEN -> VGreenPreviewStyles
    MarkdownThemeId.CLAUDETTE -> ClaudettePreviewStyles
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
    headingColor = Color(0xDE2E2424),
    themeColor = Color(0xFF1976D2),
    linkColor = Color(0xFF027FFF),
    boldColor = null,
    linkUnderlined = false,
    linkHasIcon = true,
    firstLetterCapitalized = true,
    h1HasPrefix = true,
    h1Centered = false,
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
    listPaddingStart = 28,
    listTopMargin = 16,
    listBottomMargin = 16,
    listItemTopMargin = 0,
    orderedItemExtraPaddingStart = 6,
    nestedListPaddingStart = 28,
    nestedListTopMargin = 3,
    headingSpecs = listOf(
        HeadingSpec(30f, FontWeight.Medium, 35, 5, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
        // The 15px bottom is hydrogen's 10px h2 margin plus its 5px shared padding-bottom.
        HeadingSpec(28f, FontWeight.Normal, 20, 15, borderLeft = true, borderWidth = 5.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
        HeadingSpec(24f, FontWeight.Normal, 15, 10, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
        HeadingSpec(20f, FontWeight.Medium, 35, 10, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
        HeadingSpec(16f, FontWeight.Normal, 35, 10, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
        // hydrogen defines no h6 font-size; the body base of 16px is used instead.
        HeadingSpec(16f, FontWeight.Normal, 5, 10, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
    ),
)

/** GitHub preview styles; values mirror docs/theme/github/github.scss (primer/css markdown, light). */
internal val GitHubPreviewStyles = MarkdownPreviewStyles(
    bodyColor = Color(0xFF1F2328),
    mutedColor = Color(0xFF59636E),
    headingColor = Color(0xFF1F2328),
    themeColor = Color(0xFF0969DA),
    linkColor = Color(0xFF0969DA),
    boldColor = null,
    linkUnderlined = true,
    linkHasIcon = false,
    firstLetterCapitalized = false,
    h1HasPrefix = false,
    h1Centered = false,
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
    listPaddingStart = 32,
    listTopMargin = 0,
    listBottomMargin = 16,
    listItemTopMargin = 4,
    orderedItemExtraPaddingStart = 0,
    nestedListPaddingStart = 32,
    nestedListTopMargin = 0,
    headingSpecs = listOf(
        HeadingSpec(32f, FontWeight.SemiBold, 24, 16, borderLeft = false, borderWidth = 0.dp, borderBottom = true, muted = false, lineHeightMultiplier = 1.25f, borderBottomGap = 10.dp),
        HeadingSpec(24f, FontWeight.SemiBold, 24, 16, borderLeft = false, borderWidth = 0.dp, borderBottom = true, muted = false, lineHeightMultiplier = 1.25f, borderBottomGap = 8.dp),
        HeadingSpec(20f, FontWeight.SemiBold, 24, 16, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.25f),
        HeadingSpec(16f, FontWeight.SemiBold, 24, 16, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.25f),
        HeadingSpec(14f, FontWeight.SemiBold, 24, 16, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.25f),
        HeadingSpec(13.6f, FontWeight.SemiBold, 24, 16, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = true, lineHeightMultiplier = 1.25f),
    ),
)

/**
 * smart-blue preview styles; values mirror docs/theme/smart-blue/smart-blue.css.
 * The h1 juejin-logo watermark is absent from the reference stylesheet, and the `.markdown-body`
 * grid background and `kbd` rule are intentionally not rendered. Paragraph spacing approximates
 * `p + p { margin-top: 16px }` with a top margin (the model has no sibling selector), and quote
 * paragraphs keep the stylesheet's tight 2px inset because `blockquote p` defines no margin.
 */
internal val SmartBluePreviewStyles = MarkdownPreviewStyles(
    bodyColor = Color(0xFF595959),
    // Unused: no smart-blue heading takes the muted color.
    mutedColor = Color(0xFF666666),
    headingColor = Color(0xFF135CE0),
    themeColor = Color(0xFF135CE0),
    linkColor = Color(0xFF036ACA),
    boldColor = Color(0xFF036ACA),
    linkUnderlined = true,
    linkHasIcon = false,
    firstLetterCapitalized = false,
    h1HasPrefix = false,
    h1Centered = true,
    headingBorderColor = Color(0xFF135CE0),
    // Unused: no smart-blue heading renders a bottom border.
    headingBottomBorderColor = Color(0xFFDFE2E5),
    bodyFontSize = 15.sp,
    bodyLineHeight = 30.sp,
    paragraphTopMargin = 16,
    paragraphBottomMargin = 0,
    quoteParagraphTopMargin = 0,
    quoteParagraphBottomMargin = 0,
    quoteColor = Color(0xFF666666),
    quoteBackground = Color(0xFFFFF9F9),
    quoteBorder = Color(0xFFB2AEC5),
    quoteHasBackground = true,
    quoteHasMarks = false,
    quoteHasHover = false,
    // 24 = the stylesheet's 20px padding-left plus the 4px left border the widget paints inside.
    quotePaddingStart = 24,
    quotePaddingEnd = 20,
    quotePaddingTop = 2,
    quotePaddingBottom = 2,
    quoteVerticalMargin = 30,
    quoteNestedVerticalMargin = 30,
    inlineCodeColor = Color(0xFFFF502C),
    inlineCodeBackground = Color(0xFFFFF5F5),
    inlineCodeFontScale = 0.87f,
    inlineCodeCornerRadius = 2.dp,
    codeBlockTopMargin = 15,
    codeBlockBottomMargin = 15,
    codeBlockCornerShape = androidx.compose.foundation.shape.RoundedCornerShape(0.dp),
    codeBlockPaddingVertical = 15,
    codeBlockPaddingHorizontal = 12,
    codeBlockFontSize = 12.sp,
    codeBlockLineHeight = 21.sp,
    strikethroughColor = Color(0xFF595959),
    tableBorderColor = Color(0xFFDFE2E5),
    tableBorderWidth = 1.dp,
    tableHeaderBackground = Color.Transparent,
    tableHeaderColor = Color(0xFF595959),
    tableHeaderFontWeight = FontWeight.Bold,
    tableStripeBackground = Color(0xFFF6F8FA),
    tableCellPaddingHorizontal = 15,
    tableCellPaddingVertical = 9,
    tableFontSize = 15.sp,
    // The stylesheet sets no cell line-height, i.e. the browser's normal (about 1.2 x 15px).
    tableLineHeight = 18.sp,
    ruleIsGradient = false,
    ruleGradient = emptyList(),
    ruleSolidColor = Color(0xFF135CE0),
    ruleHeight = 1.dp,
    // The stylesheet sets only the top border; 8px is its .5em browser default at the 15px body size.
    ruleVerticalMargin = 8,
    // 70 = the stylesheet's 2em ul margin-left plus the browser's 40px padding-left; ordered lists
    // share it because the model carries a single list indent.
    listPaddingStart = 70,
    listTopMargin = 15,
    listBottomMargin = 15,
    listItemTopMargin = 0,
    orderedItemExtraPaddingStart = 0,
    nestedListPaddingStart = 70,
    nestedListTopMargin = 15,
    headingSpecs = listOf(
        // The stylesheet's symmetric 80px (50 margin + 30 padding) becomes hydrogen's asymmetric h1
        // rhythm, which renders 35px above and 27px below. The 10px here plus the paragraph's 16px
        // top margin render 26px, the same gap the export writes as the heading's own bottom margin.
        HeadingSpec(22f, FontWeight.Bold, 35, 10, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
        HeadingSpec(20f, FontWeight.Bold, 30, 30, borderLeft = true, borderWidth = 4.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
        HeadingSpec(16f, FontWeight.Bold, 30, 30, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
        // h4 to h6 keep the shared rule and fall back to the browser's 1em/0.83em/0.67em at 15px.
        HeadingSpec(15f, FontWeight.Bold, 30, 30, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
        HeadingSpec(12.45f, FontWeight.Bold, 30, 30, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
        HeadingSpec(10.05f, FontWeight.Bold, 30, 30, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
    ),
)

/** Typora Paper preview styles adapted from Esther Inspired Paper at commit 8c4f912. */
internal val TyporaPaperPreviewStyles = MarkdownPreviewStyles(
    bodyColor = Color(0xFF1A1A2E),
    mutedColor = Color(0xFF555568),
    headingColor = Color(0xFF17172A),
    themeColor = Color(0xFF2B7FD8),
    linkColor = Color(0xFF2B7FD8),
    boldColor = Color(0xFF17172A),
    linkUnderlined = true,
    linkHasIcon = false,
    firstLetterCapitalized = false,
    h1HasPrefix = false,
    h1Centered = false,
    headingBorderColor = Color.Transparent,
    headingBottomBorderColor = Color(0xFFE8E0CF),
    bodyFontSize = 16.sp,
    bodyLineHeight = 29.12.sp,
    paragraphTopMargin = 16,
    paragraphBottomMargin = 16,
    quoteParagraphTopMargin = 0,
    quoteParagraphBottomMargin = 0,
    quoteColor = Color(0xFF555568),
    quoteBackground = Color.White,
    quoteBorder = Color.Transparent,
    quoteHasBackground = true,
    quoteHasMarks = true,
    quoteHasHover = false,
    quotePaddingStart = 59,
    quotePaddingEnd = 28,
    quotePaddingTop = 25,
    quotePaddingBottom = 23,
    quoteVerticalMargin = 27,
    quoteNestedVerticalMargin = 16,
    inlineCodeColor = Color(0xFFB43C50),
    inlineCodeBackground = Color(0xFFF5EFE1),
    inlineCodeFontScale = 0.88f,
    inlineCodeCornerRadius = 6.dp,
    inlineCodeBorderColor = Color(0xFFE8E0CF),
    codeBlockTopMargin = 24,
    codeBlockBottomMargin = 24,
    codeBlockCornerShape = androidx.compose.foundation.shape.RoundedCornerShape(15.dp),
    codeBlockPaddingVertical = 20,
    codeBlockPaddingHorizontal = 19,
    codeBlockFontSize = 14.08.sp,
    codeBlockLineHeight = 23.65.sp,
    strikethroughColor = Color(0xFF1A1A2E),
    tableBorderColor = Color(0xFFE8E0CF),
    tableBorderWidth = 1.dp,
    tableHeaderBackground = Color(0x1A2B7FD8),
    tableHeaderColor = Color(0xFF17172A),
    tableHeaderFontWeight = FontWeight.ExtraBold,
    tableStripeBackground = Color(0xFFFAF6EB),
    tableCellPaddingHorizontal = 14,
    tableCellPaddingVertical = 12,
    tableFontSize = 16.sp,
    tableLineHeight = 29.12.sp,
    ruleIsGradient = false,
    ruleGradient = emptyList(),
    ruleSolidColor = Color(0xFFF4D758),
    ruleHeight = 5.dp,
    ruleVerticalMargin = 51,
    listPaddingStart = 26,
    listTopMargin = 16,
    listBottomMargin = 16,
    listItemTopMargin = 5,
    orderedItemExtraPaddingStart = 3,
    nestedListPaddingStart = 26,
    nestedListTopMargin = 3,
    headingSpecs = listOf(
        HeadingSpec(30f, FontWeight.ExtraBold, 28, 36, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.18f),
        HeadingSpec(28f, FontWeight.ExtraBold, 67, 23, borderLeft = false, borderWidth = 0.dp, borderBottom = true, muted = false, lineHeightMultiplier = 1.35f, borderBottomGap = 12.dp),
        HeadingSpec(24f, FontWeight.ExtraBold, 45, 16, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.35f),
        HeadingSpec(20f, FontWeight.ExtraBold, 38, 13, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.35f),
        HeadingSpec(16f, FontWeight.ExtraBold, 33, 11, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.35f),
        HeadingSpec(16f, FontWeight.ExtraBold, 30, 10, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = true, lineHeightMultiplier = 1.35f),
    ),
    h1AccentBar = true,
    h2AccentDots = true,
    quoteBorderWidth = 0.dp,
    quoteCornerRadius = 18.dp,
    quoteShadowElevation = 10.dp,
    quoteMarkColor = Color(0xFFF4D758),
    // Export uses 73.6px, but the system font draws a taller ink for the mark glyph;
    // 58sp yields the same ~20px ink height as the export, keeping a gap above the text.
    quoteMarkFontSize = 58.sp,
    quoteMarkWeight = FontWeight.Bold,
    quoteMarkInkTop = 6.dp,
    quoteMinHeight = 37.dp,
    quoteNestedBackground = Color(0xFFFAF6EB),
    quoteNestedShadowElevation = 0.dp,
    quoteNestedBottomMargin = 0,
    quoteHasClosingMark = false,
    codeBlockHasWindowHeader = true,
    codeBlockFrameColor = Color(0xFFEEF0F4),
    codeBlockBorderColor = Color(0xFFD9DDE5),
    codeBlockBorderWidth = 1.dp,
    codeBlockShadowElevation = 12.dp,
    ruleWidthFraction = 0.42f,
    ruleHasPaperAccents = true,
    unorderedMarkerColor = Color(0xFFE84A5F),
    orderedMarkerColor = Color(0xFF2B7FD8),
    imageCornerRadius = 14.dp,
    imageShadowElevation = 12.dp,
    tableCornerRadius = 14.dp,
    tableShadowElevation = 6.dp,
)

/** Rim preview styles adapted from typora-theme-rim at commit f0d54ef. */
internal val RimPreviewStyles = MarkdownPreviewStyles(
    bodyColor = Color(0xFF13202C),
    mutedColor = Color(0xFF47525D),
    headingColor = Color(0xFF152E45),
    themeColor = Color(0xFF4E3E8B),
    linkColor = Color(0xFF3E3282),
    boldColor = null,
    linkUnderlined = true,
    linkHasIcon = false,
    firstLetterCapitalized = false,
    h1HasPrefix = false,
    h1Centered = false,
    headingBorderColor = Color.Transparent,
    headingBottomBorderColor = Color.Transparent,
    bodyFontSize = 18.sp,
    bodyLineHeight = 30.6.sp,
    paragraphTopMargin = 14,
    paragraphBottomMargin = 14,
    quoteParagraphTopMargin = 14,
    quoteParagraphBottomMargin = 14,
    quoteColor = Color(0xFF13202C),
    quoteBackground = Color.Transparent,
    quoteBorder = Color(0xFF4E3E8B),
    quoteHasBackground = false,
    quoteHasMarks = false,
    quoteHasHover = false,
    quotePaddingStart = 17,
    quotePaddingEnd = 15,
    quotePaddingTop = 0,
    quotePaddingBottom = 0,
    quoteVerticalMargin = 14,
    quoteNestedVerticalMargin = 14,
    inlineCodeColor = Color(0xFF00711E),
    inlineCodeBackground = Color(0xFFF8F6F6),
    inlineCodeFontScale = 0.8f,
    inlineCodeCornerRadius = 3.dp,
    codeBlockTopMargin = 15,
    codeBlockBottomMargin = 15,
    codeBlockCornerShape = androidx.compose.foundation.shape.RoundedCornerShape(3.dp),
    codeBlockPaddingVertical = 7,
    codeBlockPaddingHorizontal = 8,
    codeBlockFontSize = 14.4.sp,
    codeBlockLineHeight = 20.16.sp,
    strikethroughColor = Color(0xFF13202C),
    tableBorderColor = Color(0xFFCCCCCC),
    tableBorderWidth = 1.dp,
    tableHeaderBackground = Color.Transparent,
    tableHeaderColor = Color(0xFF13202C),
    tableHeaderFontWeight = FontWeight.Bold,
    tableStripeBackground = Color.Transparent,
    tableCellPaddingHorizontal = 0,
    tableCellPaddingVertical = 6,
    tableFontSize = 14.4.sp,
    tableLineHeight = 20.16.sp,
    ruleIsGradient = false,
    ruleGradient = emptyList(),
    ruleSolidColor = Color(0xFFDEDEDE),
    ruleHeight = 2.dp,
    ruleVerticalMargin = 16,
    listPaddingStart = 20,
    listTopMargin = 14,
    listBottomMargin = 14,
    listItemTopMargin = 0,
    orderedItemExtraPaddingStart = 0,
    nestedListPaddingStart = 20,
    nestedListTopMargin = 0,
    headingSpecs = listOf(
        HeadingSpec(34.2f, FontWeight.Bold, 18, 18, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.3f),
        HeadingSpec(28.8f, FontWeight.Bold, 43, 18, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.3f),
        HeadingSpec(23.4f, FontWeight.SemiBold, 36, 18, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.3f),
        HeadingSpec(20.7f, FontWeight.SemiBold, 32, 18, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.3f),
        HeadingSpec(18f, FontWeight.SemiBold, 29, 18, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.3f),
        HeadingSpec(18f, FontWeight.SemiBold, 29, 18, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = true, lineHeightMultiplier = 1.3f),
    ),
    quoteBorderWidth = 3.dp,
    codeBlockBorderColor = Color(0xFFE7EAED),
    codeBlockBorderWidth = 1.dp,
)

/** Chocolate's piece icon decoded once for preview headings; null when decoding fails. */
private val chocolateIcon: ImageBitmap? by lazy {
    runCatching {
        val encoded = Base64.getDecoder().decode(ChocolateThemeIconPngBase64)
        Bitmap.makeFromImage(Image.makeFromEncoded(encoded)).asComposeImageBitmap()
    }.getOrNull()
}

/** Chocolate preview styles adapted from juejin-markdown-theme-qklhk at commit 4f2a290.
 * The `.markdown-body` grid background is not rendered: the preview is a block flow with no
 * body wrapper. The h5/h6 trailing numbered circles, the strong 「」 brackets, and the em-strong
 * highlight have no preview equivalent. List items keep the body color although the stylesheet
 * grays them (#858585); the export carries the gray. Heading top margins fall back to the
 * browser defaults the stylesheet leaves in place.
 */
internal val ChocolatePreviewStyles = MarkdownPreviewStyles(
    bodyColor = Color(0xFF412C0C),
    // Unused: no chocolate heading takes the muted color.
    mutedColor = Color(0xFFA37400),
    // The stylesheet tints each level differently (#664900/#614500/#a37400); h1's tone leads.
    headingColor = Color(0xFF664900),
    themeColor = Color(0xFF6D4E00),
    linkColor = Color(0xFF755300),
    boldColor = Color(0xFFC28A00),
    // The stylesheet underlines links with a 1px bottom border; approximated as an underline.
    linkUnderlined = true,
    linkHasIcon = false,
    firstLetterCapitalized = false,
    h1HasPrefix = false,
    h1Centered = false,
    headingBorderColor = Color(0xFF8F6600),
    headingBottomBorderColor = Color(0xFF6D4E00),
    bodyFontSize = 15.sp,
    bodyLineHeight = 26.25.sp,
    paragraphTopMargin = 0,
    paragraphBottomMargin = 16,
    quoteParagraphTopMargin = 0,
    quoteParagraphBottomMargin = 0,
    quoteColor = Color(0xFFFFF6E0),
    quoteBackground = Color(0x80BD8600),
    quoteBorder = Color(0xFFFFD87A),
    quoteHasBackground = true,
    quoteHasMarks = true,
    quoteHasHover = false,
    quotePaddingStart = 20,
    quotePaddingEnd = 20,
    quotePaddingTop = 20,
    quotePaddingBottom = 20,
    quoteVerticalMargin = 20,
    quoteNestedVerticalMargin = 20,
    inlineCodeColor = Color(0xFF996D00),
    inlineCodeBackground = Color(0x4D826200),
    // WeiSome floor: absolute 14sp (14/15 of the 15sp body), not the upstream 0.87em scale.
    inlineCodeFontScale = 14f / 15f,
    inlineCodeCornerRadius = 4.dp,
    codeBlockTopMargin = 15,
    codeBlockBottomMargin = 15,
    codeBlockCornerShape = androidx.compose.foundation.shape.RoundedCornerShape(0.dp),
    codeBlockPaddingVertical = 15,
    codeBlockPaddingHorizontal = 12,
    // WeiSome floor: 14sp instead of the upstream 12px; line height evened to 22sp.
    codeBlockFontSize = 14.sp,
    codeBlockLineHeight = 22.sp,
    strikethroughColor = Color(0xFFC28A00),
    tableBorderColor = Color(0x1A482A0A),
    tableBorderWidth = 1.dp,
    tableHeaderBackground = Color(0xFFF6F6F6),
    tableHeaderColor = Color.Black,
    tableHeaderFontWeight = FontWeight.Bold,
    // The stylesheet stripes no body rows.
    tableStripeBackground = Color.Transparent,
    tableCellPaddingHorizontal = 7,
    tableCellPaddingVertical = 12,
    // WeiSome floor: 14sp instead of the upstream 12px.
    tableFontSize = 14.sp,
    tableLineHeight = 24.sp,
    ruleIsGradient = false,
    ruleGradient = emptyList(),
    ruleSolidColor = Color(0xFF805B00),
    ruleHeight = 1.dp,
    ruleVerticalMargin = 32,
    listPaddingStart = 28,
    listTopMargin = 15,
    listBottomMargin = 15,
    listItemTopMargin = 0,
    orderedItemExtraPaddingStart = 6,
    nestedListPaddingStart = 28,
    nestedListTopMargin = 3,
    headingSpecs = listOf(
        // WeiSome: h1 is 24sp with the shared 1.5 line height (36sp, even); keeps the 10px bottom
        // margin and drops the shared 5px padding-bottom.
        HeadingSpec(24f, FontWeight.Bold, 17, 10, borderLeft = false, borderWidth = 0.dp, borderBottom = true, muted = false, lineHeightMultiplier = 1.5f, borderBottomWidth = 5.dp),
        HeadingSpec(20f, FontWeight.Bold, 17, 15, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
        HeadingSpec(18f, FontWeight.Bold, 20, 0, borderLeft = true, borderWidth = 5.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
        HeadingSpec(17f, FontWeight.Bold, 23, 15, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
        HeadingSpec(14f, FontWeight.Bold, 23, 15, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
        // WeiSome floor: 14sp instead of the upstream 12px.
        HeadingSpec(14f, FontWeight.Bold, 28, 15, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
    ),
    h1Icon = chocolateIcon,
    h2Icon = chocolateIcon,
    quoteBorderWidth = 1.dp,
    quoteCornerRadius = 10.dp,
    quoteHasBoxBorder = true,
    quoteMarkColor = Color(0xFFCC9100),
    quoteMarkFontSize = 34.sp,
    quoteMarkWeight = FontWeight.Bold,
    // Anchors the 34sp mark's ink at the stylesheet's left: 5px; text start spacing is then
    // derived from the measured glyph width, keeping the wide ❝ clear of the first characters.
    quoteMarkInkTop = 5.dp,
    quoteOpenMark = "\u275D",
    quoteCloseMark = "\u275E",
)

/** Yu preview styles adapted from juejin-markdown-theme-yu at commit 1e3096f.
 * The `.markdown-body` grid background is not rendered: the preview is a block flow with no
 * body wrapper. The strong `·` dashes have no preview equivalent. Headings keep the body color;
 * list markers use the widget's default tone although the stylesheet tints them (#ee69a9); the
 * export carries the tint. The h2 font-size falls back to the browser default (1.5em).
 * Deliberate deviations from the upstream stylesheet: h1 is sized 28px, the smallest font is
 * clamped to 14px (h6, inline code, code blocks, and table cells), emoji prefixes match their
 * heading size, and heading line-heights are absolute even-valued sp.
 */
internal val YuPreviewStyles = MarkdownPreviewStyles(
    bodyColor = Color(0xFF5F6368),
    // Unused: no yu heading takes the muted color.
    mutedColor = Color(0xFF666666),
    headingColor = Color(0xFF5F6368),
    themeColor = Color(0xFFFD79A8),
    linkColor = Color(0xFFFD79A8),
    boldColor = Color(0xFFFD79A8),
    // The stylesheet underlines links with a 1px bottom border; approximated as an underline.
    linkUnderlined = true,
    linkHasIcon = false,
    firstLetterCapitalized = false,
    h1HasPrefix = false,
    h1Centered = false,
    headingBorderColor = Color(0xFFFD79A8),
    headingBottomBorderColor = Color(0xFFECECEC),
    bodyFontSize = 15.sp,
    bodyLineHeight = 26.25.sp,
    paragraphTopMargin = 22,
    paragraphBottomMargin = 22,
    quoteParagraphTopMargin = 10,
    quoteParagraphBottomMargin = 10,
    quoteColor = Color(0xFF666666),
    quoteBackground = Color(0x1AFD79A8),
    quoteBorder = Color(0xFFEE69A9),
    quoteHasBackground = true,
    quoteHasMarks = true,
    quoteHasHover = false,
    quotePaddingStart = 27,
    quotePaddingEnd = 23,
    quotePaddingTop = 23,
    quotePaddingBottom = 23,
    quoteVerticalMargin = 22,
    quoteNestedVerticalMargin = 22,
    inlineCodeColor = Color(0xFFFF502C),
    inlineCodeBackground = Color(0xFFFFF5F5),
    // 14px at the 15px body (upstream 0.87em ≈ 13px is below the WeiSome 14px floor).
    inlineCodeFontScale = 14f / 15f,
    inlineCodeCornerRadius = 2.dp,
    codeBlockTopMargin = 15,
    codeBlockBottomMargin = 15,
    codeBlockCornerShape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    codeBlockPaddingVertical = 15,
    codeBlockPaddingHorizontal = 12,
    codeBlockFontSize = 14.sp,
    codeBlockLineHeight = 24.sp,
    strikethroughColor = Color(0xFF5F6368),
    tableBorderColor = Color(0xFFF6F6F6),
    tableBorderWidth = 1.dp,
    tableHeaderBackground = Color(0x1AFD79A8),
    tableHeaderColor = Color(0xFFFD79A8),
    tableHeaderFontWeight = FontWeight.Bold,
    tableStripeBackground = Color(0xFFFCFCFC),
    tableCellPaddingHorizontal = 7,
    tableCellPaddingVertical = 12,
    tableFontSize = 14.sp,
    tableLineHeight = 24.sp,
    ruleIsGradient = false,
    ruleGradient = emptyList(),
    ruleSolidColor = Color(0x80FD79A8),
    ruleHeight = 1.dp,
    ruleVerticalMargin = 32,
    listPaddingStart = 28,
    listTopMargin = 15,
    listBottomMargin = 15,
    listItemTopMargin = 0,
    orderedItemExtraPaddingStart = 6,
    nestedListPaddingStart = 28,
    nestedListTopMargin = 3,
    headingSpecs = listOf(
        HeadingSpec(28f, FontWeight.Bold, 35, 5, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f, lineHeight = 42.sp, prefixEmoji = "\uD83E\uDD84", prefixEmojiSize = 28f, prefixEmojiLeft = 0),
        HeadingSpec(22.5f, FontWeight.Bold, 35, 34, borderLeft = false, borderWidth = 0.dp, borderBottom = true, muted = false, lineHeightMultiplier = 1.5f, lineHeight = 34.sp, prefixEmoji = "\uD83D\uDC33", prefixEmojiSize = 22.5f, prefixEmojiLeft = 8, borderBottomGap = 24.dp),
        HeadingSpec(18f, FontWeight.Bold, 35, 10, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f, lineHeight = 28.sp, prefixEmoji = "\uD83D\uDC04", prefixEmojiSize = 18f, prefixEmojiLeft = 8),
        HeadingSpec(16f, FontWeight.Bold, 35, 15, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f, lineHeight = 24.sp, prefixEmoji = "\uD83E\uDDA5", prefixEmojiSize = 16f, prefixEmojiLeft = 8),
        HeadingSpec(14f, FontWeight.Bold, 35, 15, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f, lineHeight = 22.sp, prefixEmoji = "\uD83E\uDDA9", prefixEmojiSize = 14f, prefixEmojiLeft = 9),
        HeadingSpec(14f, FontWeight.Bold, 5, 15, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f, lineHeight = 22.sp, prefixEmoji = "\uD83D\uDC27", prefixEmojiSize = 14f, prefixEmojiLeft = 10),
    ),
    quoteBorderWidth = 4.dp,
    quoteMarkColor = Color(0xCCFD79A8),
    quoteMarkFontSize = 27.sp,
    quoteMarkWeight = FontWeight.Normal,
    quoteOpenMark = "\u275D",
    quoteCloseMark = "\u275E",
)

/** Cyanosis preview styles adapted from juejin-markdown-theme-cyanosis at commit 6b814ea.
 * The `.markdown-body` grid background is not rendered: the preview is a block flow with no
 * body wrapper. The h2 「」 marks and the h3 » prefix have no preview equivalent; the export
 * renders them as real elements. The h2 border gap mirrors the export's 10px padding-bottom.
 * The export sizes the table header at the thead's 14px while the preview shares
 * the 12sp cell size. The hr's scissors ornament and the row hover are not rendered. The
 * stylesheet underlines links with a 1px bottom border; approximated as an underline.
 */
internal val CyanosisPreviewStyles = MarkdownPreviewStyles(
    bodyColor = Color(0xFF353535),
    // Unused: no cyanosis heading takes the muted color.
    mutedColor = Color(0xFF8C8C8C),
    headingColor = Color(0xFF005BB7),
    themeColor = Color(0xFF2196F3),
    linkColor = Color(0xFF3DA8F5),
    boldColor = Color(0xFF2196F3),
    linkUnderlined = true,
    linkHasIcon = false,
    firstLetterCapitalized = false,
    h1HasPrefix = false,
    h1Centered = false,
    headingBorderColor = Color.Transparent,
    headingBottomBorderColor = Color(0xFFECECEC),
    bodyFontSize = 14.sp,
    bodyLineHeight = 24.5.sp,
    paragraphTopMargin = 16,
    paragraphBottomMargin = 16,
    quoteParagraphTopMargin = 10,
    quoteParagraphBottomMargin = 10,
    quoteColor = Color(0xFF8C8C8C),
    quoteBackground = Color(0xFFF0FDFF),
    quoteBorder = Color(0xFF2196F3),
    quoteHasBackground = true,
    quoteHasMarks = false,
    quoteHasHover = false,
    // 24 = the stylesheet's 20px padding-left plus the 4px left border the widget paints inside.
    quotePaddingStart = 24,
    quotePaddingEnd = 20,
    quotePaddingTop = 1,
    quotePaddingBottom = 1,
    quoteVerticalMargin = 22,
    quoteNestedVerticalMargin = 22,
    inlineCodeColor = Color(0xFFC2185B),
    inlineCodeBackground = Color(0xFFFFF4F4),
    inlineCodeFontScale = 0.87f,
    inlineCodeCornerRadius = 2.dp,
    codeBlockTopMargin = 14,
    codeBlockBottomMargin = 14,
    codeBlockCornerShape = androidx.compose.foundation.shape.RoundedCornerShape(0.dp),
    codeBlockPaddingVertical = 16,
    codeBlockPaddingHorizontal = 12,
    codeBlockFontSize = 12.sp,
    codeBlockLineHeight = 21.sp,
    strikethroughColor = Color(0xFFCCCCCC),
    tableBorderColor = Color(0xFFC3E0FD),
    tableBorderWidth = 1.dp,
    tableHeaderBackground = Color(0xFFDFF0FF),
    tableHeaderColor = Color(0xFF005BB7),
    tableHeaderFontWeight = FontWeight.Bold,
    tableStripeBackground = Color(0xFFF7FBFF),
    tableCellPaddingHorizontal = 8,
    tableCellPaddingVertical = 12,
    tableFontSize = 12.sp,
    tableLineHeight = 24.sp,
    ruleIsGradient = true,
    ruleGradient = listOf(
        Color(0xFF007FFF),
        Color(0x4DFF0000),
        Color(0x1AFFFFFF),
        Color(0x4DFF0000),
        Color(0xFF007FFF),
    ),
    ruleSolidColor = Color(0xFF007FFF),
    ruleHeight = 1.dp,
    ruleVerticalMargin = 32,
    ruleWidthFraction = 0.98f,
    listPaddingStart = 28,
    listTopMargin = 16,
    listBottomMargin = 16,
    listItemTopMargin = 0,
    orderedItemExtraPaddingStart = 6,
    nestedListPaddingStart = 28,
    nestedListTopMargin = 4,
    headingSpecs = listOf(
        HeadingSpec(30f, FontWeight.Bold, 36, 10, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
        HeadingSpec(24f, FontWeight.Bold, 36, 10, borderLeft = false, borderWidth = 0.dp, borderBottom = true, muted = false, lineHeightMultiplier = 1.5f, borderBottomGap = 10.dp),
        HeadingSpec(20f, FontWeight.Bold, 30, 10, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
        HeadingSpec(16f, FontWeight.Bold, 24, 10, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
        HeadingSpec(14f, FontWeight.Bold, 18, 10, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
        HeadingSpec(12f, FontWeight.Bold, 12, 10, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
    ),
    quoteBorderWidth = 4.dp,
)

/** V-Green preview styles adapted from juejin-markdown-theme-v-green at commit 015f88b.
 * The heading `:first-child` negative top margin and the green ordered-list `::marker` have no
 * preview equivalent. The unordered list's green `•` bullets are drawn via `li::before`; the
 * widget renders standard bullets. The link `⇲` glyph renders after the anchor text (upstream
 * places it before). The heading text stays body-colored with a green `#` prefix; the preview
 * prefix mechanism only covers h1. The `details`/`summary` rules have no model element.
 */
internal val VGreenPreviewStyles = MarkdownPreviewStyles(
    bodyColor = Color(0xFF333333),
    // Unused: no v-green heading takes the muted color.
    mutedColor = Color(0xFF8C8C8C),
    headingColor = Color(0xFF333333),
    themeColor = Color(0xFF3EAF7C),
    linkColor = Color(0xFF3EAF7C),
    boldColor = Color(0xFF3EAF7C),
    linkUnderlined = false,
    linkHasIcon = true,
    firstLetterCapitalized = false,
    h1HasPrefix = true,
    h1Centered = false,
    headingBorderColor = Color.Transparent,
    headingBottomBorderColor = Color(0xFFECECEC),
    bodyFontSize = 15.sp,
    bodyLineHeight = 26.25.sp,
    paragraphTopMargin = 22,
    paragraphBottomMargin = 22,
    quoteParagraphTopMargin = 10,
    quoteParagraphBottomMargin = 10,
    quoteColor = Color(0xFF666666),
    quoteBackground = Color(0xFFF8F8F8),
    quoteBorder = Color(0xFF42B983),
    quoteHasBackground = true,
    quoteHasMarks = false,
    quoteHasHover = false,
    // 31 = the stylesheet's 23px padding-left plus the 8px (0.5rem) left border the widget paints inside.
    quotePaddingStart = 31,
    quotePaddingEnd = 23,
    quotePaddingTop = 1,
    quotePaddingBottom = 1,
    quoteVerticalMargin = 22,
    quoteNestedVerticalMargin = 22,
    inlineCodeColor = Color(0xFF3EAF7C),
    inlineCodeBackground = Color(0x0D1B1F23),
    inlineCodeFontScale = 0.85f,
    inlineCodeCornerRadius = 3.dp,
    codeBlockTopMargin = 15,
    codeBlockBottomMargin = 15,
    codeBlockCornerShape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
    codeBlockBorderColor = Color(0xFF3EAF7C),
    codeBlockBorderWidth = 2.dp,
    codeBlockPaddingVertical = 15,
    codeBlockPaddingHorizontal = 12,
    codeBlockFontSize = 12.sp,
    codeBlockLineHeight = 21.sp,
    strikethroughColor = Color(0xFF333333),
    tableBorderColor = Color(0xFF3EAF7C),
    tableBorderWidth = 1.dp,
    tableHeaderBackground = Color(0xFF3EAF7C),
    tableHeaderColor = Color(0xFFFFFFFF),
    tableHeaderFontWeight = FontWeight.Bold,
    tableStripeBackground = Color(0x333EAF7C),
    tableCellPaddingHorizontal = 7,
    tableCellPaddingVertical = 12,
    tableFontSize = 12.sp,
    tableLineHeight = 24.sp,
    ruleIsGradient = false,
    ruleGradient = emptyList(),
    ruleSolidColor = Color(0xFF3EAF7C),
    ruleHeight = 1.dp,
    ruleVerticalMargin = 32,
    ruleWidthFraction = 1.0f,
    listPaddingStart = 28,
    listTopMargin = 15,
    listBottomMargin = 15,
    listItemTopMargin = 0,
    orderedItemExtraPaddingStart = 6,
    nestedListPaddingStart = 28,
    nestedListTopMargin = 3,
    headingSpecs = listOf(
        HeadingSpec(32f, FontWeight.Bold, 35, 10, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
        HeadingSpec(28f, FontWeight.Bold, 35, 18, borderLeft = false, borderWidth = 0.dp, borderBottom = true, muted = false, lineHeightMultiplier = 1.5f, borderBottomGap = 8.dp),
        HeadingSpec(24f, FontWeight.Bold, 35, 10, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
        HeadingSpec(20f, FontWeight.Bold, 35, 15, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
        HeadingSpec(16f, FontWeight.Bold, 35, 15, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
        HeadingSpec(15f, FontWeight.Bold, 5, 15, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.5f),
    ),
    quoteBorderWidth = 8.dp,
)

/** Cyan preview styles adapted from juejin-markdown-theme-channing-cyan at commit c843c2f.
 * The `.markdown-body` checkered grid background is not rendered: the preview is a block flow with
 * no body wrapper. The h1 background glow and animated gradient shadow, the h2 leaf icon, and the
 * h3 underline bar and circle have no preview equivalent; the export renders them as real
 * elements. The `strong` 「」 marks and `figcaption` rules have no model element. Paragraphs use a
 * 14px size with 2px letter/word spacing in the export; the preview shares the 15sp body type.
 * The h2 border gap mirrors the export's 12px padding-bottom. Code-block colors
 * continue to come from the selected code theme.
 */
internal val CyanPreviewStyles = MarkdownPreviewStyles(
    bodyColor = Color(0xFF2B2B2B),
    // Unused: no cyan heading takes the muted color.
    mutedColor = Color(0xFF8C8C8C),
    headingColor = Color(0xFF4DD0E1),
    themeColor = Color(0xFF4DD0E1),
    linkColor = Color(0xFF4DD0E1),
    boldColor = Color(0xFF26C6DA),
    linkUnderlined = true,
    linkHasIcon = false,
    firstLetterCapitalized = false,
    h1HasPrefix = false,
    h1Centered = true,
    headingBorderColor = Color.Transparent,
    headingBottomBorderColor = Color(0xFF4DD0E1),
    bodyFontSize = 15.sp,
    bodyLineHeight = 26.25.sp,
    paragraphTopMargin = 22,
    paragraphBottomMargin = 22,
    quoteParagraphTopMargin = 22,
    quoteParagraphBottomMargin = 22,
    quoteColor = Color(0xFF595959),
    quoteBackground = Color(0x264DD0E1),
    quoteBorder = Color(0xFF26C6DA),
    quoteHasBackground = true,
    quoteHasMarks = true,
    quoteHasHover = false,
    quoteMarkColor = Color(0xB34DD0E1),
    quoteMarkFontSize = 30.sp,
    quoteMarkWeight = FontWeight.Bold,
    quoteOpenMark = "\u275D",
    quoteCloseMark = "\u275E",
    // 36 = the stylesheet's 32px padding-left plus the 4px left border the widget paints inside.
    quotePaddingStart = 36,
    quotePaddingEnd = 32,
    quotePaddingTop = 24,
    quotePaddingBottom = 24,
    quoteVerticalMargin = 30,
    quoteNestedVerticalMargin = 30,
    inlineCodeColor = Color(0xFF26C6DA),
    inlineCodeBackground = Color(0x144DD0E1),
    inlineCodeFontScale = 1.0f,
    inlineCodeCornerRadius = 2.dp,
    codeBlockTopMargin = 16,
    codeBlockBottomMargin = 16,
    codeBlockCornerShape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
    codeBlockPaddingVertical = 15,
    codeBlockPaddingHorizontal = 12,
    codeBlockFontSize = 12.sp,
    codeBlockLineHeight = 21.sp,
    strikethroughColor = Color(0xFF4DD0E1),
    tableBorderColor = Color(0xFFF6F6F6),
    tableBorderWidth = 1.dp,
    tableHeaderBackground = Color(0xFFF6F6F6),
    tableHeaderColor = Color(0xFF000000),
    tableHeaderFontWeight = FontWeight.Bold,
    tableStripeBackground = Color(0x0D4DD0E1),
    tableCellPaddingHorizontal = 7,
    tableCellPaddingVertical = 12,
    tableFontSize = 12.sp,
    tableLineHeight = 24.sp,
    ruleIsGradient = false,
    ruleGradient = emptyList(),
    ruleSolidColor = Color(0xFF4DD0E1),
    ruleHeight = 1.dp,
    ruleVerticalMargin = 32,
    ruleWidthFraction = 1.0f,
    listPaddingStart = 28,
    listTopMargin = 15,
    listBottomMargin = 15,
    listItemTopMargin = 0,
    orderedItemExtraPaddingStart = 6,
    nestedListPaddingStart = 28,
    nestedListTopMargin = 3,
    headingSpecs = listOf(
        HeadingSpec(30f, FontWeight.Bold, 30, 40, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.2f),
        HeadingSpec(24f, FontWeight.Bold, 42, 42, borderLeft = false, borderWidth = 0.dp, borderBottom = true, muted = false, lineHeightMultiplier = 1.2f, borderBottomGap = 12.dp),
        HeadingSpec(18f, FontWeight.Bold, 39, 14, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.2f),
        HeadingSpec(16f, FontWeight.Bold, 65, 40, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.2f),
        HeadingSpec(15f, FontWeight.Bold, 65, 40, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.2f),
        HeadingSpec(15f, FontWeight.Bold, 35, 40, borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false, lineHeightMultiplier = 1.2f),
    ),
    quoteBorderWidth = 4.dp,
)

/**
 * Claudette preview styles; values mirror docs/theme/claudette/claudette.css (light variant),
 * with em/rem converted to px and non-integers rounded to the nearest even integer.
 * Heading sizes are retuned to an even 28-18 ladder (h1-h4 at weight 460, h5-h6 at weight 400)
 * with line heights scaled proportionally to the upstream ratios.
 * Known platform gaps: link underline ink follows the text color (no separate decoration
 * color), h6 letter-spacing is not applied, and images carry no hairline ring.
 */
internal val ClaudettePreviewStyles = MarkdownPreviewStyles(
    bodyColor = Color(0xFF141413),
    mutedColor = Color(0xFF73726C),
    headingColor = Color(0xFF141413),
    themeColor = Color(0xFFD97757),
    linkColor = Color(0xFF141413),
    boldColor = null,
    linkUnderlined = true,
    linkHasIcon = true,
    linkIconText = "\u2197",
    linkIconTextColor = Color(0xFF87867F),
    linkIconTextSize = 12.sp,
    firstLetterCapitalized = false,
    h1HasPrefix = false,
    h1Centered = false,
    headingBorderColor = Color(0xFFD97757),
    headingBottomBorderColor = Color(0x1F1F1E1D),
    bodyFontSize = 17.sp,
    bodyLineHeight = 28.sp,
    paragraphTopMargin = 0,
    paragraphBottomMargin = 18,
    quoteParagraphTopMargin = 0,
    quoteParagraphBottomMargin = 18,
    quoteColor = Color(0xFF52514E),
    quoteBackground = Color.Transparent,
    quoteBorder = Color(0x8CD97757),
    quoteHasBackground = false,
    quoteHasMarks = true,
    quoteHasHover = false,
    quoteMarkColor = Color(0x59D97757),
    quoteMarkFontSize = 36.sp,
    quoteMarkWeight = FontWeight.Normal,
    quoteOpenMark = "\u201C",
    quoteHasClosingMark = false,
    // Anchors the opening mark's ink at 10px from the card corner; the export CSS places it
    // at left 10px / top -2px, so the mark sits slightly lower here (faint decoration only).
    quoteMarkInkTop = 10.dp,
    quotePaddingStart = 32,
    quotePaddingEnd = 0,
    quotePaddingTop = 4,
    quotePaddingBottom = 4,
    quoteVerticalMargin = 26,
    quoteNestedVerticalMargin = 14,
    quoteBorderWidth = 2.dp,
    quoteFontSerif = true,
    inlineCodeColor = Color(0xFF9C4A21),
    inlineCodeBackground = Color(0x1AD97757),
    // 14sp inline-code text against the 17sp body, the 0.875em ratio converted to even px.
    inlineCodeFontScale = 14f / 17f,
    inlineCodeCornerRadius = 4.dp,
    codeBlockTopMargin = 24,
    codeBlockBottomMargin = 24,
    codeBlockCornerShape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    codeBlockPaddingVertical = 16,
    codeBlockPaddingHorizontal = 20,
    codeBlockFontSize = 14.sp,
    codeBlockLineHeight = 22.sp,
    codeBlockBorderColor = Color(0x1F1F1E1D),
    codeBlockBorderWidth = 1.dp,
    codeBlockHasLangLabel = true,
    codeBlockLangLabelBackground = Color(0xFFECE9DF),
    codeBlockLangLabelTextColor = Color(0xFF73726C),
    strikethroughColor = Color(0xFF87867F),
    tableBorderColor = Color(0x1F1F1E1D),
    tableBorderWidth = 1.dp,
    tableHeaderBackground = Color(0xFFF5F4ED),
    tableHeaderColor = Color(0xFF73726C),
    tableHeaderFontWeight = FontWeight(460),
    tableStripeBackground = Color.Transparent,
    tableCellPaddingHorizontal = 12,
    tableCellPaddingVertical = 10,
    tableFontSize = 15.sp,
    tableLineHeight = 24.sp,
    tableCornerRadius = 8.dp,
    tableHeaderUppercase = true,
    tableHeaderFontSize = 12.sp,
    ruleIsGradient = false,
    ruleGradient = emptyList(),
    ruleSolidColor = Color(0x1F1F1E1D),
    ruleHeight = 1.dp,
    ruleVerticalMargin = 40,
    ruleWidthFraction = 1.0f,
    ruleCenterGlyph = "\u2733",
    listPaddingStart = 26,
    listTopMargin = 0,
    listBottomMargin = 18,
    listItemTopMargin = 6,
    orderedItemExtraPaddingStart = 0,
    nestedListPaddingStart = 26,
    nestedListTopMargin = 4,
    unorderedMarkerColor = Color(0xFFD97757),
    orderedMarkerColor = Color(0xFF73726C),
    imageCornerRadius = 8.dp,
    headingFontSerif = true,
    leadFontSize = 20.sp,
    leadLineHeight = 30.sp,
    leadColor = Color(0xFF52514E),
    headingSpecs = listOf(
        // h1: short 40px clay underline sits 16px below the text (padding-bottom 0.45em).
        HeadingSpec(
            28f, FontWeight(460), 32, 32,
            borderLeft = false, borderWidth = 0.dp, borderBottom = true, muted = false,
            lineHeightMultiplier = 1.1f, lineHeight = 32.sp,
            borderBottomWidth = 2.dp, borderBottomGap = 16.dp,
            shortRuleWidth = 40.dp, borderBottomColor = Color(0xFFD97757),
        ),
        HeadingSpec(
            26f, FontWeight(460), 18, 18,
            borderLeft = false, borderWidth = 0.dp, borderBottom = true, muted = false,
            lineHeightMultiplier = 1.2f, lineHeight = 32.sp,
            borderBottomWidth = 1.dp, borderBottomGap = 10.dp, borderBottomColor = Color(0x1F1F1E1D),
        ),
        HeadingSpec(
            24f, FontWeight(460), 14, 14,
            borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false,
            lineHeightMultiplier = 1.3f, lineHeight = 30.sp, prefixDot = true,
        ),
        HeadingSpec(
            22f, FontWeight(460), 12, 12,
            borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false,
            lineHeightMultiplier = 1.3f, lineHeight = 30.sp,
        ),
        HeadingSpec(
            20f, FontWeight(400), 8, 8,
            borderLeft = false, borderWidth = 0.dp, borderBottom = false, muted = false,
            lineHeightMultiplier = 1.4f, lineHeight = 28.sp,
        ),
        // h6: small uppercase label behind a 2px clay bar; 6dp text gap makes the ink start 8px from the edge.
        HeadingSpec(
            18f, FontWeight(400), 6, 6,
            borderLeft = true, borderWidth = 2.dp, borderBottom = false, muted = true,
            lineHeightMultiplier = 1.4f, lineHeight = 24.sp,
            uppercase = true, borderLeftTextGap = 6.dp,
        ),
    ),
)

/**
 * Uppercases every character of the text-bearing inlines, reproducing claudette's
 * `text-transform: uppercase` labels. Code and image inlines are skipped because
 * their content is not plain flowing text.
 */
internal fun uppercaseInlines(inlines: List<MarkdownInline>): List<MarkdownInline> = inlines.map { inline ->
    when (inline) {
        is MarkdownInline.Text -> inline.copy(value = inline.value.uppercase())
        is MarkdownInline.Bold -> inline.copy(value = inline.value.uppercase())
        is MarkdownInline.Italic -> inline.copy(value = inline.value.uppercase())
        is MarkdownInline.Strikethrough -> inline.copy(value = inline.value.uppercase())
        is MarkdownInline.Link -> inline.copy(text = inline.text.uppercase())
        is MarkdownInline.Code, is MarkdownInline.Image -> inline
    }
}

/** Converts a packed RGB value to an opaque Compose color. */
internal fun Int.toComposeColor(): Color = Color(0xFF000000L or (toLong() and 0xFFFFFFL))
