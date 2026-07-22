package com.rocybyte.weisome.window.biz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rocybyte.weisome.repository.window.WindowStateRepo
import com.rocybyte.weisome.window.SavedWindowState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal const val WindowSaveDebounceMillis = 500L

class WindowStateViewModel(
    private val repository: WindowStateRepo,
    private val debounceMillis: Long = WindowSaveDebounceMillis,
) : ViewModel() {
    private val _uiState = MutableStateFlow(WindowUiState())
    val uiState: StateFlow<WindowUiState> = _uiState.asStateFlow()

    private var normalBounds: SavedWindowState? = null
    private var lastObservedState: SavedWindowState? = null
    private var hasStableInitialState = false
    private var pendingState: SavedWindowState? = null
    private var debounceJob: Job? = null

    init {
        viewModelScope.launch {
            val restoredState = try {
                repository.load()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                System.err.println("Unable to load window state: ${error.message}")
                null
            }
            normalBounds = restoredState?.copy(isMaximized = false)
            lastObservedState = restoredState
            _uiState.value = WindowUiState(
                isLoaded = true,
                restoredState = restoredState,
            )
        }
    }

    /** Records a platform-independent window observation and schedules meaningful changes for saving. */
    fun onWindowChanged(observation: WindowObservation) {
        if (!_uiState.value.isLoaded || observation.isMinimized) return

        if (observation.mode == WindowMode.Floating) {
            normalBounds = observation.bounds?.copy(isMaximized = false) ?: normalBounds
        }

        val currentState = normalBounds?.copy(
            isMaximized = observation.mode == WindowMode.Maximized,
        ) ?: return

        if (!hasStableInitialState) {
            hasStableInitialState = true
            lastObservedState = currentState
            return
        }

        if (currentState == lastObservedState) return
        lastObservedState = currentState
        submit(currentState)
    }

    /** Cancels the debounce delay and persists the latest pending state before shutdown. */
    suspend fun flush() {
        debounceJob?.cancelAndJoin()
        debounceJob = null
        persistPendingState()
    }

    /** Replaces the pending state and restarts the trailing-edge debounce timer. */
    private fun submit(state: SavedWindowState) {
        pendingState = state
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(debounceMillis)
            persistPendingState()
        }
    }

    /** Saves the pending state and retains it when persistence fails. */
    private suspend fun persistPendingState() {
        val state = pendingState ?: return
        try {
            repository.save(state)
            if (pendingState == state) {
                pendingState = null
            }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            System.err.println("Unable to save window state: ${error.message}")
        }
    }
}
