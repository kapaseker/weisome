package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.ListItem
import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownInline
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ListBlockTest {
    @Test
    /** Verifies unordered lists use unordered markup and preserve item styling. */
    fun `renders unordered lists with unordered markup`() {
        assertEquals(
            "<ul style=\"padding-left: 28px; margin: 16px 0;\">" +
                "<li style=\"font-size: 16px; line-height: 1.75; margin-bottom: 0; color: rgba(46, 36, 36, 0.87);\">One</li></ul>",
            renderListBlock(listBlock(ordered = false, "One")),
        )
    }

    @Test
    /** Verifies ordered lists add the ordered-list left padding from hydrogen. */
    fun `renders ordered lists with ordered markup`() {
        assertEquals(
            "<ol style=\"padding-left: 28px; margin: 16px 0;\">" +
                "<li style=\"font-size: 16px; line-height: 1.75; margin-bottom: 0; color: rgba(46, 36, 36, 0.87); padding-left: 6px;\">First</li>" +
                "<li style=\"font-size: 16px; line-height: 1.75; margin-bottom: 0; color: rgba(46, 36, 36, 0.87); padding-left: 6px;\">Second</li></ol>",
            renderListBlock(listBlock(ordered = true, "First", "Second")),
        )
    }

    @Test
    /** Verifies task items drop the list marker and render checkbox glyphs. */
    fun `renders task items without list markers`() {
        val block = MarkdownBlock.ListBlock(
            ordered = false,
            items = listOf(
                ListItem(listOf(MarkdownInline.Text("todo")), task = false),
                ListItem(listOf(MarkdownInline.Text("done")), task = true),
            ),
        )

        val html = renderListBlock(block)

        assertTrue(html.contains("<li style=\"list-style: none; "))
        assertTrue(html.contains(">\u2610 todo</li>"))
        assertTrue(html.contains(">\u2611 done</li>"))
    }

    @Test
    /** Verifies nested lists render inside their parent item with the tighter margin. */
    fun `renders nested lists inside parent items`() {
        val block = MarkdownBlock.ListBlock(
            ordered = false,
            items = listOf(
                ListItem(
                    content = listOf(MarkdownInline.Text("Parent")),
                    child = MarkdownBlock.ListBlock(
                        ordered = false,
                        items = listOf(ListItem(listOf(MarkdownInline.Text("Child")))),
                    ),
                ),
            ),
        )

        val html = renderListBlock(block)

        assertTrue(html.contains("Parent<ul style=\"padding-left: 28px; margin: 3px 0 0;\">"))
        assertTrue(html.contains("<li style=\"font-size: 16px; line-height: 1.75; margin-bottom: 0; color: rgba(46, 36, 36, 0.87);\">Child</li></ul></li>"))
    }

    /** Builds a list block containing plain-text items for renderer tests. */
    private fun listBlock(ordered: Boolean, vararg items: String): MarkdownBlock.ListBlock =
        MarkdownBlock.ListBlock(
            ordered = ordered,
            items = items.map { ListItem(listOf(MarkdownInline.Text(it))) },
        )
}
