package com.rocybyte.weisome.storage.settings

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesSerializer
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

class DisplaySettingsStorageTest {
    @Test
    /** Verifies text and UI overrides can be saved, loaded, and cleared independently. */
    fun `display settings round trip independently`() = withPreferences { storage ->
        storage.saveUserTextScale(1.4f)
        storage.saveUserUiScale(1.8f)

        assertEquals(1.4f, storage.load().userTextScale)
        assertEquals(1.8f, storage.load().userUiScale)

        storage.saveUserTextScale(null)
        assertNull(storage.load().userTextScale)
        assertEquals(1.8f, storage.load().userUiScale)

        storage.saveUserUiScale(null)
        assertNull(storage.load().userUiScale)
    }

    /** Runs a storage assertion against an isolated temporary Preferences DataStore. */
    private fun withPreferences(
        test: suspend (DisplaySettingsStorage) -> Unit,
    ) = runBlocking {
        val directory = Files.createTempDirectory("weisome-display-settings-test").toFile()
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val dataStore = createDataStore(directory, scope)

        try {
            test(DisplaySettingsStorage(dataStore))
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
            producePath = { directory.resolve("test.preferences_pb").absolutePath.toPath() },
        ),
        scope = scope,
    )
}
