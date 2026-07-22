package com.rocybyte.weisome.window.biz

import com.rocybyte.weisome.window.SavedWindowState

data class WindowUiState(
    val isLoaded: Boolean = false,
    val restoredState: SavedWindowState? = null,
)

enum class WindowMode {
    Floating,
    Maximized,
    Fullscreen,
}

data class WindowObservation(
    val mode: WindowMode,
    val isMinimized: Boolean,
    val bounds: SavedWindowState?,
)
