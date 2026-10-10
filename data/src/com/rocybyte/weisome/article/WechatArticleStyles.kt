package com.rocybyte.weisome.article

import java.util.Locale
import kotlin.math.roundToInt

/**
 * Per-theme inline CSS for the WeChat HTML export.
 * Each theme mirrors its canonical stylesheet in docs/theme/: HYDROGEN follows docs/theme/hydrogen/hydrogen.scss
 * (DawnLck/juejin-markdown-theme-hydrogen@b3f86fb), GITHUB follows docs/theme/github/github.scss
 * (primer/css src/markdown, light values resolved), SMART_BLUE follows docs/theme/smart-blue/smart-blue.css
 * (cumt-robin/juejin-markdown-theme-smart-blue@f740565), and TYPORA_PAPER follows
 * docs/theme/typora-paper at lisitan/esther-obsidian-typora-themes@8c4f912, and RIM follows
 * docs/theme/rim/rim.css at Rimseg/typora-theme-rim@f0d54ef, and CHOCOLATE follows
 * docs/theme/chocolate/chocolate.scss at qklhk/juejin-markdown-theme-qklhk@4f2a290, and YU follows
 * docs/theme/yu/yu.scss at jianghurong/juejin-markdown-theme-yu@1e3096f, and CYANOSIS follows
 * docs/theme/cyanosis/cyanosis.scss at linxsbox/juejin-markdown-theme-cyanosis@6b814ea, and CYAN
 * follows docs/theme/cyan/channing-cyan.scss at ChanningHan/juejin-markdown-theme-channing-cyan@c843c2f,
 * and V_GREEN follows docs/theme/v-green/v-green.scss at DawnLck/juejin-markdown-theme-v-green@015f88b,
 * and CLAUDETTE follows docs/theme/claudette/claudette.css at CookPiu/typora-theme-claudette@0a6c75a (light variant).
 * Keep the shared-ui Compose preview (MarkdownPreviewStyles) aligned with these values.
 */
internal abstract class MarkdownExportStyles {
    /** Base body text color. */
    abstract val fontColor: String

    /** Builds the inline CSS used to render a heading at the requested level. */
    abstract fun headingCss(level: Int): String

    /**
     * Builds the heading CSS with the document-start override applied. When [atDocumentStart] is
     * true the heading opens the document and its top margin is dropped, mirroring the canonical
     * stylesheet's `:first-child { margin-top: 0 }` rule. The default ignores the flag; only themes
     * whose stylesheet carries that rule override this form.
     */
    open fun headingCss(level: Int, atDocumentStart: Boolean): String = headingCss(level)

    /** Inline CSS for the decorative prefix rendered before level-one headings (h1HasPrefix only). */
    open val h1PrefixCss: String get() = ""

    /** Returns real leading markup for heading decorations that inline CSS cannot express. */
    open fun headingPrefixHtml(level: Int): String = if (level == 1 && h1HasPrefix) {
        "<span style=\"$h1PrefixCss\">#</span>"
    } else {
        ""
    }

    /** Returns real trailing markup for heading decorations that inline CSS cannot express. */
    open fun headingSuffixHtml(level: Int): String = ""

    abstract val paragraphCss: String
    abstract val quoteParagraphCss: String

    /** Paragraph CSS override for the lead paragraph directly after a level-one heading; null disables the rule. */
    open val leadParagraphCss: String? get() = null

    abstract val listCss: String
    abstract val listItemCss: String
    abstract val orderedListItemCss: String
    abstract val nestedListCss: String

    /** Task list items drop the list marker; the leading space keeps it composable with item css. */
    abstract val taskItemPrefixCss: String

    abstract val inlineCodeCss: String
    abstract val codeBlockCss: String

    /** Real-element decoration inserted before code content inside the preformatted frame. */
    open fun codeBlockHeaderHtml(language: CodeLanguage?): String = ""

    /** Inline CSS for the code element, colored with the active code theme's base text color and background. */
    abstract fun codeElementCss(codeTheme: CodeTheme): String
    abstract val emCss: String

    /** Inline CSS color for `<strong>`; null keeps the plain element, which inherits the body color. */
    open val boldColor: String? get() = null

    abstract val strikethroughCss: String
    abstract val linkCss: String

    /** Real-element link icon appended after anchors (linkHasIcon only). */
    open val linkIconSpan: String get() = ""

    abstract val imgCss: String

    /** Table images drop decorations that the surrounding cell already provides. */
    open val tableImgCss: String get() = imgCss

    abstract val blockquoteCss: String

    /** Blockquote margin override when nested inside another blockquote. */
    open val nestedBlockquoteCss: String get() = blockquoteCss

    /** Shared typography of the decorative opening and closing quote marks (quoteHasMarks only). */
    open val quoteOpenCss: String get() = ""
    open val quoteCloseCss: String get() = ""

    /** Whether a decorative closing quote is rendered alongside the opening quote. */
    open val quoteHasClosingMark: Boolean get() = true

    abstract val hrCss: String

    /** Real-element markup rendered inside the horizontal-rule div (claudette's centered star). */
    open val hrInnerHtml: String get() = ""

    abstract val tableCss: String
    abstract val thCss: String
    abstract val tdCss: String

    /** Even body rows carry the striped background because inline CSS cannot express nth-child. */
    abstract val stripedTdCss: String

    /** Whether level-one headings render the decorative "#" prefix. */
    abstract val h1HasPrefix: Boolean

    /** Whether anchors are followed by the theme's link icon span. */
    abstract val linkHasIcon: Boolean

    /** Whether blockquotes render decorative opening/closing quote marks. */
    abstract val quoteHasMarks: Boolean

    /** Glyphs inside the opening/closing quote mark spans (quoteHasMarks only). */
    open val quoteOpenMark: String get() = "\u201C"
    open val quoteCloseMark: String get() = "\u201D"

    /** Whether paragraphs and h2/h3 headings capitalize their first letter. */
    abstract val firstLetterCapitalized: Boolean
}

/** Returns the export style set for the requested Markdown theme. */
internal fun exportStylesFor(theme: MarkdownThemeId): MarkdownExportStyles = when (theme) {
    MarkdownThemeId.GITHUB -> GitHubExportStyles
    MarkdownThemeId.HYDROGEN -> HydrogenExportStyles
    MarkdownThemeId.SMART_BLUE -> SmartBlueExportStyles
    MarkdownThemeId.TYPORA_PAPER -> TyporaPaperExportStyles
    MarkdownThemeId.RIM -> RimExportStyles
    MarkdownThemeId.CHOCOLATE -> ChocolateExportStyles
    MarkdownThemeId.YU -> YuExportStyles
    MarkdownThemeId.CYANOSIS -> CyanosisExportStyles
    MarkdownThemeId.CYAN -> CyanExportStyles
    MarkdownThemeId.V_GREEN -> VGreenExportStyles
    MarkdownThemeId.CLAUDETTE -> ClaudetteExportStyles
}

/** Formats a packed RGB value as a six-digit CSS hexadecimal color. */
internal fun Int.toCssColor(): String = "#%06x".format(this and 0xFFFFFF)

private const val monospaceFont = "Menlo, Monaco, Consolas, 'Courier New', monospace"

/** Hydrogen export styles; values mirror docs/theme/hydrogen/hydrogen.scss. */
internal object HydrogenExportStyles : MarkdownExportStyles() {
    override val fontColor = "rgba(46, 36, 36, 0.87)"

    private data class HeadingSpec(
        val size: Int,
        val weight: Int,
        val top: Int,
        val bottom: Int,
        val padding: String,
        val borderLeft: Boolean,
    )

    /** Returns the hydrogen typography for a heading level from 1 to 6. */
    private fun heading(level: Int): HeadingSpec = when (level) {
        1 -> HeadingSpec(30, 500, 35, 5, "padding-bottom: 5px;", borderLeft = false)
        2 -> HeadingSpec(28, 400, 20, 10, "padding: 0 0 5px 10px;", borderLeft = true)
        3 -> HeadingSpec(24, 400, 15, 10, "padding-bottom: 0;", borderLeft = false)
        4 -> HeadingSpec(20, 500, 35, 10, "padding-bottom: 5px;", borderLeft = false)
        5 -> HeadingSpec(16, 400, 35, 10, "padding-bottom: 5px;", borderLeft = false)
        // hydrogen defines no h6 font-size; the body base of 16px is used instead.
        else -> HeadingSpec(16, 400, 5, 10, "padding-bottom: 5px;", borderLeft = false)
    }

    override fun headingCss(level: Int): String {
        val spec = heading(level)
        val border = if (spec.borderLeft) " border-left: 5px solid #454545;" else ""
        return "font-size: ${spec.size}px; font-weight: ${spec.weight}; line-height: 1.5; " +
            "margin: ${spec.top}px 0 ${spec.bottom}px; ${spec.padding}$border"
    }

    override val h1PrefixCss = "color: #1976d2; margin-right: 10px;"

    override val paragraphCss =
        "font-size: 16px; line-height: 1.75; margin: 22px 0; color: $fontColor; word-break: break-word;"

    /** Paragraph override applied to paragraphs nested inside a blockquote (margin: 10px 0). */
    override val quoteParagraphCss =
        "font-size: 16px; line-height: 1.75; margin: 10px 0; color: #666666; word-break: break-word;"

    // hydrogen leaves the outer list margin to the browser default (1em); 16px is its equivalent at body size.
    override val listCss = "padding-left: 28px; margin: 16px 0;"
    override val listItemCss = "font-size: 16px; line-height: 1.75; margin-bottom: 0; color: $fontColor;"
    override val orderedListItemCss = "font-size: 16px; line-height: 1.75; margin-bottom: 0; color: $fontColor; padding-left: 6px;"
    override val nestedListCss = "padding-left: 28px; margin: 3px 0 0;"
    override val taskItemPrefixCss = "list-style: none; "

    override val inlineCodeCss =
        "color: #c0341d; background-color: #fbe5e1; padding: 0.065em 0.4em; border-radius: 2px; " +
            "font-family: $monospaceFont; font-size: 0.87em; font-style: normal; " +
            "word-break: break-word; box-decoration-break: clone; -webkit-box-decoration-break: clone; overflow-wrap: anywhere;"

    override val codeBlockCss =
        "font-family: $monospaceFont; line-height: 1.75; border-radius: 0 4px; margin: 22px 0; " +
            "white-space: pre; overflow: auto;"

    override fun codeElementCss(codeTheme: CodeTheme) =
        "display: -webkit-box; min-width: 100%; box-sizing: border-box; overflow-x: auto; " +
            "font-weight: 400; font-size: 12px; padding: 15px 12px; margin: 0; word-break: normal; " +
            "white-space: pre; color: ${codeTheme.codeRgb.toCssColor()}; " +
            "background: ${codeTheme.backgroundRgb.toCssColor()}; border-radius: 0 4px;"

    /** Emphasis rendered as dot text-emphasis per hydrogen; italic kept from the default em semantics. */
    override val emCss = "font-style: italic; text-emphasis: dot; text-emphasis-position: under;"

    override val strikethroughCss = "color: rgba(0, 0, 0, 0.6);"

    override val linkCss =
        "color: #027fff; text-decoration: none; margin: 0 4px; padding-bottom: 4px; border-bottom: 2px solid transparent;"

    /** Real-element replacement of hydrogen's a::after link icon (18x18 inline SVG data URI). */
    override val linkIconSpan =
        "<span style=\"display: inline-block; width: 18px; height: 18px; margin-left: 4px; " +
            "vertical-align: middle; background-size: cover; background-repeat: no-repeat; " +
            "background-image: url('data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIyMiIgaGVpZ2h0PSIyMiIgdmlld0JveD0iMCAwIDIyIDIyIj4KICAgIDxnIGZpbGw9Im5vbmUiIGZpbGwtcnVsZT0iZXZlbm9kZCIgc3Ryb2tlPSIjMDI3RkZGIiBzdHJva2UtbGluZWNhcD0icm91bmQiPgogICAgICAgIDxwYXRoIGQ9Ik05LjgxNSA2LjQ0OGwxLjkzNi0xLjkzNmMxLjMzNy0xLjMzNiAzLjU4LTEuMjU5IDUuMDEzLjE3MyAxLjQzMiAxLjQzMiAxLjUxIDMuNjc2LjE3MyA1LjAxM2wtMS40NTIgMS40NTItLjk2OC45NjhjLTEuMzM3IDEuMzM2LTMuNTgxIDEuMjU5LTUuMDEzLS4xNzIiLz4KICAgICAgICA8cGF0aCBkPSJNMTEuMjY3IDE1LjM2N2wtMS45MzYgMS45MzZjLTEuMzM2IDEuMzM3LTMuNTggMS4yNi01LjAxMi0uMTczLTEuNDMyLTEuNDMyLTEuNTEtMy42NzYtLjE3My01LjAxMmwxLjQ1Mi0xLjQ1Mi45NjgtLjk2OGMxLjMzNi0xLjMzNyAzLjU4LTEuMjYgNS4wMTIuMTczIi8+CiAgICA8L2c+Cjwvc3ZnPgo=');\"></span>"

    /** Image styling including the Material elevation shadow from hydrogen's elevation mixin. */
    override val imgCss =
        "display: block; margin: 0 auto; max-width: 100%; border-radius: 2px; " +
            "box-shadow: 0 2px 4px -1px rgba(0, 0, 0, 0.2), 0 4px 5px 0 rgba(0, 0, 0, 0.14), 0 1px 10px 0 rgba(0, 0, 0, 0.12);"

    /** Table images drop the elevation shadow per hydrogen's `table img { box-shadow: none }`. */
    override val tableImgCss = "display: block; margin: 0 auto; max-width: 100%; border-radius: 2px;"

    override val blockquoteCss =
        "position: relative; color: #666666; padding: 5px 23px 1px; margin: 22px 0; " +
            "border-left: 4px solid #cbcbcb; background-color: rgba(200, 200, 200, 0.12);"

    /** Blockquote margin override when nested inside another blockquote (margin: 10px 0). */
    override val nestedBlockquoteCss =
        "position: relative; color: #666666; padding: 5px 23px 1px; margin: 10px 0; " +
            "border-left: 4px solid #cbcbcb; background-color: rgba(200, 200, 200, 0.12);"

    /** Shared typography of the decorative opening and closing quote marks. */
    private val quoteMarkCss =
        "position: absolute; font-size: 24px; font-weight: 800; line-height: 24px; color: #cbcbcb; opacity: 0.6;"

    override val quoteOpenCss = "$quoteMarkCss top: 4px; left: 6px;"
    override val quoteCloseCss = "$quoteMarkCss right: 8px; bottom: -8px;"

    override val hrCss =
        "position: relative; width: 98%; height: 1px; border: none; margin: 32px 0; " +
            "background-image: linear-gradient(to right, #dddddd, #999999, #dddddd); overflow: visible;"

    override val tableCss =
        "margin: 0 auto 10px; font-size: 12px; width: auto; max-width: 100%; overflow: auto; border: 2px solid #c6c6c6;"

    override val thCss =
        "background: #f6f6f6; color: #000000; text-align: left; padding: 12px 7px; line-height: 24px; font-size: 12px;"

    override val tdCss =
        "padding: 12px 7px; line-height: 24px; font-size: 12px; min-width: 120px;"

    override val stripedTdCss = "$tdCss background: #fcfcfc;"

    override val h1HasPrefix = true
    override val linkHasIcon = true
    override val quoteHasMarks = true
    override val firstLetterCapitalized = true
}

/** GitHub export styles; values mirror docs/theme/github/github.scss (primer/css markdown, light). */
internal object GitHubExportStyles : MarkdownExportStyles() {
    override val fontColor = "#1f2328"

    private data class HeadingSpec(
        val size: String,
        val color: String?,
        val borderBottom: Boolean,
    )

    /** Returns the primer typography for a heading level from 1 to 6. */
    private fun heading(level: Int): HeadingSpec = when (level) {
        1 -> HeadingSpec("2em", null, borderBottom = true)
        2 -> HeadingSpec("1.5em", null, borderBottom = true)
        3 -> HeadingSpec("1.25em", null, borderBottom = false)
        4 -> HeadingSpec("1em", null, borderBottom = false)
        5 -> HeadingSpec("0.875em", null, borderBottom = false)
        // h6 dims to the muted foreground per primer's headings.scss.
        else -> HeadingSpec("0.85em", "#59636e", borderBottom = false)
    }

    override fun headingCss(level: Int): String {
        val spec = heading(level)
        val color = spec.color?.let { " color: $it;" } ?: ""
        // Upstream padding-bottom: 0.3em (h1 9.6px, h2 7.2px), rounded to the nearest even px.
        val borderPadding = if (level == 1) "10px" else "8px"
        val border = if (spec.borderBottom) " padding-bottom: $borderPadding; border-bottom: 1px solid #d1d9e0;" else ""
        return "font-size: ${spec.size}; font-weight: 600; line-height: 1.25; margin: 24px 0 16px;$color$border"
    }

    override val paragraphCss =
        "font-size: 16px; line-height: 1.5; margin: 0 0 16px; color: $fontColor; word-break: break-word;"

    override val quoteParagraphCss =
        "font-size: 16px; line-height: 1.5; margin: 0 0 16px; color: #59636e; word-break: break-word;"

    override val listCss = "padding-left: 2em; margin: 0 0 16px;"
    override val listItemCss = "font-size: 16px; line-height: 1.5; margin-top: 0.25em; color: $fontColor;"
    override val orderedListItemCss = "font-size: 16px; line-height: 1.5; margin-top: 0.25em; color: $fontColor;"
    override val nestedListCss = "padding-left: 2em; margin: 0;"
    override val taskItemPrefixCss = "list-style: none; "

    override val inlineCodeCss =
        "color: $fontColor; background-color: rgba(175, 184, 193, 0.2); padding: 0.2em 0.4em; border-radius: 6px; " +
            "font-family: $monospaceFont; font-size: 0.85em; font-style: normal; " +
            "white-space: break-spaces; word-break: break-word; box-decoration-break: clone; " +
            "-webkit-box-decoration-break: clone; overflow-wrap: anywhere;"

    override val codeBlockCss =
        "font-family: $monospaceFont; line-height: 1.45; border-radius: 6px; margin: 0 0 16px; " +
            "white-space: pre; overflow: auto;"

    override fun codeElementCss(codeTheme: CodeTheme) =
        "display: -webkit-box; min-width: 100%; box-sizing: border-box; overflow-x: auto; " +
            "font-weight: 400; font-size: 0.85em; padding: 16px; margin: 0; word-break: normal; " +
            "white-space: pre; color: ${codeTheme.codeRgb.toCssColor()}; " +
            "background: ${codeTheme.backgroundRgb.toCssColor()}; border-radius: 6px;"

    override val emCss = "font-style: italic;"

    override val strikethroughCss = "color: $fontColor;"

    override val linkCss = "color: #0969da; text-decoration: underline;"

    override val imgCss = "display: block; margin: 0 auto; max-width: 100%;"

    override val blockquoteCss =
        "color: #59636e; padding: 0 1em; margin: 0 0 16px; border-left: 0.25em solid #d1d9e0;"

    override val hrCss =
        "height: 0.25em; padding: 0; margin: 24px 0; background-color: #d1d9e0; border: 0;"

    override val tableCss =
        "margin: 0 0 16px; font-size: 16px; width: auto; max-width: 100%; overflow: auto; " +
            "border-collapse: collapse; border: 1px solid #d1d9e0;"

    override val thCss =
        "background: transparent; color: #1f2328; text-align: left; padding: 6px 13px; " +
            "line-height: 1.5; font-size: 16px; font-weight: 600; border: 1px solid #d1d9e0;"

    override val tdCss =
        "padding: 6px 13px; line-height: 1.5; font-size: 16px; color: #1f2328; border: 1px solid #d1d9e0;"

    override val stripedTdCss = "$tdCss background: #f6f8fa;"

    override val h1HasPrefix = false
    override val linkHasIcon = false
    override val quoteHasMarks = false
    override val firstLetterCapitalized = false
}

/**
 * smart-blue export styles; values mirror docs/theme/smart-blue/smart-blue.css.
 * The h1 juejin-logo watermark is absent from the reference stylesheet, and the `.markdown-body`
 * grid background and `kbd` rule are intentionally not rendered: the export is a block fragment
 * with no body wrapper, and the model has no kbd element.
 */
internal object SmartBlueExportStyles : MarkdownExportStyles() {
    override val fontColor = "#595959"

    private data class HeadingSpec(
        val size: String,
        val padding: String,
        val margin: String,
        val centered: Boolean,
        val borderLeft: Boolean,
    )

    /** Returns the smart-blue typography for a heading level from 1 to 6. */
    private fun heading(level: Int): HeadingSpec = when (level) {
        // The stylesheet's symmetric 80px (50 margin + 30 padding) becomes hydrogen's asymmetric h1
        // rhythm, which renders 35px above and 27px below. Hydrogen reaches its 27px only because
        // its h1 bottom margin collapses under the following paragraph's 22px top margin; smartblue
        // paragraphs carry no CSS margin, so the heading supplies the whole 26px itself, matching
        // the preview's 10px heading bottom plus 16px paragraph top.
        1 -> HeadingSpec("22px", "padding: 0;", "margin: 35px 0 26px;", centered = true, borderLeft = false)
        // h2 drops the shared 30px vertical padding for its own 10px left inset.
        2 -> HeadingSpec("20px", "padding: 0 0 0 10px;", "margin: 30px 0;", centered = false, borderLeft = true)
        3 -> HeadingSpec("16px", "padding: 30px 0;", "margin: 0;", centered = false, borderLeft = false)
        // h4 to h6 keep the shared rule and fall back to the browser's 1em/0.83em/0.67em at 15px.
        4 -> HeadingSpec("15px", "padding: 30px 0;", "margin: 0;", centered = false, borderLeft = false)
        5 -> HeadingSpec("12.45px", "padding: 30px 0;", "margin: 0;", centered = false, borderLeft = false)
        else -> HeadingSpec("10.05px", "padding: 30px 0;", "margin: 0;", centered = false, borderLeft = false)
    }

    override fun headingCss(level: Int): String {
        val spec = heading(level)
        val center = if (spec.centered) " text-align: center;" else ""
        val border = if (spec.borderLeft) " border-left: 4px solid #135ce0;" else ""
        return "font-size: ${spec.size}; font-weight: 700; line-height: 1.5; color: #135ce0; " +
            "${spec.padding} ${spec.margin}$center$border"
    }

    override val paragraphCss =
        "font-size: 15px; line-height: 2; margin: 0; color: $fontColor; word-break: break-word;"

    /** Paragraph override applied to paragraphs nested inside a blockquote (color: #666). */
    override val quoteParagraphCss =
        "font-size: 15px; line-height: 2; margin: 0; color: #666666; word-break: break-word;"

    // The stylesheet indents only `ul` (margin-left: 2em on top of the browser's 40px padding-left);
    // ordered lists share it because the model carries a single list container style.
    override val listCss = "padding-left: 40px; margin: 15px 0 15px 30px;"
    override val listItemCss = "font-size: 15px; line-height: 2; margin-bottom: 0; color: $fontColor;"
    override val orderedListItemCss = "font-size: 15px; line-height: 2; margin-bottom: 0; color: $fontColor;"
    override val nestedListCss = "padding-left: 40px; margin: 15px 0 15px 30px;"
    override val taskItemPrefixCss = "list-style: none; "

    override val inlineCodeCss =
        "color: #ff502c; background-color: #fff5f5; padding: 0.065em 0.4em; border-radius: 2px; " +
            "font-family: $monospaceFont; font-size: 0.87em; font-style: normal; " +
            "word-break: break-word; box-decoration-break: clone; -webkit-box-decoration-break: clone; overflow-wrap: anywhere;"

    override val codeBlockCss =
        "font-family: $monospaceFont; line-height: 1.75; margin: 15px 0; white-space: pre; overflow: auto;"

    override fun codeElementCss(codeTheme: CodeTheme) =
        "display: -webkit-box; min-width: 100%; box-sizing: border-box; overflow-x: auto; " +
            "font-weight: 400; font-size: 12px; padding: 15px 12px; margin: 0; word-break: normal; " +
            "white-space: pre; color: ${codeTheme.codeRgb.toCssColor()}; " +
            "background: ${codeTheme.backgroundRgb.toCssColor()};"

    override val emCss = "font-style: italic;"

    override val boldColor = "#036aca"

    override val strikethroughCss = "color: $fontColor;"

    /** Anchors keep the stylesheet's 1px translucent bottom border instead of an underline. */
    override val linkCss = "color: #036aca; text-decoration: none; border-bottom: 1px solid rgba(3, 106, 202, 0.8);"

    override val imgCss = "display: block; margin: 0 auto; max-width: 100%;"

    override val blockquoteCss =
        "background: #fff9f9; margin: 30px 0; padding: 2px 20px; border-left: 4px solid #b2aec5;"

    /** The stylesheet sets only the top border; 8px is its .5em browser default at the 15px body size. */
    override val hrCss = "height: 1px; border: none; margin: 8px 0; background-color: #135ce0;"

    override val tableCss =
        "border-collapse: collapse; margin: 15px 0; width: auto; max-width: 100%; overflow-x: auto;"

    override val thCss =
        "background: transparent; color: $fontColor; text-align: left; padding: 9px 15px; " +
            "font-size: 15px; font-weight: 700; border: 1px solid #dfe2e5;"

    override val tdCss =
        "padding: 9px 15px; font-size: 15px; line-height: 18px; color: $fontColor; border: 1px solid #dfe2e5;"

    override val stripedTdCss = "$tdCss background: #f6f8fa;"

    override val h1HasPrefix = false
    override val linkHasIcon = false
    override val quoteHasMarks = false
    override val firstLetterCapitalized = false
}

/** Typora Paper export styles adapted from Esther Inspired Paper at commit 8c4f912. */
internal object TyporaPaperExportStyles : MarkdownExportStyles() {
    override val fontColor = "#1a1a2e"

    private data class HeadingSpec(
        val size: String,
        val lineHeight: String,
        val top: String,
        val bottom: String,
        val color: String,
        val letterSpacing: String,
        val borderBottom: Boolean = false,
    )

    /** Returns Paper's heading metrics with font sizes aligned to the hydrogen heading scale. */
    private fun heading(level: Int): HeadingSpec = when (level) {
        1 -> HeadingSpec("30px", "1.18", "27.72px", "36.288px", "#17172a", "-0.045em")
        2 -> HeadingSpec("28px", "1.35", "67.2px", "23.04px", "#17172a", "-0.025em", borderBottom = true)
        3 -> HeadingSpec("24px", "1.35", "45.36px", "15.552px", "#2b7fd8", "-0.025em")
        4 -> HeadingSpec("20px", "1.35", "37.632px", "12.9024px", "#17172a", "-0.025em")
        5 -> HeadingSpec("16px", "1.35", "32.928px", "11.2896px", "#17172a", "0.02em")
        else -> HeadingSpec("16px", "1.35", "29.568px", "10.1376px", "#555568", "0.08em")
    }

    /** Builds the Paper heading style without changing the application's selected font. */
    override fun headingCss(level: Int): String {
        val spec = heading(level)
        // Upstream padding-bottom: 0.42em at h2's 28px = 11.76px, rounded to the nearest even 12px.
        val border = if (spec.borderBottom) " padding-bottom: 12px; border-bottom: 1px solid #e8e0cf;" else ""
        return "font-size: ${spec.size}; font-weight: 800; line-height: ${spec.lineHeight}; " +
            "margin: ${spec.top} 0 ${spec.bottom}; color: ${spec.color}; letter-spacing: ${spec.letterSpacing};$border"
    }

    /** Renders Paper's paired blue and yellow dots before level-two headings, scaled to the h2 font size. */
    override fun headingPrefixHtml(level: Int): String = if (level == 2) {
        // The dot pair keeps its 23px reference geometry, scaled to the h2 font size so the icon
        // matches the text; every derived px value rounds to the nearest even integer, and
        // vertical-align: middle mirrors the preview's CenterVertically.
        val unit = heading(level).size.removeSuffix("px").toFloat() / 23f
        fun evenPx(value: Float): String = "%dpx".format(Locale.ROOT, (value / 2f).roundToInt() * 2)
        val box = evenPx(23f * unit)
        val dot = evenPx(15f * unit)
        val yellowLeft = evenPx(8f * unit)
        val yellowTop = evenPx(7f * unit)
        "<span style=\"position: relative; display: inline-block; width: $box; height: $box; margin-right: 8px; " +
            "vertical-align: middle;\"><span style=\"position: absolute; left: $yellowLeft; top: $yellowTop; " +
            "width: $dot; height: $dot; border-radius: 50%; background: #f4d758;\"></span>" +
            "<span style=\"position: absolute; left: 0; top: 0; width: $dot; height: $dot; " +
            "border-radius: 50%; background: #2b7fd8;\"></span></span>"
    } else {
        ""
    }

    /** Renders Paper's hand-drawn yellow accent bar after level-one headings. */
    override fun headingSuffixHtml(level: Int): String = if (level == 1) {
        "<span style=\"display: block; width: 51.2px; height: 6.72px; margin-top: 8px; " +
            "border-radius: 999px 52% 999px 46%; background: #f4d758; transform: rotate(-1.5deg);\"></span>"
    } else {
        ""
    }

    override val paragraphCss =
        "font-size: 16px; line-height: 1.82; letter-spacing: 0.012em; margin: 16px 0; color: $fontColor; word-break: break-word;"
    override val quoteParagraphCss =
        "font-size: 16px; line-height: 1.82; letter-spacing: 0.012em; margin: 0; color: #555568; word-break: break-word;"
    override val listCss = "padding-left: 26.4px; margin: 16px 0;"
    override val listItemCss = "font-size: 16px; line-height: 1.82; margin: 5.44px 0; padding-left: 2.56px; color: $fontColor;"
    // ponytail: inline CSS cannot target ::marker without recoloring item text; add explicit marker spans if needed.
    override val orderedListItemCss = listItemCss
    override val nestedListCss = "padding-left: 26.4px; margin: 3.2px 0;"
    override val taskItemPrefixCss = "list-style: none; "
    override val inlineCodeCss =
        "color: #b43c50; background: #f5efe1; margin: 0 0.08em; padding: 0.16em 0.38em; " +
            "border: 1px solid #e8e0cf; border-radius: 6px; font-family: $monospaceFont; font-size: 0.88em; " +
            "white-space: break-spaces; overflow-wrap: anywhere; word-break: break-all; box-decoration-break: clone;"
    override val codeBlockCss =
        "font-family: $monospaceFont; margin: 24px 0; border: 1px solid #d9dde5; border-radius: 15px; " +
            "background: #eef0f4; box-shadow: 0 12px 30px rgba(62, 48, 22, 0.10); overflow: hidden;"
    override fun codeBlockHeaderHtml(language: CodeLanguage?): String =
        "<span style=\"display: block; height: 38.4px; box-sizing: border-box; padding: 13px 19.2px; " +
            "border-bottom: 1px solid #d9dde5;\"><span style=\"display: inline-block; width: 10px; height: 10px; " +
            "border-radius: 50%; background: #ff5f57;\"></span><span style=\"display: inline-block; width: 10px; height: 10px; " +
            "margin-left: 7px; border-radius: 50%; background: #febc2e;\"></span><span style=\"display: inline-block; " +
            "width: 10px; height: 10px; margin-left: 7px; border-radius: 50%; background: #28c840;\"></span></span>"

    /** Keeps the active code palette while applying Paper's inner spacing and line rhythm. */
    override fun codeElementCss(codeTheme: CodeTheme) =
        "display: -webkit-box; min-width: 100%; box-sizing: border-box; overflow-x: auto; font-weight: 400; " +
            "font-size: 14.08px; line-height: 1.68; padding: 20px 19.2px; margin: 0; word-break: normal; " +
            "white-space: pre; color: ${codeTheme.codeRgb.toCssColor()}; background: ${codeTheme.backgroundRgb.toCssColor()};"

    override val emCss = "font-style: italic; color: #555568;"
    override val boldColor = "#17172a"
    override val strikethroughCss = "color: $fontColor;"
    override val linkCss =
        "color: #2b7fd8; text-decoration-line: underline; text-decoration-color: #f4d758; " +
            "text-decoration-thickness: 0.16em; text-underline-offset: 0.16em;"
    override val imgCss =
        "display: block; margin: 27.2px auto; max-width: 100%; border-radius: 14px; " +
            "box-shadow: 0 12px 32px rgba(62, 48, 22, 0.10);"
    override val tableImgCss = "display: block; margin: 0 auto; max-width: 100%; border-radius: 14px;"
    override val blockquoteCss =
        "position: relative; min-height: 36.8px; margin: 27.2px 0; padding: 24.8px 28px 23.2px 50px; " +
            "overflow: hidden; border: 0; border-radius: 18px; color: #555568; background: #ffffff; " +
            "box-shadow: 0 10px 34px rgba(62, 48, 22, 0.10);"
    override val nestedBlockquoteCss =
        "position: relative; margin: 16px 0 0; padding: 24.8px 28px 23.2px 50px; border: 0; " +
            "border-radius: 18px; color: #555568; background: #faf6eb;"
    override val quoteOpenCss =
        "position: absolute; top: 1.5px; left: 6px; color: #f4d758; font-size: 73.6px; font-weight: 700; line-height: 1;"
    override val quoteHasClosingMark = false
    override val hrCss =
        "width: 42%; height: 5px; margin: 51.2px auto; border: 0; border-radius: 50%; " +
            "background: #f4d758; box-shadow: 14px 0 0 #2b7fd8, -14px 0 0 #e84a5f; opacity: 0.78;"
    override val tableCss =
        "width: 100%; margin: 16px 0; border: 1px solid #e8e0cf; border-spacing: 0; border-collapse: separate; " +
            "border-radius: 14px; background: #fffdf8; box-shadow: 0 6px 22px rgba(62, 48, 22, 0.10); overflow: hidden;"
    override val thCss =
        "padding: 11.52px 14.4px; color: #17172a; background: rgba(43, 127, 216, 0.10); text-align: left; " +
            "font-size: 16px; line-height: 1.82; font-weight: 800; border-right: 1px solid #e8e0cf; border-bottom: 1px solid #e8e0cf;"
    override val tdCss =
        "padding: 11.52px 14.4px; color: #1a1a2e; font-size: 16px; line-height: 1.82; " +
            "border-right: 1px solid #e8e0cf; border-bottom: 1px solid #e8e0cf;"
    override val stripedTdCss = "$tdCss background: #faf6eb;"
    override val h1HasPrefix = false
    override val linkHasIcon = false
    override val quoteHasMarks = true
    override val firstLetterCapitalized = false
}

/** Rim export styles adapted from typora-theme-rim at commit f0d54ef. */
internal object RimExportStyles : MarkdownExportStyles() {
    override val fontColor = "#13202c"

    private data class HeadingSpec(
        val size: String,
        val weight: Int,
        val top: String,
        val color: String,
    )

    /** Returns Rim's heading metrics resolved against its 18px base size. */
    private fun heading(level: Int): HeadingSpec = when (level) {
        1 -> HeadingSpec("34.2px", 700, "18px", "#152e45")
        2 -> HeadingSpec("28.8px", 700, "43.2px", "#152e45")
        3 -> HeadingSpec("23.4px", 600, "36px", "#152e45")
        4 -> HeadingSpec("20.7px", 600, "32.4px", "#152e45")
        5 -> HeadingSpec("18px", 600, "28.8px", "#152e45")
        else -> HeadingSpec("18px", 600, "28.8px", "#47525d")
    }

    /** Builds one Rim heading style without importing its bundled heading font. */
    override fun headingCss(level: Int): String {
        val spec = heading(level)
        return "font-size: ${spec.size}; font-weight: ${spec.weight}; line-height: 1.3; " +
            "margin: ${spec.top} 0 18px; color: ${spec.color};"
    }

    override val paragraphCss =
        "font-size: 18px; line-height: 1.7; margin: 14.4px 0; color: $fontColor; word-break: break-word;"
    override val quoteParagraphCss = paragraphCss
    override val listCss = "padding-left: 20px; margin: 14.4px 0;"
    override val listItemCss = "font-size: 18px; line-height: 1.7; margin: 0; color: $fontColor;"
    override val orderedListItemCss = listItemCss
    override val nestedListCss = "padding-left: 20px; margin: 0;"
    override val taskItemPrefixCss = "list-style: none; "
    override val inlineCodeCss =
        "color: #00711e; background: #f8f6f6; padding: 0.08em 0.28em; border: 1px solid #e7eaed; " +
            "border-radius: 3px; font-family: $monospaceFont; font-size: 0.8em; line-height: 1.4; " +
            "white-space: break-spaces; overflow-wrap: anywhere; word-break: break-all; box-decoration-break: clone;"
    override val codeBlockCss =
        "font-family: $monospaceFont; line-height: 1.4; margin: 15px 0; border: 1px solid #e7eaed; " +
            "border-radius: 3px; background: #f8f6f6; white-space: pre; overflow: auto;"

    /** Keeps the active code palette while applying Rim's compact type and spacing. */
    override fun codeElementCss(codeTheme: CodeTheme) =
        "display: -webkit-box; min-width: 100%; box-sizing: border-box; overflow-x: auto; font-weight: 400; " +
            "font-size: 14.4px; line-height: 1.4; padding: 8px; margin: 0; word-break: normal; white-space: pre; " +
            "color: ${codeTheme.codeRgb.toCssColor()}; background: ${codeTheme.backgroundRgb.toCssColor()}; border-radius: 3px;"

    override val emCss = "font-style: italic;"
    override val strikethroughCss = "color: $fontColor;"
    override val linkCss = "color: #3e3282; text-decoration: underline;"
    override val imgCss = "display: block; max-width: 100%; margin: 14.4px auto;"
    override val blockquoteCss = "margin: 14.4px 0; padding: 0 15px 0 17px; border-left: 3px solid #4e3e8b;"
    override val nestedBlockquoteCss = "margin: 14.4px 0; padding: 0 0 0 17px; border-left: 3px solid #4e3e8b;"
    override val hrCss = "height: 2px; margin: 16px 0; padding: 0; border: 0; background: #dedede;"
    override val tableCss =
        "font-size: 14.4px; margin: 14.4px 0; border: 1px solid #cccccc; border-collapse: collapse; word-break: normal;"
    override val thCss =
        "padding: 6px 0; color: #13202c; font-size: 14.4px; line-height: 1.4; font-weight: 700;"
    override val tdCss = "padding: 6px 0; color: #13202c; font-size: 14.4px; line-height: 1.4;"
    override val stripedTdCss = tdCss
    override val h1HasPrefix = false
    override val linkHasIcon = false
    override val quoteHasMarks = false
    override val firstLetterCapitalized = false
}

/**
 * Chocolate export styles adapted from juejin-markdown-theme-qklhk at commit 4f2a290.
 * The `.markdown-body` grid background is not rendered: the export is a block fragment with no
 * body wrapper. The h5/h6 trailing numbered circles, the strong 「」 brackets, and the em-strong
 * highlight are pseudo-element/combinator effects the inline-CSS model cannot express. Heading
 * top margins fall back to the browser defaults the stylesheet leaves in place.
 */
internal object ChocolateExportStyles : MarkdownExportStyles() {
    override val fontColor = "#412c0c"

    private data class HeadingSpec(
        val size: String,
        val color: String,
        val lineHeight: String,
        val margin: String,
        val padding: String,
        val borderBottom: String = "",
    )

    /** Returns the chocolate typography for a heading level from 1 to 6. */
    private fun heading(level: Int): HeadingSpec = when (level) {
        // WeiSome: h1 is 24px with the shared 1.5 line height (36px, even). h1/h2 indent comes
        // from the inline icon image plus its 6px margin.
        1 -> HeadingSpec(
            "24px", "#664900", "36px", "17px 0 10px", "padding-bottom: 0;",
            borderBottom = "border-bottom: 5px solid #6d4e00; text-shadow: 1px 1px 1px #8a6200;",
        )
        2 -> HeadingSpec("20px", "#614500", "1.5", "17px 0 10px", "padding: 0 0 5px;")
        3 -> HeadingSpec("18px", "#614500", "1.5", "20px 10px 0 0", "padding: 0 0 0 10px;")
        4 -> HeadingSpec("17px", "#a37400", "1.5", "23px 0 10px", "padding-bottom: 5px;")
        5 -> HeadingSpec("14px", "#a37400", "1.5", "23px 0 10px", "padding-bottom: 5px;")
        // WeiSome floor: 14px instead of the upstream 12px.
        else -> HeadingSpec("14px", "#a37400", "1.5", "28px 0 10px", "padding-bottom: 5px;")
    }

    override fun headingCss(level: Int): String {
        val spec = heading(level)
        val borderLeft = if (level == 3) " border-left: 5px solid #8f6600;" else ""
        return "font-size: ${spec.size}; font-weight: bold; line-height: ${spec.lineHeight}; " +
            "margin: ${spec.margin}; ${spec.padding} color: ${spec.color};$borderLeft ${spec.borderBottom}"
    }

    /**
     * Renders the chocolate-piece icon before h1/h2 text like the ::before. The icon is sized to
     * the heading font size. The icon flows inline (vertical-align: middle) instead of absolute
     * positioning: the WeChat editor's paste sanitizer strips empty decorative spans and rewrites
     * <img> without position styles, so the icon plus its margin must reproduce the heading indent.
     */
    override fun headingPrefixHtml(level: Int): String {
        if (level > 2) return ""
        val size = heading(level).size
        return "<img src=\"data:image/png;base64,$ChocolateThemeIconPngBase64\" " +
            "style=\"display: inline-block; vertical-align: middle; width: $size; height: $size; " +
            "margin-right: 6px;\">"
    }

    override val paragraphCss =
        "font-size: 15px; line-height: 1.75; margin: 0 0 16px; color: $fontColor; letter-spacing: 1px; word-break: break-word;"

    /** Paragraph override applied to paragraphs nested inside a blockquote (color: #fff6e0). */
    override val quoteParagraphCss =
        "font-size: 15px; line-height: 25px; margin: 0; color: #fff6e0; letter-spacing: 2px; word-break: break-word;"

    // The stylesheet keeps the browser's 1em ul/ol vertical margin; 15px is its equivalent at body size.
    override val listCss = "padding-left: 28px; margin: 15px 0;"
    override val listItemCss = "font-size: 15px; line-height: 1.75; margin-bottom: 0; color: #858585; letter-spacing: 1px;"
    override val orderedListItemCss = "$listItemCss padding-left: 6px;"
    override val nestedListCss = "padding-left: 28px; margin: 3px 0 0;"
    override val taskItemPrefixCss = "list-style: none; "

    // WeiSome floor: absolute 14px instead of the upstream 0.87em.
    override val inlineCodeCss =
        "color: #996d00; background-color: rgba(130, 98, 0, 0.3); padding: 0.065em 0.4em; border-radius: 4px; " +
            "font-family: $monospaceFont; font-size: 14px; font-style: normal; " +
            "word-break: break-word; box-decoration-break: clone; -webkit-box-decoration-break: clone; overflow-wrap: anywhere;"

    // pre leaves its vertical margin to the browser default (1em); 15px is its equivalent at body size.
    override val codeBlockCss =
        "font-family: $monospaceFont; line-height: 1.75; margin: 15px 0; white-space: pre; overflow: auto;"

    // WeiSome floor: 14px instead of the upstream 12px.
    override fun codeElementCss(codeTheme: CodeTheme) =
        "display: -webkit-box; min-width: 100%; box-sizing: border-box; overflow-x: auto; " +
            "font-weight: 400; font-size: 14px; padding: 15px 12px; margin: 0; word-break: normal; " +
            "white-space: pre; color: ${codeTheme.codeRgb.toCssColor()}; " +
            "background: ${codeTheme.backgroundRgb.toCssColor()};"

    override val emCss = "font-style: italic; color: #c28a00;"

    override val boldColor = "#c28a00"

    override val strikethroughCss = "color: #c28a00;"

    /** Anchors keep the stylesheet's 1px bottom border instead of an underline. */
    override val linkCss =
        "color: #755300; text-decoration: none; font-weight: bolder; border-bottom: 1px solid #755300;"

    override val imgCss = "display: block; margin: 0 auto; max-width: 100%;"

    override val blockquoteCss =
        "position: relative; line-height: 25px; border-radius: 10px; border: 1px solid #ffd87a; " +
            "background-color: rgba(189, 134, 0, 0.5); margin: 20px 0; padding: 20px;"

    override val quoteOpenCss =
        "position: absolute; top: 8px; left: 5px; font-size: 34px; font-weight: 700; line-height: 1; color: #cc9100;"
    override val quoteCloseCss =
        "position: absolute; right: 5px; bottom: -5px; font-size: 34px; font-weight: 700; line-height: 1; color: #cc9100;"
    override val quoteOpenMark = "\u275D"
    override val quoteCloseMark = "\u275E"

    // The stylesheet overrides its own first hr rule with border-top: 1px solid #805b00.
    override val hrCss =
        "height: 1px; border: none; margin: 32px 0; background-color: #805b00;"

    // The stylesheet's width: 100% !important wins over its own width: auto.
    // WeiSome floor: 14px instead of the upstream 12px.
    override val tableCss =
        "margin: 0; font-size: 14px; width: 100%; max-width: 100%; overflow: auto; border-collapse: collapse; border-spacing: 0;"

    override val thCss =
        "background: #f6f6f6; color: #000000; text-align: center; padding: 12px 7px; line-height: 24px; " +
            "font-size: 14px; border: 1px solid rgba(72, 42, 10, 0.1);"

    override val tdCss =
        "padding: 12px 7px; line-height: 24px; font-size: 14px; color: $fontColor; border: 1px solid rgba(72, 42, 10, 0.1);"

    override val stripedTdCss = tdCss

    override val h1HasPrefix = false
    override val linkHasIcon = false
    override val quoteHasMarks = true
    override val firstLetterCapitalized = false
}

/**
 * Yu export styles adapted from juejin-markdown-theme-yu at commit 1e3096f.
 * The `.markdown-body` grid background is not rendered: the export is a block fragment with no
 * body wrapper. The strong `·` dashes and the blockquote/table hover styles are pseudo-element
 * or interactive effects the inline-CSS model cannot express. The h2 font-size falls back to
 * the browser default (1.5em) the stylesheet leaves in place. Deliberate deviations from the
 * upstream stylesheet: h1 is sized 28px, the smallest font is clamped to 14px (h6, inline code,
 * code blocks, and table cells), emoji prefixes match their heading size, and all line-heights
 * are absolute even-valued px instead of unitless/em-relative values.
 */
internal object YuExportStyles : MarkdownExportStyles() {
    override val fontColor = "#5f6368"

    private data class HeadingSpec(
        val size: String,
        val margin: String,
        val paddingBottom: String,
        val lineHeight: String,
        val borderBottom: String = "",
    )

    /** Returns the yu typography for a heading level from 1 to 6. */
    private fun heading(level: Int): HeadingSpec = when (level) {
        1 -> HeadingSpec("28px", "35px 0 5px", "5px", "42px")
        // The stylesheet defines no h2 font-size; the browser's 1.5em default at body size applies.
        2 -> HeadingSpec("22.5px", "35px 0 10px", "24px", "34px", borderBottom = "border-bottom: 1px solid #ececec;")
        3 -> HeadingSpec("18px", "35px 0 10px", "0", "28px")
        4 -> HeadingSpec("16px", "35px 0 10px", "5px", "24px")
        5 -> HeadingSpec("14px", "35px 0 10px", "5px", "22px")
        else -> HeadingSpec("14px", "5px 0 10px", "5px", "22px")
    }

    /** Returns the animal emoji, its left offset, and its top offset for a heading level. */
    private fun prefixEmoji(level: Int): Triple<String, Int, Int>? = when (level) {
        1 -> Triple("\uD83E\uDD84", 0, 0)
        2 -> Triple("\uD83D\uDC33", 8, 0)
        3 -> Triple("\uD83D\uDC04", 8, -2)
        4 -> Triple("\uD83E\uDDA5", 8, -2)
        5 -> Triple("\uD83E\uDDA9", 9, -2)
        6 -> Triple("\uD83D\uDC27", 10, -1)
        else -> null
    }

    /** Returns the emoji glyph size for a heading level from 1 to 6, matching the heading size. */
    private fun prefixSize(level: Int): Float = when (level) {
        1 -> 28f
        2 -> 22.5f
        3 -> 18f
        4 -> 16f
        5 -> 14f
        else -> 14f
    }

    override fun headingCss(level: Int): String {
        val spec = heading(level)
        return "font-size: ${spec.size}; font-weight: bold; line-height: ${spec.lineHeight}; " +
            "margin: ${spec.margin}; padding: 0 0 ${spec.paddingBottom} 50px; color: $fontColor; " +
            "position: relative;${spec.borderBottom}"
    }

    /** Renders the animal emoji before heading text, positioned like the ::before. */
    override fun headingPrefixHtml(level: Int): String {
        val (emoji, left, top) = prefixEmoji(level) ?: return ""
        return "<span style=\"position: absolute; top: ${top}px; left: ${left}px; font-size: " +
            "${prefixSize(level)}px;\">$emoji</span>"
    }

    override val paragraphCss =
        "font-size: 15px; line-height: 28px; margin: 22px 0; color: $fontColor; " +
            "letter-spacing: 1px; word-spacing: 1px; word-break: break-word;"

    /** Paragraph override applied to paragraphs nested inside a blockquote (margin: 10px 0). */
    override val quoteParagraphCss =
        "font-size: 15px; line-height: 28px; margin: 10px 0; color: #666666; " +
            "letter-spacing: 1px; word-spacing: 1px; word-break: break-word;"

    // The stylesheet keeps the browser's 1em ul/ol vertical margin; 15px is its equivalent at body size.
    override val listCss = "padding-left: 28px; margin: 15px 0;"
    override val listItemCss =
        "font-size: 15px; line-height: 26px; margin-bottom: 0; color: #fd79a8; letter-spacing: 1px; word-spacing: 1px;"
    override val orderedListItemCss = "$listItemCss padding-left: 6px;"
    override val nestedListCss = "padding-left: 28px; margin: 3px 0 0;"
    override val taskItemPrefixCss = "list-style: none; "

    // WeiSome floor: absolute 14px and px padding instead of the upstream 0.87em (≈13px) and em padding.
    override val inlineCodeCss =
        "color: #ff502c; background-color: #fff5f5; padding: 1px 6px; border-radius: 2px; " +
            "font-family: $monospaceFont; font-size: 14px; font-style: normal; " +
            "word-break: break-word; box-decoration-break: clone; -webkit-box-decoration-break: clone; overflow-wrap: anywhere;"

    // pre leaves its vertical margin to the browser default (1em); 15px is its equivalent at body size.
    override val codeBlockCss =
        "font-family: $monospaceFont; line-height: 24px; margin: 15px 0; white-space: pre; overflow: auto;"

    override fun codeElementCss(codeTheme: CodeTheme) =
        "display: -webkit-box; min-width: 100%; box-sizing: border-box; overflow-x: auto; " +
            "font-weight: 400; font-size: 14px; padding: 15px 12px; margin: 0; word-break: normal; " +
            "white-space: pre; color: ${codeTheme.codeRgb.toCssColor()}; " +
            "background: ${codeTheme.backgroundRgb.toCssColor()}; border-radius: 8px;"

    override val emCss = "font-style: italic;"

    override val boldColor = "#fd79a8"

    override val strikethroughCss = "color: $fontColor;"

    /** Anchors keep the stylesheet's 1px bottom border and 4px side padding. */
    override val linkCss =
        "color: #fd79a8; text-decoration: none; border-bottom: 1px solid #fd79a8; padding: 0 4px;"

    override val imgCss = "display: block; margin: 0 auto; max-width: 100%;"

    override val blockquoteCss =
        "position: relative; color: #666666; padding: 23px; margin: 22px 0; " +
            "border-left: 4px solid #ee69a9; background-color: rgba(253, 121, 168, 0.1);"

    override val quoteOpenCss =
        "position: absolute; top: 0; left: 10px; font-size: 27px; color: rgba(253, 121, 168, 0.8);"
    override val quoteCloseCss =
        "position: absolute; bottom: 0; right: 10px; font-size: 27px; color: rgba(253, 121, 168, 0.8);"
    override val quoteOpenMark = "\u275D"
    override val quoteCloseMark = "\u275E"

    override val hrCss =
        "height: 1px; border: none; margin: 32px 0; background-color: rgba(253, 121, 168, 0.5);"

    override val tableCss =
        "display: inline-block; font-size: 14px; width: auto; max-width: 100%; overflow: auto; " +
            "border: solid 1px #f6f6f6; border-spacing: 0;"

    override val thCss =
        "background: rgba(253, 121, 168, 0.1); color: #fd79a8; text-align: left; padding: 12px 7px; " +
            "line-height: 24px; font-size: 14px;"

    override val tdCss = "padding: 12px 7px; line-height: 24px; font-size: 14px; min-width: 120px; color: $fontColor;"

    override val stripedTdCss = "$tdCss background: #fcfcfc;"

    override val h1HasPrefix = false
    override val linkHasIcon = false
    override val quoteHasMarks = true
    override val firstLetterCapitalized = false
}

/**
 * Cyanosis export styles adapted from juejin-markdown-theme-cyanosis at commit 6b814ea.
 * The `.markdown-body` grid background is not rendered: the export is a block fragment with no
 * body wrapper. The hr's centered scissors ornament, the link hover underline animation, the
 * selection styles, the `details`/`summary` rules, the task-list checkbox art, and the table row
 * hover are pseudo-element or interactive effects the inline-CSS model cannot express. The
 * heading `font-weight` stays at the browser default (bold) the stylesheet leaves in place.
 */
internal object CyanosisExportStyles : MarkdownExportStyles() {
    override val fontColor = "#353535"

    private data class HeadingSpec(
        val size: Int,
        val top: Int,
        val padding: String,
        val borderBottom: String = "",
    )

    /** Returns the cyanosis typography for a heading level from 1 to 6. */
    private fun heading(level: Int): HeadingSpec = when (level) {
        1 -> HeadingSpec(30, 36, "padding-bottom: 4px;")
        2 -> HeadingSpec(
            24, 36, "padding: 0 10px 10px 16px;",
            borderBottom = "border-bottom: 1px solid #ececec;",
        )
        3 -> HeadingSpec(20, 30, "padding-bottom: 0; padding-left: 6px;")
        4 -> HeadingSpec(16, 24, "padding-bottom: 0; padding-left: 6px;")
        5 -> HeadingSpec(14, 18, "padding-bottom: 0; padding-left: 6px;")
        else -> HeadingSpec(12, 12, "padding-bottom: 0; padding-left: 6px;")
    }

    override fun headingCss(level: Int): String {
        val spec = heading(level)
        val relative = if (level == 2) " position: relative;" else ""
        return "font-size: ${spec.size}px; font-weight: bold; line-height: 1.5; " +
            "margin: ${spec.top}px 0 10px; ${spec.padding} color: #005bb7;$relative${spec.borderBottom}"
    }

    /** Renders the opening 「 mark before h2 text, positioned like the ::before. */
    override fun headingPrefixHtml(level: Int): String = when (level) {
        2 -> "<span style=\"position: absolute; top: -6px; left: -10px;\">\u300C</span>"
        3 -> "<span style=\"padding-right: 6px; color: #2196f3;\">\u00BB</span>"
        else -> ""
    }

    /** Renders the closing 」 mark after h2 text at its static position like the ::after. */
    override fun headingSuffixHtml(level: Int): String = if (level == 2) {
        "<span style=\"position: absolute; top: 6px;\">\u300D</span>"
    } else {
        ""
    }

    override val paragraphCss =
        "font-size: 14px; line-height: 1.75; margin: 16px 0; color: $fontColor; word-break: break-word;"

    /** Paragraph override applied to paragraphs nested inside a blockquote (margin: 10px 0). */
    override val quoteParagraphCss =
        "font-size: 14px; line-height: 1.75; margin: 10px 0; color: #8c8c8c; word-break: break-word;"

    // The stylesheet keeps the browser's 1em ul/ol vertical margin; 16px is its equivalent at body size.
    override val listCss = "padding-left: 28px; margin: 16px 0;"
    override val listItemCss = "font-size: 14px; line-height: 1.75; margin-bottom: 0; color: $fontColor;"
    override val orderedListItemCss = "$listItemCss padding-left: 6px;"
    override val nestedListCss = "padding-left: 28px; margin: 4px 0 0;"
    override val taskItemPrefixCss = "list-style: none; "

    override val inlineCodeCss =
        "color: #c2185b; background-color: #fff4f4; padding: 0.065em 0.4em; border-radius: 2px; " +
            "font-family: $monospaceFont; font-size: 0.87em; font-style: normal; " +
            "word-break: break-word; box-decoration-break: clone; -webkit-box-decoration-break: clone; overflow-wrap: anywhere;"

    // pre leaves its vertical margin to the browser default (1em); 14px is its equivalent at body size.
    override val codeBlockCss =
        "font-family: $monospaceFont; line-height: 1.75; margin: 14px 0; white-space: pre; overflow: auto;"

    /** Keeps the active code palette while applying cyanosis's compact code type. */
    override fun codeElementCss(codeTheme: CodeTheme) =
        "display: -webkit-box; min-width: 100%; box-sizing: border-box; overflow-x: auto; " +
            "font-weight: 400; font-size: 12px; padding: 16px 12px; margin: 0; word-break: normal; " +
            "white-space: pre; color: ${codeTheme.codeRgb.toCssColor()}; " +
            "background: ${codeTheme.backgroundRgb.toCssColor()};"

    override val emCss = "font-style: italic; color: #4fc3f7;"

    override val boldColor = "#2196f3"

    override val strikethroughCss = "color: #ccc;"

    /** Anchors keep the stylesheet's 1px bottom border instead of an underline. */
    override val linkCss =
        "color: #3da8f5; text-decoration: none; border-bottom: 1px solid #bedcff;"

    override val imgCss = "display: block; margin: 0 auto; max-width: 100%;"

    override val blockquoteCss =
        "color: #8c8c8c; padding: 1px 20px; margin: 22px 0; " +
            "border-left: 4px solid #2196f3; background-color: #f0fdff;"

    override val hrCss =
        "position: relative; width: 98%; height: 1px; border: none; margin: 32px 0; " +
            "background-image: linear-gradient(to right, #007fff, rgba(255, 0, 0, 0.3), " +
            "rgba(255, 255, 255, 0.1), rgba(255, 0, 0, 0.3), #007fff); overflow: visible;"

    override val tableCss =
        "display: inline-block; font-size: 12px; width: auto; max-width: 100%; overflow: auto; " +
            "border: 1px solid #c3e0fd; border-spacing: 0; border-collapse: collapse;"

    override val thCss =
        "color: #005bb7; background-color: #dff0ff; text-align: left; padding: 12px 8px; " +
            "line-height: 24px; font-size: 14px; border: 1px solid #c3e0fd;"

    override val tdCss =
        "padding: 12px 8px; line-height: 24px; font-size: 12px; min-width: 120px; " +
            "color: $fontColor; border: 1px solid #c3e0fd;"

    override val stripedTdCss = "$tdCss background: #f7fbff;"

    override val h1HasPrefix = false
    override val linkHasIcon = false
    override val quoteHasMarks = false
    override val firstLetterCapitalized = false
}

/** Leaf icon PNG embedded before h2 text, extracted from the upstream `channing-cyan.scss`. */
private const val CyanH2IconPngBase64 = "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAADGklEQVRYR81X32vTYBQ999s6mFjQgQ+DrbHiVFZYU4cDcQ/6pGhTFVYFEXGi82H+Bz448UnEF1Fx9ccEEcXpZE3d5tP2ooKiTacTHaLNpigMHDgnU9tcSbrWrkwWR0sbyEOSe885ObnfvV8IRT6oyPwoLQHBx+OVM5WJvSyEVAhnBOjt7yU/+/rr6r6l8TMO+F/EN0JQhICqQpD/xaRpcpAc9tS+M+9lBCia/oqBamK+zeDuQogQZaKJk3wcQjxSva7tGQGB2Ke1zIk3DNyMyNL+QpCnMQOaPsDAVuGAp9cjvbYc8Ec/bCYSg0zoiHilk1tHxqsqEsYlML4kjIpT/eurJxRNPweQU5VdrWaOEo1fgKAVbBgXIz73kF3R/ph+ghgdzMYWM29eAWlBJqgZaFlFYtC6nhWpaDqnSGlIlV1WjJ3DloDNgyNLncudqgX//Ucg3LxuStHGuhi8pqKCW3rqV342rwFjRznKm+/LNaN2yC237ThgF2wxcfMLeP6+ncrKzoPoKTGeLQbYbg4TNoC5iZPJY5HGVRdSNZAWYBclD3FzBQzrR8hACAKdzBzKA/4/IYioDQaOskBbpEG6PO8qKKSAEi3CnEb0Pw4oMf0OmKbTDWqh3Lw6EIiNBZi5lxh3wz4puBD5ovqAMvxhHSdFKxE1CQe3m/07TeTX4lcJdAhE+1Sv65Z5P/ByvIGTRowIZ9igbtXnmrOsbTvgj+kHBNMuBu9OdVw8EeU4nC1A0cYmAHZOTRrLhra4Z8ywnSN6vZHAFTA2WnnMfQB3qz73ddsOZM8CACFDIPSgQXqebXEgqgeZcAeEe6pXasm1f8ew3igMtAHWac0Uc/jYdyAaP0xEBwFsmgUPqbJ0NE2UKj4EGcahiOzuyhagaHpnmtgcVgTcCMuua7YdyAHbA3ArQNscVFbb4635aD6fnYaTvxxi9UNP7ddMXaRWVBdAcaLk6bDXPZCNZ9uBXEsDUX1T2Cc9yjig6Z0EHg3LK8/aqf6MwJKchkXfks1+0+JtSq3qLPa23BRR1B+T/6nkfMaW1r9hPt/MLtYfTLEpP+T9FNoAAAAASUVORK5CYII="

/** Outline icon PNG drawn inside the h3 underline bar and circle, from the upstream stylesheet. */
private const val CyanH3IconPngBase64 = "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAABRklEQVRYR2NkGGDAOMD2M4w6YDQERkNg+ITAppcfY/8zMv3wF+NdTUrZQpUQ2PT6cz8Dw/8CkMWMDIwNvqK8jcQ6gmIHNN19EaXPx1XPyMCghrCUKcpPlGc5MY6gyAE+Fx52MjL8j3cU5a1UYWXtZGBkEAVb+p8hxU+Mby5NHQCxnKEMaskzJ37uFmUetkmMjAzrfUX4woixHBJlZAA0y2EmPPYU4enLkhGeQIqRJDsAh+UgO7duNpD3IcVykkOA2paT5ABaWE60A2hlOdEO8D3/4CMDIyMfWvySFefoaYSoROh74eFXBgYGLiTNVLGc+BC48PAnAwMDG9QBVLOcaAd8P5ox+x/jf5AjGLgYfnwnKqv9/8/PwPO/kFF/MSj0cAKiouD/0bgYoixFU8RovWgJIX1EOYCQIZTIjzpgNARGQ2DAQwAAvHBaIdB7zxsAAAAASUVORK5CYII="

/**
 * Cyan export styles adapted from juejin-markdown-theme-channing-cyan at commit c843c2f
 * (registered upstream as `channing-cyan`, renamed `cyan` here).
 * The `.markdown-body` checkered grid background is not rendered: the export is a block fragment
 * with no body wrapper. The h1 background glow and animated gradient shadow are skipped; the h3
 * underline and circle render in their final static state without the upstream animations. The
 * `strong` 「」 marks and `figcaption` rules have no model element. Code-block colors continue to
 * come from the active code theme.
 */
internal object CyanExportStyles : MarkdownExportStyles() {
    override val fontColor = "#2b2b2b"

    /** Returns the cyan typography for a heading level from 1 to 6. */
    private fun heading(level: Int): String = when (level) {
        1 -> "font-size: 30px; font-weight: bold; color: #4dd0e1; text-align: center; " +
            "width: max-content; padding: 30px 0; margin: 0 auto;"
        2 -> "font-size: 24px; font-weight: bold; color: #4dd0e1; " +
            "border-bottom: 4px solid #4dd0e1; padding: 12px 32px 12px 0; margin: 30px 0;"
        3 -> "font-size: 18px; font-weight: bold; color: #4dd0e1; position: relative; " +
            "width: max-content; padding: 4px 32px; margin: 35px 0 10px;"
        4 -> "font-size: 16px; font-weight: bold; color: #4dd0e1; padding: 30px 0; margin: 35px 0 10px;"
        5 -> "font-size: 15px; font-weight: bold; color: #4dd0e1; padding: 30px 0; margin: 35px 0 10px;"
        else -> "font-size: 15px; font-weight: bold; color: #4dd0e1; padding: 30px 0; margin: 5px 0 10px;"
    }

    override fun headingCss(level: Int): String = heading(level)

    /**
     * Renders the leaf icon before h2 text and the h3 underline bar like their ::before.
     * The h2 icon flows inline (the WeChat editor's paste sanitizer rewrites <img> without
     * position styles), so the icon plus its margin reproduces the upstream 32px indent. The
     * h3 bar and circle spans keep absolute positioning and wrap their icon in an <img> to
     * stay non-empty.
     */
    override fun headingPrefixHtml(level: Int): String = when (level) {
        2 -> "<img src=\"data:image/png;base64,$CyanH2IconPngBase64\" " +
            "style=\"display: inline-block; vertical-align: middle; width: 24px; height: 24px; " +
            "margin-right: 8px;\">"
        3 -> "<span style=\"display: block; position: absolute; left: 0; top: 0; bottom: -2px; " +
            "margin: auto; width: 100%; height: 28px; border-bottom: 2px solid #4dd0e1;\">" +
            "<img src=\"data:image/png;base64,$CyanH3IconPngBase64\" style=\"width: 28px; height: 28px;\"></span>"
        else -> ""
    }

    /** Renders the h3 outline circle after the text at its ::after position, without animation. */
    override fun headingSuffixHtml(level: Int): String = if (level == 3) {
        "<span style=\"display: block; position: absolute; right: -15px; top: 0; bottom: 0; " +
            "margin: auto; width: 28px; height: 28px; box-sizing: border-box; " +
            "border: 2px solid #4dd0e1; border-radius: 50%;\">" +
            "<img src=\"data:image/png;base64,$CyanH3IconPngBase64\" " +
            "style=\"width: 24px; height: 24px;\"></span>"
    } else {
        ""
    }

    override val paragraphCss =
        "font-size: 14px; line-height: 1.75; margin: 22px 0; color: $fontColor; " +
            "letter-spacing: 2px; word-spacing: 2px; word-break: break-word;"

    /** Paragraph override applied to paragraphs nested inside a blockquote (line-height: 2). */
    override val quoteParagraphCss =
        "font-size: 14px; line-height: 2; margin: 22px 0; color: #595959; word-break: break-word;"

    // The stylesheet keeps the browser's 1em ul/ol vertical margin; 15px is its equivalent at body size.
    override val listCss = "padding-left: 28px; margin: 15px 0; color: #595959;"
    override val listItemCss = "font-size: 15px; line-height: 1.75; margin-bottom: 0; color: #595959;"
    override val orderedListItemCss = "$listItemCss padding-left: 6px;"
    override val nestedListCss = "padding-left: 28px; margin: 3px 0 0;"
    override val taskItemPrefixCss = "list-style: none; "

    override val inlineCodeCss =
        "color: #26c6da; background-color: rgba(77, 208, 225, 0.08); border-radius: 2px; " +
            "font-family: $monospaceFont; padding: 0.195em 0.4em; word-break: break-word; " +
            "overflow-x: auto; overflow-wrap: anywhere; box-decoration-break: clone; " +
            "-webkit-box-decoration-break: clone;"

    override val codeBlockCss =
        "font-family: $monospaceFont; line-height: 1.75; margin: 16px; border-radius: 4px; " +
            "box-shadow: 0 0 8px rgba(110, 110, 110, 0.45); overflow: auto; position: relative;"

    /** Renders the mac-style dots bar the stylesheet draws via pre::before. */
    override fun codeBlockHeaderHtml(language: CodeLanguage?): String =
        "<span style=\"display: block; height: 30px; margin-bottom: -7px; box-sizing: border-box; " +
            "padding: 10px 0 0 10px;\"><span style=\"display: inline-block; width: 10px; height: 10px; " +
            "border-radius: 50%; background: #ff5f57;\"></span><span style=\"display: inline-block; " +
            "width: 10px; height: 10px; margin-left: 7px; border-radius: 50%; background: #febc2e;\"></span>" +
            "<span style=\"display: inline-block; width: 10px; height: 10px; margin-left: 7px; " +
            "border-radius: 50%; background: #28c840;\"></span></span>"

    /** Keeps the active code palette while applying cyan's compact code type. */
    override fun codeElementCss(codeTheme: CodeTheme) =
        "display: -webkit-box; min-width: 100%; box-sizing: border-box; overflow-x: auto; " +
            "font-weight: 400; font-size: 12px; padding: 15px 12px; margin: 0; word-break: normal; " +
            "white-space: pre; color: ${codeTheme.codeRgb.toCssColor()}; " +
            "background: ${codeTheme.backgroundRgb.toCssColor()};"

    override val emCss = "font-style: normal; color: #4dd0e1; font-weight: bold;"

    override val boldColor = "#26c6da"

    override val strikethroughCss = "color: #4dd0e1;"

    /** Anchors keep the stylesheet's 1px bottom border and side margins instead of an underline. */
    override val linkCss =
        "color: #4dd0e1; border-bottom: 1px solid #4dd0e1; font-weight: 400; text-decoration: none; margin: 0 4px;"

    override val imgCss =
        "display: block; margin: 20px auto; max-width: 80%; border-radius: 6px; " +
            "object-fit: contain; box-shadow: 0 0 16px rgba(110, 110, 110, 0.45);"

    override val blockquoteCss =
        "color: #595959; padding: 24px 32px; margin: 2em 0; border-left: 4px solid #26c6da; " +
            "background: rgba(77, 208, 225, 0.15); position: relative;"

    override val quoteOpenCss =
        "position: absolute; top: 8px; left: 8px; color: #4dd0e1; font-size: 30px; " +
            "line-height: 1; font-weight: 700; opacity: 0.7;"
    override val quoteCloseCss =
        "position: absolute; right: 8px; bottom: 0; color: #4dd0e1; font-size: 30px; line-height: 1; opacity: 0.7;"
    override val quoteOpenMark = "\u275D"
    override val quoteCloseMark = "\u275E"

    override val hrCss =
        "border: none; border-top: 1px solid #4dd0e1; margin: 32px 0;"

    override val tableCss =
        "display: inline-block; font-size: 12px; width: auto; max-width: 100%; overflow: auto; border: solid 1px #f6f6f6;"

    override val thCss =
        "background: #f6f6f6; color: #000; text-align: left; padding: 12px 7px; line-height: 24px; font-weight: bold;"

    override val tdCss =
        "padding: 12px 7px; line-height: 24px; font-size: 12px; min-width: 120px; color: $fontColor;"

    override val stripedTdCss = "$tdCss background: rgba(77, 208, 225, 0.05);"

    override val h1HasPrefix = false
    override val linkHasIcon = false
    override val quoteHasMarks = true
    override val firstLetterCapitalized = false
}

/**
 * V-Green export styles adapted from juejin-markdown-theme-v-green at commit 015f88b.
 * The heading `:first-child` negative top margin and the green ordered-list `::marker` are
 * selector-based effects the per-element inline-CSS model cannot express. The unordered list's
 * green `•` bullets are drawn via `li::before`; the model has no list-item content hook. The
 * link `⇲` glyph renders after the anchor text (the model appends icons; upstream places it
 * before). The `details`/`summary` rules have no model element. Code-block colors continue to
 * come from the active code theme. h1/h2 render at 32px/28px instead of the upstream
 * 2.5rem/2.2rem: the rem-based values are disproportionately large next to the other themes.
 */
internal object VGreenExportStyles : MarkdownExportStyles() {
    override val fontColor = "#333333"

    /** Returns the v-green typography for a heading level from 1 to 6. */
    private fun heading(level: Int): String = when (level) {
        1 -> "font-size: 32px; font-weight: bold; line-height: 1.5; margin: 35px 0 5px; " +
            "padding-bottom: 5px; position: relative; color: $fontColor;"
        2 -> "font-size: 28px; font-weight: bold; line-height: 1.5; margin: 35px 0 10px; " +
            "padding-bottom: 0.5rem; border-bottom: 1px solid #ececec; color: $fontColor;"
        3 -> "font-size: 24px; font-weight: bold; line-height: 1.5; margin: 35px 0 10px; " +
            "padding-bottom: 0; color: $fontColor;"
        4 -> "font-size: 20px; font-weight: bold; line-height: 1.5; margin: 35px 0 10px; " +
            "padding-bottom: 5px; color: $fontColor;"
        5 -> "font-size: 16px; font-weight: bold; line-height: 1.5; margin: 35px 0 10px; " +
            "padding-bottom: 5px; color: $fontColor;"
        else -> "font-size: 15px; font-weight: bold; line-height: 1.5; margin: 5px 0 10px; " +
            "padding-bottom: 5px; color: $fontColor;"
    }

    override fun headingCss(level: Int): String = heading(level)

    /** Renders the green # prefix every heading level carries before its text. */
    override fun headingPrefixHtml(level: Int): String =
        "<span style=\"color: #3eaf7c; padding-right: 0.23em;\">#</span>"

    override val paragraphCss =
        "font-size: 15px; line-height: 1.75; margin: 22px 0; color: $fontColor; word-break: break-word;"

    /** Paragraph override applied to paragraphs nested inside a blockquote (margin: 10px 0). */
    override val quoteParagraphCss =
        "font-size: 15px; line-height: 1.75; margin: 10px 0; color: #666666; word-break: break-word;"

    // The stylesheet keeps the browser's 1em ul/ol vertical margin; 15px is its equivalent at body size.
    override val listCss = "padding-left: 28px; margin: 15px 0;"
    override val listItemCss = "font-size: 15px; line-height: 1.75; margin-bottom: 0; color: $fontColor;"
    override val orderedListItemCss = "$listItemCss padding-left: 6px;"
    override val nestedListCss = "padding-left: 28px; margin: 3px 0 0;"
    override val taskItemPrefixCss = "list-style: none; "

    override val inlineCodeCss =
        "color: #3eaf7c; font-weight: 700; background-color: rgba(27, 31, 35, 0.05); " +
            "border-radius: 3px; font-family: $monospaceFont; font-size: 0.85em; padding: 0.2rem 0.5rem; " +
            "word-break: break-word; overflow-wrap: anywhere; box-decoration-break: clone; " +
            "-webkit-box-decoration-break: clone;"

    override val codeBlockCss =
        "font-family: $monospaceFont; line-height: 1.75; margin: 15px 0; border-radius: 6px; " +
            "border: 2px solid #3eaf7c;"

    /** Keeps the active code palette while applying v-green's compact code type. */
    override fun codeElementCss(codeTheme: CodeTheme) =
        "display: -webkit-box; min-width: 100%; box-sizing: border-box; overflow-x: auto; " +
            "font-weight: 400; font-size: 12px; padding: 15px 12px; margin: 0; word-break: normal; " +
            "white-space: pre; color: ${codeTheme.codeRgb.toCssColor()}; " +
            "background: ${codeTheme.backgroundRgb.toCssColor()};"

    override val emCss = "font-style: italic;"

    override val boldColor = "#3eaf7c"

    override val strikethroughCss = "color: $fontColor;"

    /** Anchors keep the stylesheet's static look; the hover border is a dynamic effect. */
    override val linkCss = "font-weight: 500; text-decoration: none; color: #3eaf7c;"

    /** Renders the ⇲ glyph after the anchor text; upstream places it before via a::before. */
    override val linkIconSpan = "<span style=\"color: #3eaf7c;\">\u21F2</span>"

    override val imgCss =
        "display: block; margin: 0 auto; max-width: 100%; border-radius: 2px; " +
            "border: 3px solid rgba(62, 175, 124, 0.2);"

    override val blockquoteCss =
        "color: #666666; padding: 1px 23px; margin: 22px 0; border-left: 0.5rem solid #42b983; " +
            "background-color: #f8f8f8;"

    override val hrCss =
        "border: none; border-top: 1px solid #3eaf7c; margin: 32px 0;"

    override val tableCss =
        "display: inline-block; font-size: 12px; width: auto; max-width: 100%; overflow: auto; " +
            "border: solid 1px #3eaf7c;"

    override val thCss =
        "background: #3eaf7c; color: #fff; text-align: left; padding: 12px 7px; line-height: 24px; font-weight: bold;"

    override val tdCss =
        "padding: 12px 7px; line-height: 24px; font-size: 12px; min-width: 120px; color: $fontColor;"

    override val stripedTdCss = "$tdCss background: rgba(62, 175, 124, 0.2);"

    override val h1HasPrefix = false
    override val linkHasIcon = true
    override val quoteHasMarks = false
    override val firstLetterCapitalized = false
}

/**
 * Claudette export styles adapted from CookPiu/typora-theme-claudette at commit 0a6c75a (light variant).
 * Known divergences from the upstream stylesheet, kept to stay aligned with the Compose preview:
 * `strong` renders at the browser-default 700 instead of the variable 540 (no weight hook in the model),
 * list `::marker` colors (clay bullets, tertiary numbers) cannot be expressed by inline CSS on the `li`,
 * the hr star masks the hairline with a white chip (the preview surface is assumed white), and
 * h6's upstream `padding-left 0.6em` is reduced by the 2px border so the ink starts 8px from the edge
 * exactly like the preview. Headings h1-h4 carry the serif stack; body text stays sans-serif.
 * Heading sizes are retuned to an even 28-18 ladder (h1-h4 at weight 460, h5-h6 at weight 400),
 * a deliberate departure from upstream's 36-12 scale. Headings use equal top and bottom margins,
 * and the document's first block drops its top margin per upstream's `:first-child` rule.
 */
internal object ClaudetteExportStyles : MarkdownExportStyles() {
    private const val serifFont = "Georgia, 'Source Serif 4', serif"
    private const val hairline = "rgba(31, 30, 29, 0.12)"

    override val fontColor = "#141413"

    /** Returns the claudette typography for a heading level from 1 to 6. */
    override fun headingCss(level: Int): String = headingCss(level, atDocumentStart = false)

    /**
     * Builds the claudette heading CSS; [atDocumentStart] drops the top margin so a heading that
     * opens the document sits flush with the top edge, mirroring the stylesheet's
     * `#write > *:first-child { margin-top: 0 }` rule.
     */
    override fun headingCss(level: Int, atDocumentStart: Boolean): String {
        val serif = if (level <= 4) " font-family: $serifFont;" else ""
        val spacing = "${headingSpacing(level)}px"
        val top = if (atDocumentStart) "0" else spacing
        return when (level) {
            // The short 40px clay underline is drawn by headingSuffixHtml, so no padding-bottom here.
            1 -> "font-size: 28px; font-weight: 460; line-height: 32px; margin: $top 0 $spacing;$serif"
            2 -> "font-size: 26px; font-weight: 460; line-height: 32px; margin: $top 0 $spacing; " +
                "padding-bottom: 10px; border-bottom: 1px solid $hairline;$serif"
            3 -> "font-size: 24px; font-weight: 460; line-height: 30px; margin: $top 0 $spacing;$serif"
            4 -> "font-size: 22px; font-weight: 460; line-height: 30px; margin: $top 0 $spacing;$serif"
            5 -> "font-size: 20px; font-weight: 400; line-height: 28px; margin: $top 0 $spacing;"
            // The 6px padding plus the 2px bar puts the ink 8px from the edge, matching the preview.
            else -> "font-size: 18px; font-weight: 400; line-height: 24px; margin: $top 0 $spacing; " +
                "color: #73726c; text-transform: uppercase; letter-spacing: 1px; " +
                "border-left: 2px solid #d97757; padding-left: 6px;"
        }
    }

    /** Returns the vertical margin claudette applies above and below a heading level. */
    private fun headingSpacing(level: Int): Int = when (level) {
        1 -> 32
        2 -> 18
        3 -> 14
        4 -> 12
        5 -> 8
        else -> 6
    }

    /** Renders the clay accent dot claudette places before level-three headings. */
    override fun headingPrefixHtml(level: Int): String = if (level == 3) {
        "<span style=\"display: inline-block; width: 8px; height: 8px; border-radius: 50%; " +
            "background: #d97757; margin-right: 12px; vertical-align: middle;\"></span>"
    } else {
        ""
    }

    /** Renders the short rounded clay underline claudette draws after level-one headings. */
    override fun headingSuffixHtml(level: Int): String = if (level == 1) {
        "<span style=\"display: block; width: 40px; height: 2px; border-radius: 1px; " +
            "background: #d97757; margin-top: 16px;\"></span>"
    } else {
        ""
    }

    override val paragraphCss =
        "font-size: 17px; line-height: 28px; margin: 0 0 18px; color: $fontColor; word-break: break-word;"

    /** The lead paragraph directly after h1 (upstream `h1 + p`), rendered larger and secondary-colored. */
    override val leadParagraphCss =
        "font-size: 20px; line-height: 30px; margin: 0 0 18px; color: #52514e; word-break: break-word;"

    override val quoteParagraphCss =
        "font-family: $serifFont; font-size: 18px; line-height: 28px; margin: 0 0 18px; " +
            "color: #52514e; word-break: break-word;"

    override val listCss = "padding-left: 26px; margin: 0 0 18px;"
    override val listItemCss = "font-size: 17px; line-height: 28px; margin: 6px 0 0; color: $fontColor;"

    // ponytail: inline CSS cannot target ::marker, so the clay bullets and tertiary numbers stay default.
    override val orderedListItemCss = listItemCss
    override val nestedListCss = "padding-left: 26px; margin: 4px 0 0;"
    override val taskItemPrefixCss = "list-style: none; "

    override val inlineCodeCss =
        "color: #9c4a21; background: rgba(217, 119, 87, 0.10); padding: 2px 6px; border-radius: 4px; " +
            "font-family: $monospaceFont; font-size: 14px; font-style: normal; word-break: break-word; " +
            "overflow-wrap: anywhere; box-decoration-break: clone; -webkit-box-decoration-break: clone;"

    // position: relative anchors the language pill; the pill's 12px spacer raises the code top padding to 28px.
    override val codeBlockCss =
        "position: relative; font-family: $monospaceFont; margin: 24px 0; " +
            "border: 1px solid $hairline; border-radius: 12px; overflow: hidden;"

    /** Renders the uppercase language pill claudette badges fenced code with; empty without a language. */
    override fun codeBlockHeaderHtml(language: CodeLanguage?): String = if (language != null) {
        "<span style=\"display: block; height: 12px;\"><span style=\"position: absolute; top: 8px; " +
            "right: 12px; padding: 2px 6px; border-radius: 999px; background: #ece9df; color: #73726c; " +
            "font-size: 12px; letter-spacing: 1px; text-transform: uppercase;\">${language.displayLabel()}</span></span>"
    } else {
        ""
    }

    override fun codeElementCss(codeTheme: CodeTheme) =
        "display: -webkit-box; min-width: 100%; box-sizing: border-box; overflow-x: auto; " +
            "font-weight: 400; font-size: 14px; line-height: 22px; padding: 16px 20px; margin: 0; " +
            "word-break: normal; white-space: pre; color: ${codeTheme.codeRgb.toCssColor()}; " +
            "background: ${codeTheme.backgroundRgb.toCssColor()};"

    override val emCss = "font-style: italic;"

    override val strikethroughCss = "color: #87867f;"

    override val linkCss =
        "color: #141413; text-decoration-line: underline; text-decoration-color: rgba(217, 119, 87, 0.45);"

    /** Real-element replacement of claudette's a::after north-east arrow glyph. */
    override val linkIconSpan =
        "<span style=\"color: #87867f; font-size: 12px; margin-left: 2px; vertical-align: 6px;\">\u2197</span>"

    /** The stylesheet's 1px ring is drawn as a box-shadow; the preview has no ring equivalent. */
    override val imgCss =
        "display: block; margin: 18px auto; max-width: 100%; border-radius: 8px; " +
            "box-shadow: 0 0 0 1px $hairline;"

    override val tableImgCss = "display: block; margin: 0 auto; max-width: 100%; border-radius: 8px;"

    override val blockquoteCss =
        "position: relative; font-family: $serifFont; font-size: 18px; line-height: 28px; " +
            "color: #52514e; padding: 4px 0 4px 32px; margin: 26px 0; " +
            "border-left: 2px solid rgba(217, 119, 87, 0.55);"

    /** Nested quotes only relax the vertical margin; the clay bar stays (the preview model has no per-level border). */
    override val nestedBlockquoteCss =
        "position: relative; font-family: $serifFont; font-size: 18px; line-height: 28px; " +
            "color: #52514e; padding: 4px 0 4px 32px; margin: 14px 0; " +
            "border-left: 2px solid rgba(217, 119, 87, 0.55);"

    /** Typography of the decorative serif opening quote claudette pins to the quote's corner. */
    override val quoteOpenCss =
        "position: absolute; left: 10px; top: -2px; font-family: $serifFont; font-size: 36px; " +
            "line-height: 1; color: rgba(217, 119, 87, 0.35);"

    override val quoteHasClosingMark = false
    override val quoteOpenMark = "\u201C"

    /** Centered-star rule: the hairline is a full-width span masked by the white-backed glyph span. */
    override val hrCss =
        "position: relative; height: 14px; line-height: 14px; font-size: 12px; margin: 40px 0; " +
            "border: none; text-align: center; overflow: visible;"

    override val hrInnerHtml =
        "<span style=\"position: absolute; left: 0; right: 0; top: 6px; height: 1px; " +
            "background: $hairline;\"></span>" +
            "<span style=\"position: relative; padding: 0 10px; background: #ffffff; color: #d97757;\">\u2733</span>"

    // ponytail: th's upstream 8px bottom padding is flattened to the 10px the preview uses on every side.
    override val tableCss =
        "width: 100%; margin: 18px 0; font-size: 15px; line-height: 24px; border: 1px solid $hairline; " +
            "border-spacing: 0; border-collapse: separate; border-radius: 8px; overflow: hidden;"

    override val thCss =
        "text-align: left; background: #f5f4ed; color: #73726c; font-size: 12px; font-weight: 460; " +
            "text-transform: uppercase; padding: 10px 12px; border-bottom: 1px solid $hairline;"

    // ponytail: :last-child is not expressible inline, so the final row keeps its hairline bottom border.
    override val tdCss =
        "text-align: left; padding: 10px 12px; color: $fontColor; border-bottom: 1px solid $hairline;"

    override val stripedTdCss = tdCss

    override val h1HasPrefix = false
    override val linkHasIcon = true
    override val quoteHasMarks = true
    override val firstLetterCapitalized = false
}
