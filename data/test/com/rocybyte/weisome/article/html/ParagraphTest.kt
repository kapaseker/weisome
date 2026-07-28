package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownInline
import kotlin.test.Test
import kotlin.test.assertEquals

class ParagraphTest {
    @Test
    /** Verifies paragraphs preserve authored lines with HTML breaks. */
    fun `renders paragraph lines with breaks`() {
        assertEquals(
            "<p style=\"font-size: 16px; line-height: 1.75; margin: 0 0 16px;\">First<br/>Second</p>",
            renderParagraph(
                MarkdownBlock.Paragraph(
                    listOf(
                        listOf(MarkdownInline.Text("First")),
                        listOf(MarkdownInline.Text("Second")),
                    ),
                ),
            ),
        )
    }
}
