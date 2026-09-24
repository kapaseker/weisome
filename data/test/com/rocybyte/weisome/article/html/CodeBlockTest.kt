package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.CodeHighlightSpan
import com.rocybyte.weisome.article.CodeLanguage
import com.rocybyte.weisome.article.CodeThemeId
import com.rocybyte.weisome.article.CodeThemes
import com.rocybyte.weisome.article.HydrogenExportStyles
import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.TyporaPaperExportStyles
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** GitHub Light palette, the default code theme assumed by these renderer tests. */
private val githubLightCode = CodeThemes.forId(CodeThemeId.GITHUB_LIGHT)

class CodeBlockTest {
    @Test
    /** Verifies fenced code is escaped inside the scrollable pre and code hierarchy. */
    fun `renders escaped fenced code in the scrollable structure`() {
        val html = renderCodeBlock(codeBlock("val tag = \"<code>\""), HydrogenExportStyles, githubLightCode)

        assertTrue(html.startsWith("<pre style=\"font-family: Menlo, Monaco, Consolas, 'Courier New', monospace; line-height: 1.75;"))
        assertTrue(
            html.contains(
                "<code style=\"display: -webkit-box; min-width: 100%; box-sizing: border-box; overflow-x: auto; " +
                    "font-weight: 400; font-size: 12px; padding: 15px 12px; margin: 0; word-break: normal; " +
                    "white-space: pre; color: #1f2328; background: #f6f8fa; border-radius: 0 4px;\">val tag = &quot;&lt;code&gt;&quot;</code>",
            ),
        )
        assertTrue(html.endsWith("</code></pre>"))
    }

    @Test
    /** Verifies exported fenced code preserves authored lines and scrolls instead of wrapping. */
    fun `exports fenced code without automatic wrapping`() {
        val html = renderCodeBlock(codeBlock("val longValue = someVeryLongExpression()"), HydrogenExportStyles, githubLightCode)
        val codeStyle = html.substringAfter("<code style=\"").substringBefore("\"")
        val preStyle = html.substringAfter("<pre style=\"").substringBefore("\"")

        assertTrue(preStyle.contains("white-space: pre;"))
        assertTrue(codeStyle.contains("overflow-x: auto;"))
        assertTrue(codeStyle.contains("display: -webkit-box;"))
        assertTrue(codeStyle.contains("word-break: normal;"))
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
            HydrogenExportStyles,
            githubLightCode,
        )

        assertEquals(true, html.contains("<span style=\"color: #cf222e;\">fun</span> main()"))
    }

    @Test
    /** Verifies the base text color follows the active code theme, not the Markdown theme. */
    fun `colors unhighlighted code with the active code theme base color`() {
        val matrixCode = CodeThemes.forId(CodeThemeId.MATRIX)

        val html = renderCodeBlock(codeBlock("class Worker"), HydrogenExportStyles, matrixCode)

        assertTrue(html.contains("color: #008500;"))
        assertFalse(html.contains("color: #1f2328;"))
    }

    @Test
    /** Verifies the code background comes from the active code theme palette, not a hardcoded value. */
    fun `colors the code background from the active code theme palette`() {
        val darkCode = githubLightCode.copy(backgroundRgb = 0x282C34)

        val html = renderCodeBlock(codeBlock("class Worker"), HydrogenExportStyles, darkCode)

        assertTrue(html.contains("background: #282c34;"))
    }

    @Test
    /** Verifies One Dark Pro exports its own dark background with the matching light foreground. */
    fun `exports one dark pro on its dark background`() {
        val html = renderCodeBlock(
            codeBlock("class Worker"),
            HydrogenExportStyles,
            CodeThemes.forId(CodeThemeId.ONE_DARK_PRO),
        )

        assertTrue(html.contains("background: #282c34;"))
        assertTrue(html.contains("color: #abb2bf;"))
    }

    @Test
    /** Verifies Paper code blocks include the three-dot window header without replacing the selected code palette. */
    fun `renders typora paper code window`() {
        val html = renderCodeBlock(
            codeBlock("val answer = 42"),
            TyporaPaperExportStyles,
            CodeThemes.forId(CodeThemeId.MATRIX),
        )

        assertTrue(html.contains("background: #ff5f57"))
        assertTrue(html.contains("background: #febc2e"))
        assertTrue(html.contains("background: #28c840"))
        assertTrue(html.contains("color: #008500;"))
        assertTrue(html.contains("background: #000000;"))
    }

    /** Builds an unhighlighted Kotlin code block for renderer tests. */
    private fun codeBlock(code: String): MarkdownBlock.CodeBlock = MarkdownBlock.CodeBlock(
        language = CodeLanguage.Kotlin,
        code = code,
    )
}
