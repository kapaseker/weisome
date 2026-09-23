package com.rocybyte.weisome.repository.article

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.rocybyte.weisome.article.CodeLanguage
import com.rocybyte.weisome.article.CodeThemeId
import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownThemeId
import com.rocybyte.weisome.repository.code.CodeHighlightRepository
import com.rocybyte.weisome.storage.article.ArticleEntity
import com.rocybyte.weisome.storage.article.WeisomeDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking

class DesktopWechatArticleRepositoryTest {
    @Test
    /** Verifies preview documents carry highlighter output for supported fenced code. */
    fun `preview enriches supported code blocks with shared highlights`() {
        val repository = DesktopWechatArticleRepository(CodeHighlightRepository())

        val codeBlock = repository.preview("```kotlin\nfun main() = Unit\n```", CodeThemeId.GITHUB_LIGHT)
            .blocks
            .single() as MarkdownBlock.CodeBlock

        assertEquals(CodeLanguage.Kotlin, codeBlock.language)
        assertTrue(codeBlock.highlights.isNotEmpty())
    }

    @Test
    /** Verifies unknown fenced languages remain unhighlighted plain code. */
    fun `preview leaves unknown code languages unhighlighted`() {
        val repository = DesktopWechatArticleRepository(CodeHighlightRepository())

        val codeBlock = repository.preview("```python\nprint('hello')\n```", CodeThemeId.DARCULA)
            .blocks
            .single() as MarkdownBlock.CodeBlock

        assertEquals(null, codeBlock.language)
        assertTrue(codeBlock.highlights.isEmpty())
    }

    @Test
    /** Verifies loading a row with an unknown stored markdown theme falls back to GITHUB. */
    fun `load falls back to github for unknown markdown theme`() = runBlocking {
        val dao = Room.inMemoryDatabaseBuilder<WeisomeDatabase>()
            .setDriver(BundledSQLiteDriver())
            .build()
            .articleDao()
        dao.save(
            ArticleEntity(
                id = "a1",
                title = "t",
                markdown = "",
                createdAt = 0,
                updatedAt = 0,
                codeTheme = "GITHUB_LIGHT",
                markdownTheme = "OBSOLETE",
            ),
        )

        assertEquals(MarkdownThemeId.GITHUB, ArticleRepository(dao).load("a1")?.markdownTheme)
    }
}
