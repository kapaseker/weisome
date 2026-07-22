package com.rocybyte.weisome.window

data class SavedWindowState(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val isMaximized: Boolean,
)

fun SavedWindowState.hasValidBounds(): Boolean = width > 0 && height > 0
