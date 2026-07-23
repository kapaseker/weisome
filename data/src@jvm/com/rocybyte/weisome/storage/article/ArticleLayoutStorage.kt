package com.rocybyte.weisome.storage.article

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.rocybyte.weisome.article.ArticleLayoutMode
import kotlinx.coroutines.flow.first

internal class ArticleLayoutStorage(
    private val dataStore: DataStore<Preferences>,
) : ArticleLayoutStore {
    /** Loads a recognized layout mode and ignores absent or unknown persisted values. */
    override suspend fun load(): ArticleLayoutMode? {
        val savedValue = dataStore.data.first()[ArticleLayoutModeKey] ?: return null
        return ArticleLayoutMode.entries.firstOrNull { mode -> mode.name == savedValue }
    }

    /** Stores the stable enum name of the supplied layout mode. */
    override suspend fun save(mode: ArticleLayoutMode) {
        dataStore.edit { preferences ->
            preferences[ArticleLayoutModeKey] = mode.name
        }
    }

    private companion object {
        val ArticleLayoutModeKey = stringPreferencesKey("article_layout_mode")
    }
}
