package com.rocybyte.weisome.storage.article

import com.rocybyte.weisome.article.ArticleLayoutMode

interface ArticleLayoutStore {
    /** Reads the saved article layout mode when the stored value is recognized. */
    suspend fun load(): ArticleLayoutMode?

    /** Writes the supplied article layout mode to local storage. */
    suspend fun save(mode: ArticleLayoutMode)
}
