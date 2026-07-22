package com.rocybyte.weisome.storage.window

import com.rocybyte.weisome.window.SavedWindowState

interface WindowStateStore {
    suspend fun load(): SavedWindowState?

    suspend fun save(state: SavedWindowState)
}
