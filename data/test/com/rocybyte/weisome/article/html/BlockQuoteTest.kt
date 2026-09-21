package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownInline
import kotlin.test.Test
import kotlin.test.assertTrue

class BlockQuoteTest {
    @Test
    /** Verifies blockquotes render quote marks, the hydrogen border, and quote margins. */
    fun `renders decorative quote marks and border`() {
        val html = renderBlockQuote(
            MarkdownBlock.BlockQuote(
                listOf(MarkdownBlock.Paragraph(listOf(listOf(MarkdownInline.Text("Quoted"))))),
            ),
        )

        assertTrue(html.startsWith("<blockquote style=\"position: relative; color: #666666; padding: 5px 23px 1px; margin: 22px 0; border-left: 4px solid #cbcbcb; background-color: rgba(200, 200, 200, 0.12);\">"))
        assertTrue(html.contains("<span style=\"position: absolute; font-size: 24px; font-weight: 800; line-height: 24px; color: #cbcbcb; opacity: 0.6; top: 4px; left: 6px;\">\u201C</span>"))
        assertTrue(html.contains("right: 8px; bottom: -8px;\">\u201D</span>"))
        assertTrue(html.contains("<p style=\"font-size: 16px; line-height: 1.75; margin: 10px 0; color: #666666; word-break: break-word;\">Quoted</p>"))
    }

    @Test
    /** Verifies nested blockquotes switch to the tighter nested margin. */
    fun `renders nested blockquotes with tighter margins`() {
        val html = renderBlockQuote(
            MarkdownBlock.BlockQuote(
                listOf(
                    MarkdownBlock.BlockQuote(
                        listOf(MarkdownBlock.Paragraph(listOf(listOf(MarkdownInline.Text("Deep"))))),
                    ),
                ),
            ),
            inQuote = true,
        )

        assertTrue(html.startsWith("<blockquote style=\"position: relative; color: #666666; padding: 5px 23px 1px; margin: 10px 0;"))
    }
}
