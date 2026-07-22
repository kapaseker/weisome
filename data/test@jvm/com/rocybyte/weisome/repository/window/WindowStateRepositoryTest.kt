package com.rocybyte.weisome.repository.window

import com.rocybyte.weisome.storage.window.WindowStateStore
import com.rocybyte.weisome.window.SavedWindowState
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class WindowStateRepositoryTest {
    @Test
    /** Verifies that valid state reaches the configured storage interface unchanged. */
    fun `valid state is delegated to storage`() = runBlocking {
        val store = RecordingWindowStateStore()
        val repository = WindowStateRepository(store)
        val expected = savedWindow()

        repository.save(expected)

        assertEquals(expected, store.savedState)
    }

    @Test
    /** Verifies that invalid dimensions loaded from storage are not exposed to callers. */
    fun `invalid stored state is ignored`() = runBlocking {
        val store = RecordingWindowStateStore(loadedState = savedWindow(width = 0))
        val repository = WindowStateRepository(store)

        assertNull(repository.load())
    }

    @Test
    /** Verifies that invalid state is rejected before a storage write occurs. */
    fun `invalid state is rejected before storage`() = runBlocking {
        val store = RecordingWindowStateStore()
        val repository = WindowStateRepository(store)

        assertFailsWith<IllegalArgumentException> {
            repository.save(savedWindow(height = 0))
        }
        assertNull(store.savedState)
    }

    /** Creates a window-state fixture with configurable dimensions. */
    private fun savedWindow(
        width: Int = 1280,
        height: Int = 720,
    ) = SavedWindowState(
        x = 100,
        y = 100,
        width = width,
        height = height,
        isMaximized = false,
    )

    private class RecordingWindowStateStore(
        private val loadedState: SavedWindowState? = null,
    ) : WindowStateStore {
        var savedState: SavedWindowState? = null

        /** Returns the state configured for the current repository test. */
        override suspend fun load(): SavedWindowState? = loadedState

        /** Records the state delegated by the repository. */
        override suspend fun save(state: SavedWindowState) {
            savedState = state
        }
    }
}
