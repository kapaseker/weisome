package com.rocybyte.weisome.page.article.biz

import com.rocybyte.weisome.article.Article
import com.rocybyte.weisome.repository.article.ArticleRepo
import java.util.concurrent.CopyOnWriteArrayList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.yield
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ArticleHomeViewModelTest {
    @Test
    /** Verifies the home state exposes the repository list once loading completes. */
    fun `loads articles from the repository`() = runBlocking {
        val repository = FakeArticleRepo(
            initial = listOf(article(id = "a2", updatedAt = 200), article(id = "a1", updatedAt = 100)),
        )
        val viewModel = ArticleHomeViewModel(repository)

        withTimeout(1_000) { viewModel.uiState.first { it.isLoaded } }

        assertEquals(listOf("a2", "a1"), viewModel.uiState.value.articles.map { it.id })
    }

    @Test
    /** Verifies a failed list load still marks the home state as ready with an empty list. */
    fun `load failure degrades to an empty list`() = runBlocking {
        val viewModel = ArticleHomeViewModel(FakeArticleRepo(listFailure = true))

        withTimeout(1_000) { viewModel.uiState.first { it.isLoaded } }

        assertTrue(viewModel.uiState.value.articles.isEmpty())
    }

    @Test
    /** Verifies create persists a new article and refreshes the visible list. */
    fun `create adds the new article to the list`() = runBlocking {
        val repository = FakeArticleRepo()
        val viewModel = ArticleHomeViewModel(repository)
        withTimeout(1_000) { viewModel.uiState.first { it.isLoaded } }

        val created = viewModel.create("新文章")

        assertEquals("新文章", created.title)
        assertEquals(listOf(created.id), viewModel.uiState.value.articles.map { it.id })
        assertEquals(created, repository.stored[created.id])
    }

    @Test
    /** Verifies delete removes the article from the repository and the refreshed list. */
    fun `delete removes the article from the list`() = runBlocking {
        val existing = article(id = "a1")
        val repository = FakeArticleRepo(initial = listOf(existing))
        val viewModel = ArticleHomeViewModel(repository)
        withTimeout(1_000) { viewModel.uiState.first { it.isLoaded } }

        viewModel.delete("a1")
        withTimeout(1_000) {
            while (!viewModel.uiState.value.articles.isEmpty()) yield()
        }

        assertTrue(viewModel.uiState.value.articles.isEmpty())
        assertEquals(null, repository.stored["a1"])
    }

    /** Builds a minimal article fixture with sensible defaults. */
    private fun article(
        id: String,
        title: String = "t",
        markdown: String = "",
        createdAt: Long = 0,
        updatedAt: Long = 0,
    ) = Article(id = id, title = title, markdown = markdown, createdAt = createdAt, updatedAt = updatedAt)
}

private class FakeArticleRepo(
    initial: List<Article> = emptyList(),
    private val listFailure: Boolean = false,
) : ArticleRepo {
    val stored = initial.associateBy { it.id }.toMutableMap()
    val deleted = CopyOnWriteArrayList<String>()

    /** Returns stored articles in insertion order unless configured to fail. */
    override suspend fun list(): List<Article> {
        if (listFailure) error("list failed")
        return stored.values.toList()
    }

    /** Returns the stored article or null when missing. */
    override suspend fun load(id: String): Article? = stored[id]

    /** Creates an article with a generated id and current timestamps. */
    override suspend fun create(title: String): Article {
        val now = System.currentTimeMillis()
        val article = Article(
            id = "generated-${stored.size + 1}",
            title = title,
            markdown = "",
            createdAt = now,
            updatedAt = now,
        )
        stored[article.id] = article
        return article
    }

    /** Inserts or overwrites the article. */
    override suspend fun save(article: Article) {
        stored[article.id] = article
    }

    /** Records the deletion. */
    override suspend fun delete(id: String) {
        stored.remove(id)
        deleted += id
    }
}
