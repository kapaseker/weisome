package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownInline
import kotlin.test.Test
import kotlin.test.assertEquals

class ListBlockTest {
    @Test
    /** Verifies unordered lists use unordered markup and preserve item styling. */
    fun `renders unordered lists with unordered markup`() {
        assertEquals(
            "<ul style=\"padding-left: 24px; margin: 0 0 16px;\"><li style=\"font-size: 16px; line-height: 1.75;\">One</li></ul>",
            renderListBlock(listBlock(ordered = false, "One")),
        )
    }

    @Test
    /** Verifies ordered lists use ordered markup while preserving item styling. */
    fun `renders ordered lists with ordered markup`() {
        assertEquals(
            "<ol style=\"padding-left: 24px; margin: 0 0 16px;\"><li style=\"font-size: 16px; line-height: 1.75;\">First</li><li style=\"font-size: 16px; line-height: 1.75;\">Second</li></ol>",
            renderListBlock(listBlock(ordered = true, "First", "Second")),
        )
    }

    /** Builds a list block containing plain-text items for renderer tests. */
    private fun listBlock(ordered: Boolean, vararg items: String): MarkdownBlock.ListBlock =
        MarkdownBlock.ListBlock(
            ordered = ordered,
            items = items.map { listOf(MarkdownInline.Text(it)) },
        )
}
