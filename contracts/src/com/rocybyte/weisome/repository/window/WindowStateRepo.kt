package com.rocybyte.weisome.repository.window

import com.rocybyte.weisome.window.SavedWindowState

interface WindowStateRepo {
    suspend fun load(): SavedWindowState?

    suspend fun save(state: SavedWindowState)
}
