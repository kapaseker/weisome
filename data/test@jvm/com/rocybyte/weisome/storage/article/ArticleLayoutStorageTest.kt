package com.rocybyte.weisome.storage.article

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesSerializer
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.rocybyte.weisome.article.ArticleLayoutMode
import java.io.File
import java.nio.file.Files
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import okio.FileSystem
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ArticleLayoutStorageTest {
    @Test
    /** Verifies that every supported layout mode round trips through Preferences DataStore. */
    fun `layout modes can be saved and loaded`() = withPreferences { _, storage ->
        ArticleLayoutMode.entries.forEach { expected ->
            storage.save(expected)
            assertEquals(expected, storage.load())
        }
    }

    @Test
    /** Verifies that absent and unrecognized persisted values are treated as no preference. */
    fun `missing and unknown layout modes return null`() = withPreferences { dataStore, storage ->
        assertNull(storage.load())

        dataStore.edit { preferences ->
            preferences[stringPreferencesKey("article_layout_mode")] = "UNKNOWN_LAYOUT"
        }

        assertNull(storage.load())
    }

    /** Runs a storage assertion against an isolated temporary Preferences DataStore. */
    private fun withPreferences(
        test: suspend (DataStore<Preferences>, ArticleLayoutStorage) -> Unit,
    ) = runBlocking {
        val directory = Files.createTempDirectory("weisome-article-layout-test").toFile()
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val dataStore = createDataStore(directory, scope)

        try {
            test(dataStore, ArticleLayoutStorage(dataStore))
        } finally {
            scope.cancel()
            directory.deleteRecursively()
        }
    }

    /** Creates the file-backed Preferences DataStore used by a storage test. */
    private fun createDataStore(
        directory: File,
        scope: CoroutineScope,
    ): DataStore<Preferences> = DataStoreFactory.create(
        storage = OkioStorage(
            fileSystem = FileSystem.SYSTEM,
            serializer = PreferencesSerializer,
            producePath = {
                directory.resolve("test.preferences_pb").absolutePath.toPath()
            },
        ),
        scope = scope,
    )
}
