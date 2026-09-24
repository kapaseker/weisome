package com.rocybyte.weisome.article

/**
 * Per-theme inline CSS for the WeChat HTML export.
 * Each theme mirrors its canonical stylesheet in docs/theme/: HYDROGEN follows docs/theme/hydrogen/hydrogen.scss
 * (DawnLck/juejin-markdown-theme-hydrogen@b3f86fb), GITHUB follows docs/theme/github/github.scss
 * (primer/css src/markdown, light values resolved), SMART_BLUE follows docs/theme/smart-blue/smart-blue.css
 * (cumt-robin/juejin-markdown-theme-smart-blue@f740565), and TYPORA_PAPER follows
 * docs/theme/typora-paper at lisitan/esther-obsidian-typora-themes@8c4f912, and RIM follows
 * docs/theme/rim/rim.css at Rimseg/typora-theme-rim@f0d54ef. Keep the shared-ui Compose preview
 * (MarkdownPreviewStyles) aligned with these values.
 */
internal abstract class MarkdownExportStyles {
    /** Base body text color. */
    abstract val fontColor: String

    /** Builds the inline CSS used to render a heading at the requested level. */
    abstract fun headingCss(level: Int): String

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
    abstract val listCss: String
    abstract val listItemCss: String
    abstract val orderedListItemCss: String
    abstract val nestedListCss: String

    /** Task list items drop the list marker; the leading space keeps it composable with item css. */
    abstract val taskItemPrefixCss: String

    abstract val inlineCodeCss: String
    abstract val codeBlockCss: String

    /** Real-element decoration inserted before code content inside the preformatted frame. */
    open val codeBlockHeaderHtml: String get() = ""

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
        val border = if (spec.borderBottom) " padding-bottom: 0.3em; border-bottom: 1px solid #d1d9e0;" else ""
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

    /** Returns Paper's heading metrics resolved at the upstream 16px base size. */
    private fun heading(level: Int): HeadingSpec = when (level) {
        1 -> HeadingSpec("50.4px", "1.18", "27.72px", "36.288px", "#17172a", "-0.045em")
        2 -> HeadingSpec("32px", "1.35", "67.2px", "23.04px", "#17172a", "-0.025em", borderBottom = true)
        3 -> HeadingSpec("21.6px", "1.35", "45.36px", "15.552px", "#2b7fd8", "-0.025em")
        4 -> HeadingSpec("17.92px", "1.35", "37.632px", "12.9024px", "#17172a", "-0.025em")
        5 -> HeadingSpec("15.68px", "1.35", "32.928px", "11.2896px", "#17172a", "0.02em")
        else -> HeadingSpec("14.08px", "1.35", "29.568px", "10.1376px", "#555568", "0.08em")
    }

    /** Builds the Paper heading style without changing the application's selected font. */
    override fun headingCss(level: Int): String {
        val spec = heading(level)
        val border = if (spec.borderBottom) " padding-bottom: 13.44px; border-bottom: 1px solid #e8e0cf;" else ""
        return "font-size: ${spec.size}; font-weight: 800; line-height: ${spec.lineHeight}; " +
            "margin: ${spec.top} 0 ${spec.bottom}; color: ${spec.color}; letter-spacing: ${spec.letterSpacing};$border"
    }

    /** Renders Paper's paired blue and yellow dots before level-two headings. */
    override fun headingPrefixHtml(level: Int): String = if (level == 2) {
        "<span style=\"position: relative; display: inline-block; width: 23px; height: 23px; margin-right: 7px; " +
            "vertical-align: -5px;\"><span style=\"position: absolute; left: 8px; top: 7px; width: 15px; height: 15px; " +
            "border-radius: 50%; background: #f4d758;\"></span><span style=\"position: absolute; left: 0; top: 0; " +
            "width: 15px; height: 15px; border-radius: 50%; background: #2b7fd8;\"></span></span>"
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
    override val codeBlockHeaderHtml =
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
        "position: relative; min-height: 36.8px; margin: 27.2px 0; padding: 24.8px 28px 23.2px 59.2px; " +
            "overflow: hidden; border: 0; border-radius: 18px; color: #555568; background: #ffffff; " +
            "box-shadow: 0 10px 34px rgba(62, 48, 22, 0.10);"
    override val nestedBlockquoteCss =
        "position: relative; margin: 16px 0 0; padding: 24.8px 28px 23.2px 59.2px; border: 0; " +
            "border-radius: 18px; color: #555568; background: #faf6eb;"
    override val quoteOpenCss =
        "position: absolute; top: 0.02em; left: 0.28em; color: #f4d758; font-size: 73.6px; font-weight: 700; line-height: 1;"
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
