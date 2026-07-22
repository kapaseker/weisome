package com.rocybyte.weisome.article

import kotlin.test.Test
import kotlin.test.assertEquals

class MarkdownToWechatHtmlTest {
    @Test
    /** Verifies that a level-one heading receives the expected inline style. */
    fun `renders a level one heading with inline style`() {
        assertEquals(
            "<h1 style=\"font-size: 24px; font-weight: 700; line-height: 1.4; margin: 24px 0 16px;\">Hello</h1>",
            MarkdownToWechatHtml.render("# Hello"),
        )
    }

    @Test
    /** Verifies paragraph, list, bold, and italic rendering in one document. */
    fun `renders paragraphs lists and inline emphasis`() {
        assertEquals(
            "<p style=\"font-size: 16px; line-height: 1.75; margin: 0 0 16px;\">A <strong>bold</strong> and <em>italic</em> line.<br/>Another line.</p>\n<ul style=\"padding-left: 24px; margin: 0 0 16px;\"><li style=\"font-size: 16px; line-height: 1.75;\">One</li></ul>",
            MarkdownToWechatHtml.render("A **bold** and *italic* line.\nAnother line.\n\n- One"),
        )
    }

    @Test
    /** Verifies HTML escaping and the empty-input rendering contract. */
    fun `escapes unsupported HTML and leaves empty input empty`() {
        assertEquals(
            "<p style=\"font-size: 16px; line-height: 1.75; margin: 0 0 16px;\">&lt;script&gt;alert(&quot;x&quot;)&lt;/script&gt;</p>",
            MarkdownToWechatHtml.render("<script>alert(\"x\")</script>"),
        )
        assertEquals("", MarkdownToWechatHtml.render("   \n\n"))
    }

    @Test
    /** Verifies fenced code is escaped and rendered inside a styled preformatted block. */
    fun `renders escaped fenced code as a preformatted block`() {
        val html = MarkdownToWechatHtml.render("```kotlin\nval tag = \"<code>\"\n```")

        assertEquals(true, html.startsWith("<pre style=\"background: #f6f8fa;"))
        assertEquals(true, html.contains("<code>val tag = &quot;&lt;code&gt;&quot;</code>"))
    }

    @Test
    /** Verifies exported code uses the exact colors supplied by the shared highlight model. */
    fun `renders shared code highlights as inline HTML colors`() {
        val document = MarkdownDocument(
            listOf(
                MarkdownBlock.CodeBlock(
                    language = CodeLanguage.Kotlin,
                    code = "fun main()",
                    highlights = listOf(CodeHighlightSpan(0, 3, 0xCF222E)),
                ),
            ),
        )

        val html = MarkdownToWechatHtml.render(document)

        assertEquals(true, html.contains("<span style=\"color: #cf222e;\">fun</span> main()"))
    }
}
