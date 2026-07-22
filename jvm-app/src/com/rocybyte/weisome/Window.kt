package com.rocybyte.weisome

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.rocybyte.weisome.ui.WeiSomeApp
import com.rocybyte.weisome.window.SavedWindowState
import com.rocybyte.weisome.window.biz.WindowMode
import com.rocybyte.weisome.window.biz.WindowObservation
import com.rocybyte.weisome.window.biz.WindowStateViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@Composable
internal fun ApplicationScope.WeiSomeApplication() {
    val viewModelStoreOwner = remember {
        object : ViewModelStoreOwner {
            override val viewModelStore = ViewModelStore()
        }
    }
    DisposableEffect(viewModelStoreOwner) {
        onDispose(viewModelStoreOwner.viewModelStore::clear)
    }

    CompositionLocalProvider(LocalViewModelStoreOwner provides viewModelStoreOwner) {
        val viewModel = koinViewModel<WindowStateViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        if (uiState.isLoaded) {
            val initialState = remember(uiState.restoredState) {
                uiState.restoredState?.let { savedState ->
                    val (workAreas, defaultWorkArea) = currentScreenWorkAreas()
                    savedState.clampToVisibleScreen(workAreas, defaultWorkArea)
                }
            }
            WeiSomeWindow(initialState, viewModel)
        }
    }
}

@Composable
private fun ApplicationScope.WeiSomeWindow(
    initialState: SavedWindowState?,
    viewModel: WindowStateViewModel,
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
    var isClosing by remember { mutableStateOf(false) }

    LaunchedEffect(windowState, viewModel) {
        snapshotFlow { windowState.toObservation() }
            .collect(viewModel::onWindowChanged)
    }

    Window(
        onCloseRequest = {
            if (!isClosing) {
                isClosing = true
                coroutineScope.launch {
                    viewModel.flush()
                    exitApplication()
                }
            }
        },
        state = windowState,
        title = "WeiSome",
    ) {
        WeiSomeApp()
    }
}

internal fun WindowState.toObservation(): WindowObservation {
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
        mode = placement.toWindowMode(),
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

internal fun WindowPlacement.toWindowMode(): WindowMode = when (this) {
    WindowPlacement.Floating -> WindowMode.Floating
    WindowPlacement.Maximized -> WindowMode.Maximized
    WindowPlacement.Fullscreen -> WindowMode.Fullscreen
}
