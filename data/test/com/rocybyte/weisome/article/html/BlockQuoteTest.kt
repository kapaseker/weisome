package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.CodeThemeId
import com.rocybyte.weisome.article.CodeThemes
import com.rocybyte.weisome.article.GitHubExportStyles
import com.rocybyte.weisome.article.HydrogenExportStyles
import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownInline
import com.rocybyte.weisome.article.SmartBlueExportStyles
import com.rocybyte.weisome.article.TyporaPaperExportStyles
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** GitHub Light palette, the default code theme assumed by these renderer tests. */
private val githubLightCode = CodeThemes.forId(CodeThemeId.GITHUB_LIGHT)

class BlockQuoteTest {
    @Test
    /** Verifies blockquotes render quote marks, the hydrogen border, and quote margins. */
    fun `renders decorative quote marks and border`() {
        val html = renderBlockQuote(
            MarkdownBlock.BlockQuote(
                listOf(MarkdownBlock.Paragraph(listOf(listOf(MarkdownInline.Text("Quoted"))))),
            ),
            inQuote = false,
            styles = HydrogenExportStyles,
            codeTheme = githubLightCode,
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
            styles = HydrogenExportStyles,
            codeTheme = githubLightCode,
        )

        assertTrue(html.startsWith("<blockquote style=\"position: relative; color: #666666; padding: 5px 23px 1px; margin: 10px 0;"))
    }

    @Test
    /** Verifies the GitHub theme renders a plain left-border quote without decorative marks. */
    fun `renders github blockquote without marks`() {
        val html = renderBlockQuote(
            MarkdownBlock.BlockQuote(
                listOf(MarkdownBlock.Paragraph(listOf(listOf(MarkdownInline.Text("Quoted"))))),
            ),
            inQuote = false,
            styles = GitHubExportStyles,
            codeTheme = githubLightCode,
        )

        assertEquals(
            "<blockquote style=\"color: #59636e; padding: 0 1em; margin: 0 0 16px; border-left: 0.25em solid #d1d9e0;\">" +
                "<p style=\"font-size: 16px; line-height: 1.5; margin: 0 0 16px; color: #59636e; word-break: break-word;\">Quoted</p>" +
                "</blockquote>",
            html,
        )
    }

    @Test
    /** Verifies the smart-blue theme renders a tinted quote without decorative marks. */
    fun `renders smart blue blockquote with tinted background`() {
        assertEquals(
            "<blockquote style=\"background: #fff9f9; margin: 30px 0; padding: 2px 20px; border-left: 4px solid #b2aec5;\">" +
                "<p style=\"font-size: 15px; line-height: 2; margin: 0; color: #666666; word-break: break-word;\">Quoted</p>" +
                "</blockquote>",
            renderBlockQuote(
                MarkdownBlock.BlockQuote(
                    listOf(MarkdownBlock.Paragraph(listOf(listOf(MarkdownInline.Text("Quoted"))))),
                ),
                inQuote = false,
                styles = SmartBlueExportStyles,
                codeTheme = githubLightCode,
            ),
        )
    }

    @Test
    /** Verifies Paper quotes render as rounded cards with one oversized opening mark. */
    fun `renders typora paper quote card`() {
        val html = renderBlockQuote(
            MarkdownBlock.BlockQuote(
                listOf(MarkdownBlock.Paragraph(listOf(listOf(MarkdownInline.Text("Quoted"))))),
            ),
            inQuote = false,
            styles = TyporaPaperExportStyles,
            codeTheme = githubLightCode,
        )

        assertTrue(html.contains("border-radius: 18px"))
        assertTrue(html.contains("box-shadow: 0 10px 34px rgba(62, 48, 22, 0.10)"))
        assertEquals(1, html.count { it == '\u201c' })
        assertEquals(0, html.count { it == '\u201d' })
    }
}
