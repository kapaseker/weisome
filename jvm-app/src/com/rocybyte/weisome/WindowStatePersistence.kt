package com.rocybyte.weisome

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState
import com.rocybyte.weisome.settings.SavedWindowState
import com.rocybyte.weisome.settings.WindowStateStore
import com.rocybyte.weisome.ui.WeiSomeApp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

private const val WindowSaveDebounceMillis = 500L

@Composable
internal fun ApplicationScope.WeiSomeWindow(
    initialState: SavedWindowState?,
    store: WindowStateStore,
) {
    val windowState = if (initialState == null) {
        rememberWindowState()
    } else {
        rememberWindowState(
            placement = if (initialState.isMaximized) {
                WindowPlacement.Maximized
            } else {
                WindowPlacement.Floating
            },
            isMinimized = false,
            position = WindowPosition(initialState.x.dp, initialState.y.dp),
            width = initialState.width.dp,
            height = initialState.height.dp,
        )
    }
    val coroutineScope = rememberCoroutineScope()
    val accumulator = remember(initialState) { WindowStateAccumulator(initialState) }
    val saveCoordinator = remember(store, coroutineScope) {
        WindowStateSaveCoordinator(store, coroutineScope)
    }
    val closeCoordinator = remember(saveCoordinator, coroutineScope) {
        WindowCloseCoordinator(coroutineScope, saveCoordinator, ::exitApplication)
    }

    LaunchedEffect(windowState, accumulator, saveCoordinator) {
        snapshotFlow { windowState.toObservation() }
            .collect { observation ->
                accumulator.observe(observation)?.let(saveCoordinator::submit)
            }
    }

    Window(
        onCloseRequest = closeCoordinator::close,
        state = windowState,
        title = "WeiSome",
    ) {
        WeiSomeApp()
    }
}

internal data class WindowObservation(
    val placement: WindowPlacement,
    val isMinimized: Boolean,
    val bounds: SavedWindowState?,
)

internal class WindowStateAccumulator(initialState: SavedWindowState?) {
    private var normalBounds = initialState?.copy(isMaximized = false)
    private var lastObservedState = initialState
    private var hasStableInitialState = false

    fun observe(observation: WindowObservation): SavedWindowState? {
        if (observation.isMinimized) return null

        if (observation.placement == WindowPlacement.Floating) {
            normalBounds = observation.bounds?.copy(isMaximized = false) ?: normalBounds
        }

        val currentState = normalBounds?.copy(
            isMaximized = observation.placement == WindowPlacement.Maximized,
        ) ?: return null

        if (!hasStableInitialState) {
            hasStableInitialState = true
            lastObservedState = currentState
            return null
        }

        if (currentState == lastObservedState) return null
        lastObservedState = currentState
        return currentState
    }
}

internal class WindowStateSaveCoordinator(
    private val store: WindowStateStore,
    private val coroutineScope: CoroutineScope,
    private val debounceMillis: Long = WindowSaveDebounceMillis,
) {
    private var pendingState: SavedWindowState? = null
    private var debounceJob: Job? = null

    fun submit(state: SavedWindowState) {
        pendingState = state
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            delay(debounceMillis)
            persistPendingState()
        }
    }

    suspend fun flush() {
        debounceJob?.cancelAndJoin()
        debounceJob = null
        persistPendingState()
    }

    private suspend fun persistPendingState() {
        val state = pendingState ?: return
        try {
            withContext(Dispatchers.IO) {
                store.save(state)
            }
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

private class WindowCloseCoordinator(
    private val coroutineScope: CoroutineScope,
    private val saveCoordinator: WindowStateSaveCoordinator,
    private val exitApplication: () -> Unit,
) {
    private var isClosing = false

    fun close() {
        if (isClosing) return
        isClosing = true
        coroutineScope.launch {
            saveCoordinator.flush()
            exitApplication()
        }
    }
}

private fun WindowState.toObservation(): WindowObservation {
    val x = position.x.value
    val y = position.y.value
    val width = size.width.value
    val height = size.height.value
    val hasValidBounds = position.isSpecified &&
        size.width.isSpecified &&
        size.height.isSpecified &&
        x.isFinite() &&
        y.isFinite() &&
        width.isFinite() &&
        height.isFinite() &&
        width > 0f &&
        height > 0f

    return WindowObservation(
        placement = placement,
        isMinimized = isMinimized,
        bounds = if (hasValidBounds) {
            SavedWindowState(
                x = x.roundToInt(),
                y = y.roundToInt(),
                width = width.roundToInt(),
                height = height.roundToInt(),
                isMaximized = false,
            )
        } else {
            null
        },
    )
}
