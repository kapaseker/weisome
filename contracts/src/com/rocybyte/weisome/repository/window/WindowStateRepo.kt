package com.rocybyte.weisome.repository.window

import com.rocybyte.weisome.window.SavedWindowState

interface WindowStateRepo {
    /** Loads the last valid window state, or `null` when no restorable state exists. */
    suspend fun load(): SavedWindowState?

    /** Validates and persists the supplied window state. */
    suspend fun save(state: SavedWindowState)
}
