package com.rocybyte.weisome.repository.article

import com.rocybyte.weisome.article.ArticleLayoutMode
import com.rocybyte.weisome.storage.article.ArticleLayoutStore
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class ArticleLayoutRepositoryTest {
    @Test
    /** Verifies that loading delegates to the configured layout storage. */
    fun `saved layout is loaded from storage`() = runBlocking {
        val store = RecordingArticleLayoutStore(loadedMode = ArticleLayoutMode.PREVIEW_ONLY)
        val repository = ArticleLayoutRepository(store)

        assertEquals(ArticleLayoutMode.PREVIEW_ONLY, repository.load())
    }

    @Test
    /** Verifies that saving delegates the selected layout to storage unchanged. */
    fun `selected layout is saved to storage`() = runBlocking {
        val store = RecordingArticleLayoutStore()
        val repository = ArticleLayoutRepository(store)

        repository.save(ArticleLayoutMode.EDITOR_ONLY)

        assertEquals(ArticleLayoutMode.EDITOR_ONLY, store.savedMode)
    }

    private class RecordingArticleLayoutStore(
        private val loadedMode: ArticleLayoutMode? = null,
    ) : ArticleLayoutStore {
        var savedMode: ArticleLayoutMode? = null

        /** Returns the layout configured for the current repository test. */
        override suspend fun load(): ArticleLayoutMode? = loadedMode

        /** Records the layout delegated by the repository. */
        override suspend fun save(mode: ArticleLayoutMode) {
            savedMode = mode
        }
    }
}
