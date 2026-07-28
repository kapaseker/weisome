package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownInline
import kotlin.test.Test
import kotlin.test.assertEquals

class HeadingTest {
    @Test
    /** Verifies a level-one heading receives the expected inline style. */
    fun `renders a level one heading with inline style`() {
        assertEquals(
            "<h1 style=\"font-size: 24px; font-weight: 700; line-height: 1.4; margin: 24px 0 16px;\">Hello</h1>",
            renderHeading(MarkdownBlock.Heading(1, listOf(MarkdownInline.Text("Hello")))),
        )
    }
}
