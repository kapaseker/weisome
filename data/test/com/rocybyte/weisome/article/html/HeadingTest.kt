package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.GitHubExportStyles
import com.rocybyte.weisome.article.HydrogenExportStyles
import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownInline
import com.rocybyte.weisome.article.RimExportStyles
import com.rocybyte.weisome.article.SmartBlueExportStyles
import com.rocybyte.weisome.article.TyporaPaperExportStyles
import kotlin.test.Test
import kotlin.test.assertEquals

class HeadingTest {
    @Test
    /** Verifies a level-one heading renders the hydrogen style and blue "#" prefix. */
    fun `renders a level one heading with inline style`() {
        assertEquals(
            "<h1 style=\"font-size: 30px; font-weight: 500; line-height: 1.5; margin: 35px 0 5px; padding-bottom: 5px;\">" +
                "<span style=\"color: #1976d2; margin-right: 10px;\">#</span>Hello</h1>",
            renderHeading(MarkdownBlock.Heading(1, listOf(MarkdownInline.Text("Hello"))), HydrogenExportStyles),
        )
    }

    @Test
    /** Verifies a level-two heading renders the left border and capitalized first letter. */
    fun `renders a level two heading with border and capitalization`() {
        assertEquals(
            "<h2 style=\"font-size: 28px; font-weight: 400; line-height: 1.5; margin: 20px 0 10px; padding: 0 0 5px 10px; border-left: 5px solid #454545;\">Title text</h2>",
            renderHeading(MarkdownBlock.Heading(2, listOf(MarkdownInline.Text("title text"))), HydrogenExportStyles),
        )
    }

    @Test
    /** Verifies a level-six heading falls back to the body font size with tight top margin. */
    fun `renders a level six heading with body size`() {
        assertEquals(
            "<h6 style=\"font-size: 16px; font-weight: 400; line-height: 1.5; margin: 5px 0 10px; padding-bottom: 5px;\">Small</h6>",
            renderHeading(MarkdownBlock.Heading(6, listOf(MarkdownInline.Text("Small"))), HydrogenExportStyles),
        )
    }

    @Test
    /** Verifies the GitHub theme omits the "#" prefix and keeps authored casing. */
    fun `renders github heading without prefix or capitalization`() {
        assertEquals(
            "<h1 style=\"font-size: 2em; font-weight: 600; line-height: 1.25; margin: 24px 0 16px; " +
                "padding-bottom: 0.3em; border-bottom: 1px solid #d1d9e0;\">Hello</h1>",
            renderHeading(MarkdownBlock.Heading(1, listOf(MarkdownInline.Text("Hello"))), GitHubExportStyles),
        )
        assertEquals(
            "<h2 style=\"font-size: 1.5em; font-weight: 600; line-height: 1.25; margin: 24px 0 16px; " +
                "padding-bottom: 0.3em; border-bottom: 1px solid #d1d9e0;\">title text</h2>",
            renderHeading(MarkdownBlock.Heading(2, listOf(MarkdownInline.Text("title text"))), GitHubExportStyles),
        )
    }

    @Test
    /** Verifies the smart-blue theme centers level-one headings and borders level-two headings in blue. */
    fun `renders smart blue headings with centered h1 and bordered h2`() {
        assertEquals(
            "<h1 style=\"font-size: 22px; font-weight: 700; line-height: 1.5; color: #135ce0; " +
                "padding: 0; margin: 35px 0 26px; text-align: center;\">Hello</h1>",
            renderHeading(MarkdownBlock.Heading(1, listOf(MarkdownInline.Text("Hello"))), SmartBlueExportStyles),
        )
        assertEquals(
            "<h2 style=\"font-size: 20px; font-weight: 700; line-height: 1.5; color: #135ce0; " +
                "padding: 0 0 0 10px; margin: 30px 0; border-left: 4px solid #135ce0;\">title text</h2>",
            renderHeading(MarkdownBlock.Heading(2, listOf(MarkdownInline.Text("title text"))), SmartBlueExportStyles),
        )
    }

    @Test
    /** Verifies levels three to six share the smart-blue heading rule with no font-size upstream. */
    fun `renders smart blue h4 to h6 with the shared padding and no margin`() {
        assertEquals(
            "<h6 style=\"font-size: 10.05px; font-weight: 700; line-height: 1.5; color: #135ce0; " +
                "padding: 30px 0; margin: 0;\">Small</h6>",
            renderHeading(MarkdownBlock.Heading(6, listOf(MarkdownInline.Text("Small"))), SmartBlueExportStyles),
        )
    }

    @Test
    /** Verifies Paper headings use real inline decorations that survive clipboard HTML sanitization. */
    fun `renders typora paper heading decorations`() {
        val h1 = renderHeading(
            MarkdownBlock.Heading(1, listOf(MarkdownInline.Text("Hello"))),
            TyporaPaperExportStyles,
        )
        val h2 = renderHeading(
            MarkdownBlock.Heading(2, listOf(MarkdownInline.Text("Section"))),
            TyporaPaperExportStyles,
        )

        assertEquals(1, Regex("background: #f4d758").findAll(h1).count())
        assertEquals(1, Regex("background: #2b7fd8").findAll(h2).count())
        assertEquals(1, Regex("background: #f4d758").findAll(h2).count())
    }

    @Test
    /** Verifies Rim headings resolve the upstream rem sizes against its 18px base. */
    fun `renders rim heading scale and muted level six`() {
        assertEquals(
            "<h1 style=\"font-size: 34.2px; font-weight: 700; line-height: 1.3; margin: 18px 0 18px; color: #152e45;\">Hello</h1>",
            renderHeading(MarkdownBlock.Heading(1, listOf(MarkdownInline.Text("Hello"))), RimExportStyles),
        )
        assertEquals(
            "<h6 style=\"font-size: 18px; font-weight: 600; line-height: 1.3; margin: 28.8px 0 18px; color: #47525d;\">Small</h6>",
            renderHeading(MarkdownBlock.Heading(6, listOf(MarkdownInline.Text("Small"))), RimExportStyles),
        )
    }
}
