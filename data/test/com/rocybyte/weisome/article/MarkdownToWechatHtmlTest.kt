package com.rocybyte.weisome.article

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MarkdownToWechatHtmlTest {
    @Test
    /** Verifies the public Markdown entry point preserves block order across renderer dispatch. */
    fun `renders parsed blocks in document order`() {
        assertEquals(
            "<h1 style=\"font-size: 30px; font-weight: 500; line-height: 1.5; margin: 35px 0 5px; padding-bottom: 5px;\">" +
                "<span style=\"color: #1976d2; margin-right: 10px;\">#</span>Title</h1>\n" +
                "<p style=\"font-size: 16px; line-height: 1.75; margin: 22px 0; color: rgba(46, 36, 36, 0.87); word-break: break-word;\">Body</p>",
            MarkdownToWechatHtml.render("# Title\n\nBody", MarkdownThemeId.HYDROGEN, CodeThemeId.GITHUB_LIGHT),
        )
    }

    @Test
    /** Verifies the public entry point dispatches every supported block type in source order. */
    fun `dispatches every block type through the public entry point`() {
        val html = MarkdownToWechatHtml.render(
            "# Title\n\nBody\n\n- Item\n\n```kotlin\nval tag = \"<code>\"\n```",
            MarkdownThemeId.HYDROGEN,
            CodeThemeId.GITHUB_LIGHT,
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
        assertEquals("", MarkdownToWechatHtml.render("   \n\n", MarkdownThemeId.GITHUB, CodeThemeId.GITHUB_LIGHT))
    }

    @Test
    /** Verifies the GITHUB theme renders the primer body style without the hydrogen decorations. */
    fun `renders github theme through the public entry point`() {
        assertEquals(
            "<h1 style=\"font-size: 2em; font-weight: 600; line-height: 1.25; margin: 24px 0 16px; " +
                "padding-bottom: 0.3em; border-bottom: 1px solid #d1d9e0;\">Title</h1>\n" +
                "<p style=\"font-size: 16px; line-height: 1.5; margin: 0 0 16px; color: #1f2328; word-break: break-word;\">Body</p>",
            MarkdownToWechatHtml.render("# Title\n\nBody", MarkdownThemeId.GITHUB, CodeThemeId.GITHUB_LIGHT),
        )
    }

    @Test
    /** Verifies the public entry point forwards the code theme so code text follows it, not the Markdown theme. */
    fun `forwards the code theme to code blocks through the public entry point`() {
        val html = MarkdownToWechatHtml.render(
            "```kotlin\nclass Worker\n```",
            MarkdownThemeId.GITHUB,
            CodeThemeId.MATRIX,
        )

        assertTrue(html.contains("color: #008500;"))
        assertTrue(html.contains("background: #f6f8fa;"))
    }
}
