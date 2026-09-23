package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.GitHubExportStyles
import com.rocybyte.weisome.article.HydrogenExportStyles
import com.rocybyte.weisome.article.MarkdownInline
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InlineTest {
    @Test
    /** Verifies inline code exports as the hydrogen red-on-pink literal label. */
    fun `renders styled literal inline code`() {
        assertEquals(
            "Call <code style=\"color: #c0341d; background-color: #fbe5e1; padding: 0.065em 0.4em; border-radius: 2px; " +
                "font-family: Menlo, Monaco, Consolas, 'Courier New', monospace; font-size: 0.87em; " +
                "font-style: normal; word-break: break-word; box-decoration-break: clone; -webkit-box-decoration-break: clone; " +
                "overflow-wrap: anywhere;\">launch(&lt;tag&gt;)</code> now",
            renderInline(
                listOf(
                    MarkdownInline.Text("Call "),
                    MarkdownInline.Code("launch(<tag>)"),
                    MarkdownInline.Text(" now"),
                ),
                HydrogenExportStyles,
            ),
        )
    }

    @Test
    /** Verifies inline emphasis and unsupported HTML characters retain their semantics safely. */
    fun `renders emphasis and escapes unsupported html`() {
        assertEquals(
            "&lt;script&gt;<strong>bold</strong>" +
                "<em style=\"font-style: italic; text-emphasis: dot; text-emphasis-position: under;\">italic</em>" +
                "&quot;x&quot; &#39;y&#39; &amp;",
            renderInline(
                listOf(
                    MarkdownInline.Text("<script>"),
                    MarkdownInline.Bold("bold"),
                    MarkdownInline.Italic("italic"),
                    MarkdownInline.Text("\"x\" 'y' &"),
                ),
                HydrogenExportStyles,
            ),
        )
    }

    @Test
    /** Verifies links export with the hydrogen color, border, and trailing icon span. */
    fun `renders links with the trailing icon`() {
        val html = renderInline(listOf(MarkdownInline.Link("docs", "https://example.com/a?b=1")), HydrogenExportStyles)

        assertTrue(html.startsWith("<a href=\"https://example.com/a?b=1\" style=\"color: #027fff; text-decoration: none; margin: 0 4px; padding-bottom: 4px; border-bottom: 2px solid transparent;\">docs</a><span"))
        assertTrue(html.contains("width: 18px; height: 18px;"))
        assertTrue(html.contains("data:image/svg+xml;base64,"))
    }

    @Test
    /** Verifies strikethrough exports with the hydrogen faded color. */
    fun `renders strikethrough with faded color`() {
        assertEquals(
            "<del style=\"color: rgba(0, 0, 0, 0.6);\">gone</del>",
            renderInline(listOf(MarkdownInline.Strikethrough("gone")), HydrogenExportStyles),
        )
    }

    @Test
    /** Verifies images export with the elevation shadow unless rendered inside a table. */
    fun `renders images with elevation shadow outside tables only`() {
        val image = MarkdownInline.Image("logo", "https://example.com/i.png")

        assertTrue(renderInline(listOf(image), HydrogenExportStyles).contains("box-shadow:"))
        assertEquals(
            "<img src=\"https://example.com/i.png\" alt=\"logo\" style=\"display: block; margin: 0 auto; max-width: 100%; border-radius: 2px;\">",
            renderInline(listOf(image), HydrogenExportStyles, imageShadow = false),
        )
    }

    @Test
    /** Verifies the GitHub theme renders inline code with the neutral grey-on-lavender pill. */
    fun `renders github inline code and links`() {
        assertEquals(
            "Call <code style=\"color: #1f2328; background-color: rgba(175, 184, 193, 0.2); padding: 0.2em 0.4em; " +
                "border-radius: 6px; font-family: Menlo, Monaco, Consolas, 'Courier New', monospace; " +
                "font-size: 0.85em; font-style: normal; white-space: break-spaces; word-break: break-word; " +
                "box-decoration-break: clone; -webkit-box-decoration-break: clone; " +
                "overflow-wrap: anywhere;\">launch(&lt;tag&gt;)</code> now",
            renderInline(
                listOf(
                    MarkdownInline.Text("Call "),
                    MarkdownInline.Code("launch(<tag>)"),
                    MarkdownInline.Text(" now"),
                ),
                GitHubExportStyles,
            ),
        )
        assertEquals(
            "<a href=\"https://example.com/a?b=1\" style=\"color: #0969da; text-decoration: underline;\">docs</a>",
            renderInline(listOf(MarkdownInline.Link("docs", "https://example.com/a?b=1")), GitHubExportStyles),
        )
    }

    @Test
    /** Verifies the first-letter rule uppercases leading text but skips code inlines. */
    fun `capitalizes the first letter of leading text inlines`() {
        assertEquals(
            listOf(MarkdownInline.Bold("Bold"), MarkdownInline.Text(" rest")),
            capitalizeFirstLetter(listOf(MarkdownInline.Bold("bold"), MarkdownInline.Text(" rest"))),
        )
        assertEquals(
            listOf(MarkdownInline.Code("code"), MarkdownInline.Text(" after")),
            capitalizeFirstLetter(listOf(MarkdownInline.Code("code"), MarkdownInline.Text(" after"))),
        )
    }
}
