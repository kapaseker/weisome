package com.rocybyte.weisome.storage.window

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesSerializer
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.rocybyte.weisome.window.SavedWindowState
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

class WindowStateStorageTest {
    @Test
    /** Verifies complete state round trips and incomplete records are rejected. */
    fun `window state can be saved and loaded`() = withPreferences { dataStore, storage ->
        val expected = SavedWindowState(
            x = -1280,
            y = 40,
            width = 1200,
            height = 800,
            isMaximized = true,
        )

        storage.save(expected)

        assertEquals(expected, storage.load())
        dataStore.edit { preferences -> preferences.remove(intPreferencesKey("window_height")) }
        assertNull(storage.load())
    }

    /** Runs a storage assertion against an isolated temporary Preferences DataStore. */
    private fun withPreferences(
        test: suspend (DataStore<Preferences>, WindowStateStorage) -> Unit,
    ) = runBlocking {
        val directory = Files.createTempDirectory("weisome-window-settings-test").toFile()
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val dataStore = createDataStore(directory, scope)

        try {
            test(dataStore, WindowStateStorage(dataStore))
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
