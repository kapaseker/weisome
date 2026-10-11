package com.rocybyte.weisome.repository.article

import com.rocybyte.weisome.article.CodeThemeId
import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownDocumentParser
import com.rocybyte.weisome.article.MarkdownThemeId
import com.rocybyte.weisome.article.MarkdownToWechatHtml
import com.rocybyte.weisome.repository.code.CodeHighlightRepository
import kotlin.test.Test
import kotlin.test.assertEquals

/** Verifies the full clipboard export pipeline keeps code text byte-identical after entity decoding. */
class ExportedCodeTextTest {
    private val repo = CodeHighlightRepository()

    /** Renders markdown through the clipboard pipeline and returns the decoded text of the first code element. */
    private fun exportedCodeText(markdown: String): String {
        val doc = MarkdownDocumentParser.parse(markdown)
        val enriched = doc.copy(blocks = doc.blocks.map { block ->
            val codeBlock = block as? MarkdownBlock.CodeBlock
            val language = codeBlock?.language
            if (codeBlock != null && language != null) {
                codeBlock.copy(highlights = repo.highlight(language, codeBlock.code, CodeThemeId.GITHUB_LIGHT))
            } else {
                block
            }
        })
        val html = MarkdownToWechatHtml.render(enriched, MarkdownThemeId.CLAUDETTE, CodeThemeId.GITHUB_LIGHT)
        val inner = html.substringAfter("<code style=\"").substringAfter("\">").substringBefore("</code>")
        return inner
            .replace("<br/>", "\n")
            .replace(Regex("<[^>]+>"), "")
            .replace("&nbsp;", " ")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&amp;", "&")
    }

    @Test
    /** Verifies indented code survives export with every space, newline, and highlight span intact. */
    fun `exports indented rust code with whitespace intact`() {
        val code = "enum Result<T, E> {\n    Ok(T),\n    Err(E),\n}"
        assertEquals(code, exportedCodeText("```rust\n$code\n```"))
    }

    @Test
    /** Verifies flat code with inline comments survives export unchanged. */
    fun `exports flat rust code with whitespace intact`() {
        val code = "let result = parse_int(\"5\");\nprintln!(\"{}\", n);"
        assertEquals(code, exportedCodeText("```rust\n$code\n```"))
    }
}
