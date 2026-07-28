package com.rocybyte.weisome.article

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MarkdownToWechatHtmlTest {
    @Test
    /** Verifies the public Markdown entry point preserves block order across renderer dispatch. */
    fun `renders parsed blocks in document order`() {
        assertEquals(
            "<h1 style=\"font-size: 24px; font-weight: 700; line-height: 1.4; margin: 24px 0 16px;\">Title</h1>\n<p style=\"font-size: 16px; line-height: 1.75; margin: 0 0 16px;\">Body</p>",
            MarkdownToWechatHtml.render("# Title\n\nBody"),
        )
    }

    @Test
    /** Verifies the public entry point dispatches every supported block type in source order. */
    fun `dispatches every block type through the public entry point`() {
        val html = MarkdownToWechatHtml.render(
            "# Title\n\nBody\n\n- Item\n\n```kotlin\nval tag = \"<code>\"\n```",
        )
        val headingIndex = html.indexOf("<h1 ")
        val paragraphIndex = html.indexOf("<p ")
        val listIndex = html.indexOf("<ul ")
        val codeIndex = html.indexOf("<pre ")

        assertTrue(headingIndex >= 0)
        assertTrue(paragraphIndex > headingIndex)
        assertTrue(listIndex > paragraphIndex)
        assertTrue(codeIndex > listIndex)
        assertTrue(html.contains("val tag = &quot;&lt;code&gt;&quot;"))
    }

    @Test
    /** Verifies the public Markdown entry point leaves empty input empty. */
    fun `renders empty markdown as empty html`() {
        assertEquals("", MarkdownToWechatHtml.render("   \n\n"))
    }
}
