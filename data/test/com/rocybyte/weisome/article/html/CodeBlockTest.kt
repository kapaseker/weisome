package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.CodeHighlightSpan
import com.rocybyte.weisome.article.CodeLanguage
import com.rocybyte.weisome.article.MarkdownBlock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CodeBlockTest {
    @Test
    /** Verifies fenced code is escaped inside the scrollable pre and code hierarchy. */
    fun `renders escaped fenced code in the scrollable structure`() {
        val html = renderCodeBlock(codeBlock("val tag = \"<code>\""))

        assertTrue(html.startsWith("<pre style=\"background: #f6f8fa;"))
        assertTrue(html.contains("<code style=\"display: -webkit-box; min-width: 100%; box-sizing: border-box; overflow-x: auto;\">val tag = &quot;&lt;code&gt;&quot;</code>"))
        assertTrue(html.endsWith("</code></pre>"))
    }

    @Test
    /** Verifies exported fenced code preserves authored lines and scrolls instead of wrapping. */
    fun `exports fenced code without automatic wrapping`() {
        val html = renderCodeBlock(codeBlock("val longValue = someVeryLongExpression()"))
        val codeStyle = html.substringAfter("<code style=\"").substringBefore("\"")
        val preStyle = html.substringAfter("<pre style=\"").substringBefore("\"")

        assertTrue(preStyle.contains("white-space: pre;"))
        assertTrue(codeStyle.contains("overflow-x: auto;"))
        assertTrue(codeStyle.contains("display: -webkit-box;"))
        assertTrue(preStyle.contains("word-break: normal;"))
        assertFalse(html.contains("white-space: pre-wrap;"))
        assertFalse(html.contains("word-break: break-word;"))
        assertFalse(preStyle.contains("overflow-x"))
    }

    @Test
    /** Verifies exported code uses the exact colors supplied by the shared highlight model. */
    fun `renders shared code highlights as inline html colors`() {
        val html = renderCodeBlock(
            MarkdownBlock.CodeBlock(
                language = CodeLanguage.Kotlin,
                code = "fun main()",
                highlights = listOf(CodeHighlightSpan(0, 3, 0xCF222E)),
            ),
        )

        assertEquals(true, html.contains("<span style=\"color: #cf222e;\">fun</span> main()"))
    }

    /** Builds an unhighlighted Kotlin code block for renderer tests. */
    private fun codeBlock(code: String): MarkdownBlock.CodeBlock = MarkdownBlock.CodeBlock(
        language = CodeLanguage.Kotlin,
        code = code,
    )
}
