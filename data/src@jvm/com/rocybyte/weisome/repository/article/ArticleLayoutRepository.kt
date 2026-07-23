package com.rocybyte.weisome.repository.article

import com.rocybyte.weisome.article.ArticleLayoutMode
import com.rocybyte.weisome.storage.article.ArticleLayoutStore

internal class ArticleLayoutRepository(
    private val store: ArticleLayoutStore,
) : ArticleLayoutRepo {
    /** Loads the current layout preference from storage. */
    override suspend fun load(): ArticleLayoutMode? = store.load()

    /** Delegates layout preference persistence to storage. */
    override suspend fun save(mode: ArticleLayoutMode) {
        store.save(mode)
    }
}
