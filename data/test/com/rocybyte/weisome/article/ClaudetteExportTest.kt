package com.rocybyte.weisome.article

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ClaudetteExportTest {
    /** Renders a document exercising every claudette-only decoration through the public entry point. */
    private fun renderClaudette(): String = MarkdownToWechatHtml.render(
        "# Title\n\nLead paragraph.\n\nPlain paragraph.\n\n## Section\n\n### Sub\n\n###### Label\n\n" +
            "---\n\nText with [link](https://example.com).\n\n> Quote\n\n```kotlin\nval x = 1\n```",
        MarkdownThemeId.CLAUDETTE,
        CodeThemeId.GITHUB_LIGHT,
    )

    @Test
    /** Verifies h1 carries the serif stack and the short 40px clay underline after its text. */
    fun `renders h1 with serif stack and short clay underline`() {
        val html = renderClaudette()
        assertTrue(html.contains("<h1 style=\"font-size: 36px; font-weight: 400; line-height: 40px; margin: 0 0 32px; font-family: Georgia"))
        assertTrue(html.contains("width: 40px; height: 2px; border-radius: 1px; background: #d97757; margin-top: 16px;"))
    }

    @Test
    /** Verifies h3 renders the clay prefix dot and h6 the uppercase label behind the clay bar. */
    fun `renders h3 dot and uppercase h6 label`() {
        val html = renderClaudette()
        assertTrue(html.contains("width: 8px; height: 8px; border-radius: 50%; background: #d97757;"))
        assertTrue(html.contains("text-transform: uppercase; letter-spacing: 1px; border-left: 2px solid #d97757; padding-left: 6px;"))
    }

    @Test
    /** Verifies only the paragraph directly after h1 picks up the lead style. */
    fun `applies lead paragraph css only after h1`() {
        val html = renderClaudette()
        assertTrue(html.contains("font-size: 20px; line-height: 30px; margin: 0 0 18px; color: #52514e;"))
        assertTrue(html.contains("font-size: 17px; line-height: 28px; margin: 0 0 18px; color: #141413;"))
        assertFalse(html.contains("<h2 style=\"font-size: 28px; font-weight: 400; line-height: 34px; margin: 56px 0 18px; padding-bottom: 10px; border-bottom: 1px solid rgba(31, 30, 29, 0.12); font-family: Georgia, 'Source Serif 4', serif;\">Section</h2>\n<p style=\"font-size: 20px"))
    }

    @Test
    /** Verifies the hr renders the hairline with the centered clay star. */
    fun `renders hr with centered star`() {
        val html = renderClaudette()
        assertTrue(html.contains("height: 14px; line-height: 14px; font-size: 12px; margin: 40px 0;"))
        assertTrue(html.contains("background: rgba(31, 30, 29, 0.12);"))
        assertTrue(html.contains("padding: 0 10px; background: #ffffff; color: #d97757;\">✳</span>"))
    }

    @Test
    /** Verifies anchors keep the ink-colored underline and gain the north-east arrow span. */
    fun `appends arrow icon to links`() {
        val html = renderClaudette()
        assertTrue(html.contains("text-decoration-color: rgba(217, 119, 87, 0.45);"))
        assertTrue(html.contains("color: #87867f; font-size: 12px; margin-left: 2px; vertical-align: 6px;\">↗</span>"))
    }

    @Test
    /** Verifies fenced code with a language gets the uppercase pill, without one it stays bare. */
    fun `renders language pill on fenced code`() {
        val html = renderClaudette()
        assertTrue(html.contains("top: 8px; right: 12px; padding: 2px 6px; border-radius: 999px; background: #ece9df;"))
        assertTrue(html.contains("text-transform: uppercase;\">KOTLIN</span>"))
        val bare = MarkdownToWechatHtml.render("```\nplain\n```", MarkdownThemeId.CLAUDETTE, CodeThemeId.GITHUB_LIGHT)
        assertFalse(bare.contains("border-radius: 999px"))
    }
}
