package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownInline
import kotlin.test.Test
import kotlin.test.assertEquals

class ParagraphTest {
    @Test
    /** Verifies paragraphs preserve authored lines and capitalize the first letter. */
    fun `renders paragraph lines with breaks`() {
        assertEquals(
            "<p style=\"font-size: 16px; line-height: 1.75; margin: 22px 0; color: rgba(46, 36, 36, 0.87); word-break: break-word;\">First<br/>Second</p>",
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

    @Test
    /** Verifies quote-context paragraphs use the tighter blockquote margins. */
    fun `renders quote paragraphs with tighter margins`() {
        assertEquals(
            "<p style=\"font-size: 16px; line-height: 1.75; margin: 10px 0; color: #666666; word-break: break-word;\">Quoted</p>",
            renderParagraph(
                MarkdownBlock.Paragraph(listOf(listOf(MarkdownInline.Text("Quoted")))),
                inQuote = true,
            ),
        )
    }
}
