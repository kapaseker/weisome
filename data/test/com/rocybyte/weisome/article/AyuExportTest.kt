package com.rocybyte.weisome.article

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AyuExportTest {
    /** Renders a document exercising ayu's structural styles through the public entry point. */
    private fun renderAyu(): String = MarkdownToWechatHtml.render(
        "# Title\n\nPlain paragraph.\n\n## Section\n\nText with [link](https://example.com), `code` and ~~gone~~.\n\n" +
            "> Quote\n\n```kotlin\nval x = 1\n```\n\n---\n\n| a | b |\n|---|---|\n| 1 | 2 |",
        MarkdownThemeId.AYU,
        CodeThemeId.GITHUB_LIGHT,
    )

    @Test
    /** Verifies headings carry the Bear medium weight, own size scale, and AvenirNext-Medium family. */
    fun `renders bear headings with medium weight`() {
        val html = renderAyu()
        assertTrue(
            html.contains(
                "font-size: 1.5em; font-weight: 500; line-height: 1.5em; margin: 1.5em 0 0.75em; " +
                    "color: #4d5255; font-family: AvenirNext-Medium;",
            ),
        )
        assertTrue(html.contains("font-size: 1.3em; font-weight: 500;"))
        assertFalse(html.contains("border-bottom: 1px solid"))
    }

    @Test
    /** Verifies the blockquote renders the accent bar and keeps the muted body color. */
    fun `renders accent quote bar without background`() {
        val html = renderAyu()
        assertTrue(html.contains("padding-left: 0.8em; border-left: 0.2em solid #ff6a00; margin: 0.75em 0 0.45em;"))
    }

    @Test
    /** Verifies the code frame carries the gray border and the code theme background and base ink. */
    fun `frames code block with gray border and code theme background`() {
        val html = renderAyu()
        assertTrue(html.contains("border: 1px solid #bfc1c4; background: #f6f8fa; white-space: pre; overflow: hidden;\">"))
        assertTrue(
            html.contains("white-space: nowrap; font-family: Menlo, Monaco, Consolas, 'Courier New', monospace; color: #1f2328;"),
        )
    }

    @Test
    /** Verifies inline code renders the bordered white pill with the dark ink. */
    fun `styles inline code with bordered pill`() {
        val html = renderAyu()
        assertTrue(
            html.contains("border: solid 1px #bfc1c4; border-radius: 0.3em; padding: 0.2em 0.5em; font-size: 0.9em; color: #272d38;"),
        )
    }

    @Test
    /** Verifies links stay plain orange without underline and strikethrough keeps the muted ink. */
    fun `keeps links plain and strikethrough muted`() {
        val html = renderAyu()
        assertTrue(html.contains("color: #e66700; text-decoration: none;"))
        assertTrue(html.contains("color: #8d8f95;"))
    }

    @Test
    /** Verifies the rule renders the 1px hairline and the table keeps normal-weight headers. */
    fun `renders hairline rule and normal-weight table headers`() {
        val html = renderAyu()
        assertTrue(html.contains("height: 1px; border: 0; background-color: #bfc1c4; margin: 0.75em auto;"))
        assertTrue(html.contains("border-collapse: collapse;"))
        assertTrue(html.contains("font-weight: normal;"))
    }
}
