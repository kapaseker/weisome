package com.rocybyte.weisome.storage.window

import com.rocybyte.weisome.window.SavedWindowState

interface WindowStateStore {
    /** Reads the complete window-state record from local storage when available. */
    suspend fun load(): SavedWindowState?

    /** Writes every field of the supplied window state to local storage. */
    suspend fun save(state: SavedWindowState)
}
