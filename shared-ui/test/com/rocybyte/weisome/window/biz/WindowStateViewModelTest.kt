package com.rocybyte.weisome.window.biz

import com.rocybyte.weisome.repository.window.WindowStateRepo
import com.rocybyte.weisome.window.SavedWindowState
import java.util.concurrent.CopyOnWriteArrayList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class WindowStateViewModelTest {
    @Test
    /** Verifies restored state is published before the desktop window becomes ready. */
    fun `loads restored state before marking the window ready`() = runBlocking {
        val restored = bounds(x = 40, y = 80)
        val viewModel = loadedViewModel(RecordingWindowStateRepo(restoredState = restored))

        assertEquals(WindowUiState(isLoaded = true, restoredState = restored), viewModel.uiState.value)
    }

    @Test
    /** Verifies a repository read failure degrades to a ready default window. */
    fun `load failure falls back to a ready default window`() = runBlocking {
        val viewModel = loadedViewModel(RecordingWindowStateRepo(loadFailure = true))

        assertTrue(viewModel.uiState.value.isLoaded)
        assertNull(viewModel.uiState.value.restoredState)
    }

    @Test
    /** Verifies the first stable observation is treated as a baseline rather than a change. */
    fun `initial floating observation establishes a baseline without saving`() = runBlocking {
        val repository = RecordingWindowStateRepo()
        val viewModel = loadedViewModel(repository)

        viewModel.onWindowChanged(observation(bounds = bounds()))
        viewModel.onWindowChanged(observation(bounds = bounds()))
        viewModel.flush()

        assertTrue(repository.savedStates.isEmpty())
    }

    @Test
    /** Verifies maximizing stores the last floating bounds with a maximized marker. */
    fun `maximizing preserves the last floating bounds`() = runBlocking {
        val floating = bounds(x = 80, y = 60, width = 1200, height = 800)
        val repository = RecordingWindowStateRepo(restoredState = floating)
        val viewModel = loadedViewModel(repository)
        viewModel.onWindowChanged(observation(bounds = floating))

        viewModel.onWindowChanged(
            observation(
                mode = WindowMode.Maximized,
                bounds = bounds(x = 0, y = 0, width = 1920, height = 1040),
            ),
        )
        viewModel.flush()

        assertEquals(listOf(floating.copy(isMaximized = true)), repository.savedStates)
    }

    @Test
    /** Verifies minimized observations neither replace bounds nor trigger persistence. */
    fun `minimized observations do not change persisted state`() = runBlocking {
        val floating = bounds()
        val repository = RecordingWindowStateRepo(restoredState = floating)
        val viewModel = loadedViewModel(repository)
        viewModel.onWindowChanged(observation(bounds = floating))

        viewModel.onWindowChanged(
            observation(
                mode = WindowMode.Maximized,
                isMinimized = true,
                bounds = bounds(width = 1920, height = 1040),
            ),
        )
        viewModel.flush()

        assertTrue(repository.savedStates.isEmpty())
    }

    @Test
    /** Verifies trailing-edge debounce persists only the most recent observation. */
    fun `debounce persists only the latest state`() = runBlocking {
        val repository = RecordingWindowStateRepo()
        val viewModel = loadedViewModel(repository, debounceMillis = 30)
        viewModel.onWindowChanged(observation(bounds = bounds(x = 50)))

        viewModel.onWindowChanged(observation(bounds = bounds(x = 100)))
        delay(10)
        val latest = bounds(x = 200)
        viewModel.onWindowChanged(observation(bounds = latest))
        delay(50)

        assertEquals(listOf(latest), repository.savedStates)
    }

    @Test
    /** Verifies shutdown flush bypasses a pending debounce delay. */
    fun `flush immediately persists pending state`() = runBlocking {
        val repository = RecordingWindowStateRepo()
        val viewModel = loadedViewModel(repository, debounceMillis = 10_000)
        viewModel.onWindowChanged(observation(bounds = bounds(x = 50)))
        val pending = bounds(x = 300)
        viewModel.onWindowChanged(observation(bounds = pending))

        viewModel.flush()

        assertEquals(listOf(pending), repository.savedStates)
    }

    @Test
    /** Verifies a failed write does not prevent a later state from being persisted. */
    fun `save failure does not prevent a later state from being saved`() = runBlocking {
        val repository = RecordingWindowStateRepo(saveFailure = true)
        val viewModel = loadedViewModel(repository, debounceMillis = 10_000)
        viewModel.onWindowChanged(observation(bounds = bounds(x = 50)))
        viewModel.onWindowChanged(observation(bounds = bounds(x = 100)))
        viewModel.flush()
        repository.saveFailure = false
        val recovered = bounds(x = 200)

        viewModel.onWindowChanged(observation(bounds = recovered))
        viewModel.flush()

        assertFalse(repository.saveFailure)
        assertEquals(listOf(recovered), repository.savedStates)
    }

    /** Creates a ViewModel and waits until its initial repository read completes. */
    private suspend fun loadedViewModel(
        repository: WindowStateRepo,
        debounceMillis: Long = 500,
    ): WindowStateViewModel {
        val viewModel = WindowStateViewModel(repository, debounceMillis)
        withTimeout(1_000) {
            viewModel.uiState.first { state -> state.isLoaded }
        }
        return viewModel
    }

    /** Creates a platform-independent window observation fixture. */
    private fun observation(
        mode: WindowMode = WindowMode.Floating,
        isMinimized: Boolean = false,
        bounds: SavedWindowState?,
    ) = WindowObservation(
        mode = mode,
        isMinimized = isMinimized,
        bounds = bounds,
    )

    /** Creates a saved-window-state fixture with configurable bounds. */
    private fun bounds(
        x: Int = 100,
        y: Int = 100,
        width: Int = 1280,
        height: Int = 720,
    ) = SavedWindowState(
        x = x,
        y = y,
        width = width,
        height = height,
        isMaximized = false,
    )

    private class RecordingWindowStateRepo(
        private val restoredState: SavedWindowState? = null,
        private val loadFailure: Boolean = false,
        var saveFailure: Boolean = false,
    ) : WindowStateRepo {
        val savedStates = CopyOnWriteArrayList<SavedWindowState>()

        /** Returns configured state or raises the configured load failure. */
        override suspend fun load(): SavedWindowState? {
            if (loadFailure) error("load failed")
            return restoredState
        }

        /** Records saved state unless the repository is configured to fail. */
        override suspend fun save(state: SavedWindowState) {
            if (saveFailure) error("save failed")
            savedStates += state
        }
    }
}
