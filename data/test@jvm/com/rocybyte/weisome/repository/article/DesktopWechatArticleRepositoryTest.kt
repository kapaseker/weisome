package com.rocybyte.weisome.repository.article

import com.rocybyte.weisome.article.CodeLanguage
import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.repository.code.CodeHighlightRepository
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
}
