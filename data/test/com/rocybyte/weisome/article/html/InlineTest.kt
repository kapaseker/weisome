package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.MarkdownInline
import kotlin.test.Test
import kotlin.test.assertEquals

class InlineTest {
    @Test
    /** Verifies inline code exports as a rounded literal label with shared colors. */
    fun `renders styled literal inline code`() {
        assertEquals(
            "Call <code style=\"color: #24292f; background: #f6f8fa; padding: 2px 4px; border-radius: 4px; font-family: inherit; font-size: inherit; font-weight: 400; font-style: normal; box-decoration-break: clone; -webkit-box-decoration-break: clone; overflow-wrap: anywhere;\">launch(&lt;tag&gt;)</code> now",
            renderInline(
                listOf(
                    MarkdownInline.Text("Call "),
                    MarkdownInline.Code("launch(<tag>)"),
                    MarkdownInline.Text(" now"),
                ),
            ),
        )
    }

    @Test
    /** Verifies inline emphasis and unsupported HTML characters retain their semantics safely. */
    fun `renders emphasis and escapes unsupported html`() {
        assertEquals(
            "&lt;script&gt;<strong>bold</strong><em>italic</em>&quot;x&quot; &#39;y&#39; &amp;",
            renderInline(
                listOf(
                    MarkdownInline.Text("<script>"),
                    MarkdownInline.Bold("bold"),
                    MarkdownInline.Italic("italic"),
                    MarkdownInline.Text("\"x\" 'y' &"),
                ),
            ),
        )
    }
}
