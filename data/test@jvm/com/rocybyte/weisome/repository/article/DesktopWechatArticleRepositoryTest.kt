package com.rocybyte.weisome.repository.article

import com.rocybyte.weisome.article.CodeLanguage
import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.repository.code.CodeHighlightRepository
import java.awt.datatransfer.DataFlavor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DesktopWechatArticleRepositoryTest {
    @Test
    /** Verifies preview documents carry highlighter output for supported fenced code. */
    fun `preview enriches supported code blocks with shared highlights`() {
        val repository = DesktopWechatArticleRepository(CodeHighlightRepository())

        val codeBlock = repository.preview("```kotlin\nfun main() = Unit\n```")
            .blocks
            .single() as MarkdownBlock.CodeBlock

        assertEquals(CodeLanguage.Kotlin, codeBlock.language)
        assertTrue(codeBlock.highlights.isNotEmpty())
    }

    @Test
    /** Verifies unknown fenced languages remain unhighlighted plain code. */
    fun `preview leaves unknown code languages unhighlighted`() {
        val repository = DesktopWechatArticleRepository(CodeHighlightRepository())

        val codeBlock = repository.preview("```python\nprint('hello')\n```")
            .blocks
            .single() as MarkdownBlock.CodeBlock

        assertEquals(null, codeBlock.language)
        assertTrue(codeBlock.highlights.isEmpty())
    }

    @Test
    /** Verifies Juejin receives the original Markdown through plain-text clipboard flavors. */
    fun `Juejin transferable exposes Markdown as plain text`() {
        val markdown = "# Article\n\n**Body**"

        val transferable = createJuejinTransferable(markdown)

        assertTrue(transferable.isDataFlavorSupported(DataFlavor.stringFlavor))
        assertEquals(markdown, transferable.getTransferData(DataFlavor.stringFlavor))
        assertTrue(transferable.transferDataFlavors.none { it.primaryType == "text" && it.subType == "html" })
    }
}
