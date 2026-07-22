package com.rocybyte.weisome.window

data class SavedWindowState(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val isMaximized: Boolean,
)

/** Returns whether both persisted window dimensions are positive. */
fun SavedWindowState.hasValidBounds(): Boolean = width > 0 && height > 0
