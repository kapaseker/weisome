package com.rocybyte.weisome.article

/**
 * Per-theme inline CSS for the WeChat HTML export.
 * Each theme mirrors its canonical SCSS in docs/: HYDROGEN follows docs/hydrogen.scss
 * (DawnLck/juejin-markdown-theme-hydrogen@b3f86fb), GITHUB follows docs/github.scss
 * (primer/css src/markdown, light values resolved). Keep the shared-ui Compose preview
 * (MarkdownPreviewStyles) aligned with these values.
 */
internal abstract class MarkdownExportStyles {
    /** Base body text color. */
    abstract val fontColor: String

    /** Builds the inline CSS used to render a heading at the requested level. */
    abstract fun headingCss(level: Int): String

    /** Inline CSS for the decorative prefix rendered before level-one headings (h1HasPrefix only). */
    open val h1PrefixCss: String get() = ""

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
    abstract val codeElementCss: String
    abstract val emCss: String
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

    abstract val hrCss: String

    /** Real-element replacement of the theme's centered hr logo (hrHasLogo only). */
    open val hrLogoCss: String get() = ""

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

    /** Whether the horizontal rule renders the theme's centered logo element. */
    abstract val hrHasLogo: Boolean

    /** Whether paragraphs and h2/h3 headings capitalize their first letter. */
    abstract val firstLetterCapitalized: Boolean
}

/** Returns the export style set for the requested Markdown theme. */
internal fun exportStylesFor(theme: MarkdownThemeId): MarkdownExportStyles = when (theme) {
    MarkdownThemeId.GITHUB -> GitHubExportStyles
    MarkdownThemeId.HYDROGEN -> HydrogenExportStyles
}

private const val monospaceFont = "Menlo, Monaco, Consolas, 'Courier New', monospace"

/** Hydrogen export styles; values mirror docs/hydrogen.scss. */
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

    override val codeElementCss =
        "display: -webkit-box; min-width: 100%; box-sizing: border-box; overflow-x: auto; " +
            "font-weight: 400; font-size: 12px; padding: 15px 12px; margin: 0; word-break: normal; " +
            "white-space: pre; color: #24292f; background: #f6f8fa; border-radius: 0 4px;"

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

    /** Real-element replacement of hydrogen's hr::after centered juejin logo. */
    override val hrLogoCss =
        "position: absolute; margin: auto; left: 0; right: 0; top: 0; bottom: 0; display: inline-block; " +
            "width: 60px; height: 20px; background: #ffffff; background-repeat: no-repeat; " +
            "background-size: auto 100%; background-position-x: center; " +
            "background-image: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACgAAAAgCAYAAABgrToAAAADoklEQVRYR82XTYgcRRTHf2933Q1RjAa9eFO8JHoJ8RQVBQ2iBwXBET0YEUTXNVmNQtTpmeqaWV0XNRq/o4KoECSCEPSg4CF+BYUkIIiCoCJCPIhC/Ihh2Z0nVV27VnZnenumW9i6ddV7//frV69fVQurfMgq56NawFTPAU6QyomqXrw6wIZeyhCPebA5buNR+akKyGoAjd6BshthnYdSjqNcRVuOlIUsD2j0SuA94IwuMHdh5ZUykOUBXfSGbmKI54EtAeYIHSZoy5dl4JxvNYBOKdW1KE8BQ8AkVk6WhasWsAiN0TX9gveXQaPP+Aytpc4u+bMI06JNohsYYYYOR2lJWtS3OKDRfcAtQfgDoI6Vo4UCGb0OmAEuDvZvYmVbEd/igC3dzDz7gQu8sPA9kJDK27mBmjqBeLjTg90PDFOjWawFFQd06kZHEfaj3LAIpTRpSXsZ5E06zEYP9sDimnAApYaV2SLZG/wjMeqAkijwW4xQJ5Gf/ZzRC8OW3hiBTGGlURRswW55Bh/Ssxljrwew8l1PQaM14GngvGDzBUKdDsMeTtgU5o8B92PFlUf3YXUrHa7Fys6lBqcCGnX15YQ2A18FyPd7Crd1A3M8C1wdbH4DD3hWeP6IEXbQkG97ajR1HPFnuPP5jFFq1OWX7hl8WM9l1AO648uNfwLk7tytMeogty+xeQ4rO3r6bdcx1nuwOGsHmaXGtPzae4uzGnLH1kQkvpdZGrHjssBZJrL+pqS05KWc8tgITAPXRzYvYOXe/C2OV43eDcRBDtIhoS2f9wzc0Cv8Wls+zoFzUC5zF0U241h5uZtPfptp6OUM8wbK+cH5GEpCS17P3fJei0Z3+npTxryJ8CPzbKMtn/ZyWbkPGl0PuFPkmkjkcb4h4R2ZLwRq1H0ALmvjkf2HwK1Y+T1PY2XABe/sHJ6MxN5lnoSpnC/UGbsTaI5phK2R7x6s3Ffk5YoDOrWm3onwJHBmEP86bPmBrsGaenNoIdnxCH+gPEhLXi0Cl1VBvyPVLSh7gEuC62yAfOIUqabWEaaiucMIk6RyqJ+Q/QM69V26jjW86Gvov/EaoyT8zRCn+Xq7PVrbx0nuYUaO9wM3WAbjCE1NEUw09Um4UV+2OKfYfu5/S19gsAzGKqm6LE5FrShbdS0ku465DjDwKA/oQht19ejqbaEVuRbiLhuHByYLjtUAZpDutzP7cYdHsPJXWbjyNVgFwQoa1WXwf4Jd9YD/Ap80+yE7+u9aAAAAAElFTkSuQmCC');"

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
    override val hrHasLogo = true
    override val firstLetterCapitalized = true
}

/** GitHub export styles; values mirror docs/github.scss (primer/css markdown, light). */
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

    override val codeElementCss =
        "display: -webkit-box; min-width: 100%; box-sizing: border-box; overflow-x: auto; " +
            "font-weight: 400; font-size: 0.85em; padding: 16px; margin: 0; word-break: normal; " +
            "white-space: pre; color: #1f2328; background: #f6f8fa; border-radius: 6px;"

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
    override val hrHasLogo = false
    override val firstLetterCapitalized = false
}
