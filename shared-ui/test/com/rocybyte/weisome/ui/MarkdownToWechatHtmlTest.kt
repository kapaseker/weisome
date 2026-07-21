package com.rocybyte.weisome.ui

import kotlin.test.Test
import kotlin.test.assertEquals

class MarkdownToWechatHtmlTest {

    @Test
    fun `renders a level one heading with inline style`() {
        assertEquals(
            "<h1 style=\"font-size: 24px; font-weight: 700; line-height: 1.4; margin: 24px 0 16px;\">Hello</h1>",
            MarkdownToWechatHtml.render("# Hello"),
        )
    }

    @Test
    fun `renders second and third level headings`() {
        assertEquals(
            "<h2 style=\"font-size: 20px; font-weight: 700; line-height: 1.4; margin: 20px 0 12px;\">Section</h2>\n<h3 style=\"font-size: 18px; font-weight: 700; line-height: 1.4; margin: 16px 0 8px;\">Detail</h3>",
            MarkdownToWechatHtml.render("## Section\n### Detail"),
        )
    }

    @Test
    fun `renders paragraphs and inline emphasis`() {
        assertEquals(
            "<p style=\"font-size: 16px; line-height: 1.75; margin: 0 0 16px;\">A <strong>bold</strong> and <em>italic</em> line.<br/>Another line.</p>",
            MarkdownToWechatHtml.render("A **bold** and *italic* line.\nAnother line."),
        )
    }

    @Test
    fun `renders ordered and unordered lists as separate blocks`() {
        assertEquals(
            "<ul style=\"padding-left: 24px; margin: 0 0 16px;\"><li style=\"font-size: 16px; line-height: 1.75;\">One</li><li style=\"font-size: 16px; line-height: 1.75;\">Two</li></ul>\n<ol style=\"padding-left: 24px; margin: 0 0 16px;\"><li style=\"font-size: 16px; line-height: 1.75;\">First</li><li style=\"font-size: 16px; line-height: 1.75;\">Second</li></ol>",
            MarkdownToWechatHtml.render("- One\n- Two\n\n1. First\n2. Second"),
        )
    }

    @Test
    fun `escapes unsupported HTML and leaves empty input empty`() {
        assertEquals(
            "<p style=\"font-size: 16px; line-height: 1.75; margin: 0 0 16px;\">&lt;script&gt;alert(&quot;x&quot;)&lt;/script&gt;</p>",
            MarkdownToWechatHtml.render("<script>alert(\"x\")</script>"),
        )
        assertEquals("", MarkdownToWechatHtml.render("   \n\n"))
    }

    @Test
    fun `keeps unmatched syntax while rendering complete emphasis`() {
        assertEquals(
            "<p style=\"font-size: 16px; line-height: 1.75; margin: 0 0 16px;\">A <strong>bold</strong> and *unfinished</p>",
            MarkdownToWechatHtml.render("A **bold** and *unfinished"),
        )
    }

    @Test
    fun `keeps a heading without required whitespace as paragraph text`() {
        assertEquals(
            "<p style=\"font-size: 16px; line-height: 1.75; margin: 0 0 16px;\">#not a heading</p>",
            MarkdownToWechatHtml.render("#not a heading"),
        )
    }
}
