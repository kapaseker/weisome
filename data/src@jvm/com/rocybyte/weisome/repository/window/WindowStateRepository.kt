package com.rocybyte.weisome.repository.window

import com.rocybyte.weisome.storage.window.WindowStateStore
import com.rocybyte.weisome.window.SavedWindowState
import com.rocybyte.weisome.window.hasValidBounds

internal class WindowStateRepository(
    private val store: WindowStateStore,
) : WindowStateRepo {
    override suspend fun load(): SavedWindowState? = store.load()
        ?.takeIf(SavedWindowState::hasValidBounds)

    override suspend fun save(state: SavedWindowState) {
        require(state.hasValidBounds()) { "Window dimensions must be positive" }
        store.save(state)
    }
}
