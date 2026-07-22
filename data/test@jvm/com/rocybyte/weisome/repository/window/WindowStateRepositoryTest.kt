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
    fun `valid state is delegated to storage`() = runBlocking {
        val store = RecordingWindowStateStore()
        val repository = WindowStateRepository(store)
        val expected = savedWindow()

        repository.save(expected)

        assertEquals(expected, store.savedState)
    }

    @Test
    fun `invalid stored state is ignored`() = runBlocking {
        val store = RecordingWindowStateStore(loadedState = savedWindow(width = 0))
        val repository = WindowStateRepository(store)

        assertNull(repository.load())
    }

    @Test
    fun `invalid state is rejected before storage`() = runBlocking {
        val store = RecordingWindowStateStore()
        val repository = WindowStateRepository(store)

        assertFailsWith<IllegalArgumentException> {
            repository.save(savedWindow(height = 0))
        }
        assertNull(store.savedState)
    }

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

        override suspend fun load(): SavedWindowState? = loadedState

        override suspend fun save(state: SavedWindowState) {
            savedState = state
        }
    }
}
