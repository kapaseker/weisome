package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownInline
import kotlin.test.Test
import kotlin.test.assertEquals

class HeadingTest {
    @Test
    /** Verifies a level-one heading renders the hydrogen style and blue "#" prefix. */
    fun `renders a level one heading with inline style`() {
        assertEquals(
            "<h1 style=\"font-size: 30px; font-weight: 500; line-height: 1.5; margin: 35px 0 5px; padding-bottom: 5px;\">" +
                "<span style=\"color: #1976d2; margin-right: 10px;\">#</span>Hello</h1>",
            renderHeading(MarkdownBlock.Heading(1, listOf(MarkdownInline.Text("Hello")))),
        )
    }

    @Test
    /** Verifies a level-two heading renders the left border and capitalized first letter. */
    fun `renders a level two heading with border and capitalization`() {
        assertEquals(
            "<h2 style=\"font-size: 28px; font-weight: 400; line-height: 1.5; margin: 20px 0 10px; padding: 0 0 5px 10px; border-left: 5px solid #454545;\">Title text</h2>",
            renderHeading(MarkdownBlock.Heading(2, listOf(MarkdownInline.Text("title text")))),
        )
    }

    @Test
    /** Verifies a level-six heading falls back to the body font size with tight top margin. */
    fun `renders a level six heading with body size`() {
        assertEquals(
            "<h6 style=\"font-size: 16px; font-weight: 400; line-height: 1.5; margin: 5px 0 10px; padding-bottom: 5px;\">Small</h6>",
            renderHeading(MarkdownBlock.Heading(6, listOf(MarkdownInline.Text("Small")))),
        )
    }
}
