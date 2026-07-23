package com.rocybyte.weisome.repository.article

import com.rocybyte.weisome.article.ArticleLayoutMode

interface ArticleLayoutRepo {
    /** Loads the saved article layout mode, or `null` when no valid preference exists. */
    suspend fun load(): ArticleLayoutMode?

    /** Persists the supplied article layout mode. */
    suspend fun save(mode: ArticleLayoutMode)
}
