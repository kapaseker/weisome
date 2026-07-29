package com.rocybyte.weisome.repository.settings

import com.rocybyte.weisome.settings.DisplaySettings
import com.rocybyte.weisome.storage.settings.DisplaySettingsStore
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DisplaySettingsRepositoryTest {
    @Test
    /** Verifies loaded values are normalized and invalid overrides are discarded. */
    fun `load normalizes persisted scales`() = runBlocking {
        val repository = DisplaySettingsRepository(
            RecordingDisplaySettingsStore(
                DisplaySettings(userTextScale = 1.36f, userUiScale = Float.NaN),
            ),
        )

        val settings = repository.load()

        assertEquals(1.4f, settings.userTextScale)
        assertNull(settings.userUiScale)
    }

    @Test
    /** Verifies saved values are normalized before reaching storage. */
    fun `save normalizes scales before storage`() = runBlocking {
        val store = RecordingDisplaySettingsStore()
        val repository = DisplaySettingsRepository(store)

        repository.saveUserTextScale(8f)
        repository.saveUserUiScale(0.74f)

        assertEquals(4f, store.savedTextScale)
        assertEquals(0.7f, store.savedUiScale)
    }
}

private class RecordingDisplaySettingsStore(
    private val settings: DisplaySettings = DisplaySettings(),
) : DisplaySettingsStore {
    var savedTextScale: Float? = null
    var savedUiScale: Float? = null

    /** Returns the configured raw storage values. */
    override suspend fun load(): DisplaySettings = settings

    /** Records the supplied text-scale value. */
    override suspend fun saveUserTextScale(scale: Float?) {
        savedTextScale = scale
    }

    /** Records the supplied UI-scale value. */
    override suspend fun saveUserUiScale(scale: Float?) {
        savedUiScale = scale
    }
}
