package com.rocybyte.weisome.article

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
    /** Verifies the SMART_BLUE theme renders the centered heading and body typography end to end. */
    fun `renders smart blue theme through the public entry point`() {
        assertEquals(
            "<h1 style=\"font-size: 22px; font-weight: 700; line-height: 1.5; color: #135ce0; " +
                "padding: 0; margin: 35px 0 26px; text-align: center;\">Title</h1>\n" +
                "<p style=\"font-size: 15px; line-height: 2; margin: 0; color: #595959; word-break: break-word;\">Body</p>",
            MarkdownToWechatHtml.render("# Title\n\nBody", MarkdownThemeId.SMART_BLUE, CodeThemeId.GITHUB_LIGHT),
        )
    }

    @Test
    /** Verifies the Typora Paper theme renders its editorial heading decoration and warm body palette. */
    fun `renders typora paper theme through the public entry point`() {
        val html = MarkdownToWechatHtml.render(
            "# Title\n\nBody",
            MarkdownThemeId.TYPORA_PAPER,
            CodeThemeId.GITHUB_LIGHT,
        )

        assertTrue(html.contains("font-size: 30px; font-weight: 800; line-height: 1.18;"))
        assertTrue(html.contains("background: #f4d758;"))
        assertTrue(html.contains("font-size: 16px; line-height: 1.82;"))
        assertTrue(html.contains("color: #1a1a2e;"))
    }

    @Test
    /** Verifies the Rim theme reaches every major article renderer through the public entry point. */
    fun `renders rim theme through the public entry point`() {
        val html = MarkdownToWechatHtml.render(
            "# Title\n\nBody with [link](https://example.com) and `code`.\n\n> Quote\n\n| A | B |\n| - | - |\n| 1 | 2 |\n\n```kotlin\nval answer = 42\n```",
            MarkdownThemeId.RIM,
            CodeThemeId.GITHUB_LIGHT,
        )

        assertTrue(html.contains("font-size: 34.2px; font-weight: 700; line-height: 1.3;"))
        assertTrue(html.contains("font-size: 18px; line-height: 1.7;"))
        assertTrue(html.contains("color: #3e3282;"))
        assertTrue(html.contains("border-left: 3px solid #4e3e8b;"))
        assertTrue(html.contains("border: 1px solid #cccccc;"))
        assertTrue(html.contains("border: 1px solid #e7eaed;"))
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
        assertTrue(html.contains("background: #000000;"))
    }

    @Test
    /** Verifies the Cyanosis theme renders its headings, decorations, and body palette end to end. */
    fun `renders cyanosis theme through the public entry point`() {
        val html = MarkdownToWechatHtml.render(
            "## Title\n\n### Section\n\nBody with [link](https://example.com), **bold** and ~~gone~~.\n\n> Quote\n\n| A | B |\n| - | - |\n| 1 | 2 |\n",
            MarkdownThemeId.CYANOSIS,
            CodeThemeId.GITHUB_LIGHT,
        )

        assertTrue(html.contains("font-size: 24px; font-weight: bold; line-height: 1.5;"))
        assertTrue(html.contains("position: absolute; top: -6px; left: -10px;"))
        assertTrue(html.contains("color: #005bb7;"))
        assertTrue(html.contains("color: #353535;"))
        assertTrue(html.contains("color: #2196f3;"))
        assertTrue(html.contains("color: #ccc;"))
        assertTrue(html.contains("border-left: 4px solid #2196f3; background-color: #f0fdff;"))
        assertTrue(html.contains("border: 1px solid #c3e0fd;"))
        assertTrue(html.contains("color: #3da8f5; text-decoration: none; border-bottom: 1px solid #bedcff;"))
    }

    @Test
    /** Verifies the Cyan theme renders its headings, decorations, quote marks, and body palette end to end. */
    fun `renders cyan theme through the public entry point`() {
        val html = MarkdownToWechatHtml.render(
            "# Title\n\n## Heading\n\n### Section\n\nBody with **bold**, *italic* and ~~gone~~.\n\n> Quote\n\n---\n\n| A | B |\n| - | - |\n| 1 | 2 |\n",
            MarkdownThemeId.CYAN,
            CodeThemeId.GITHUB_LIGHT,
        )

        assertTrue(html.contains("font-size: 24px; font-weight: bold; color: #4dd0e1;"))
        assertTrue(html.contains("border-bottom: 4px solid #4dd0e1;"))
        assertTrue(html.contains("data:image/png;base64,iVBORw0KGgo"))
        assertTrue(html.contains("color: #2b2b2b;"))
        assertTrue(html.contains("color: #26c6da;"))
        assertTrue(html.contains("font-style: normal; color: #4dd0e1; font-weight: bold;"))
        assertTrue(html.contains("background: rgba(77, 208, 225, 0.15);"))
        assertTrue(html.contains("border-left: 4px solid #26c6da;"))
        assertTrue(html.contains("border-top: 1px solid #4dd0e1;"))
        assertTrue(html.contains("border: solid 1px #f6f6f6;"))
        assertTrue(html.contains("background: #f6f6f6; color: #000;"))
    }

    @Test
    /** Verifies the V-Green theme renders its green palette, heading prefixes, and structure end to end. */
    fun `renders v-green theme through the public entry point`() {
        val html = MarkdownToWechatHtml.render(
            "# Title\n\n## Heading\n\nBody with **bold**, `code`, [link](https://example.com), *italic* and ~~gone~~.\n\n> Quote\n\n---\n\n```kotlin\nval ok = true\n```\n\n| A | B |\n| - | - |\n| 1 | 2 |\n",
            MarkdownThemeId.V_GREEN,
            CodeThemeId.GITHUB_LIGHT,
        )

        assertTrue(html.contains("font-size: 32px; font-weight: bold;"))
        assertTrue(html.contains("padding-right: 0.23em;\">#</span>"))
        assertTrue(html.contains("color: #3eaf7c;"))
        assertTrue(html.contains("font-weight: 700; background-color: rgba(27, 31, 35, 0.05);"))
        assertTrue(html.contains("border: 2px solid #3eaf7c;"))
        assertTrue(html.contains("border-left: 0.5rem solid #42b983; background-color: #f8f8f8;"))
        assertTrue(html.contains("color: #333333;"))
        assertTrue(html.contains("\u21F2</span>"))
        assertTrue(html.contains("border: solid 1px #3eaf7c;"))
        assertTrue(html.contains("background: #3eaf7c; color: #fff;"))
        assertTrue(html.contains("border-top: 1px solid #3eaf7c;"))
    }

    @Test
    /** Verifies the Chocolate theme floors every ≤14px font size at 14px (h6, code, table, inline code). */
    fun `renders chocolate theme through the public entry point`() {
        val html = MarkdownToWechatHtml.render(
            "# Title\n\n###### Tiny\n\nBody with `code`.\n\n```kotlin\nval ok = true\n```\n\n| A | B |\n| - | - |\n| 1 | 2 |\n",
            MarkdownThemeId.CHOCOLATE,
            CodeThemeId.GITHUB_LIGHT,
        )

        assertTrue(html.contains("font-size: 24px; font-weight: bold; line-height: 36px;"))
        assertTrue(html.contains("width: 24px; height: 24px; margin-right: 6px;"))
        assertTrue(html.contains("font-size: 14px; font-weight: bold; line-height: 1.5; margin: 28px 0 10px;"))
        assertTrue(html.contains("font-size: 14px; font-style: normal;"))
        assertTrue(html.contains("font-weight: 400; font-size: 14px; padding: 15px 12px;"))
        assertTrue(html.contains("margin: 0; font-size: 14px; width: 100%;"))
        assertTrue(html.contains("line-height: 24px; font-size: 14px;"))
        assertFalse(html.contains("font-size: 12px"))
        assertFalse(html.contains("font-size: 0.87em"))
    }
}
