package com.rocybyte.weisome.storage.window

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.rocybyte.weisome.window.SavedWindowState
import kotlinx.coroutines.flow.first

internal class WindowStateStorage(
    private val dataStore: DataStore<Preferences>,
) : WindowStateStore {
    /** Reconstructs window state only when every persisted preference is present. */
    override suspend fun load(): SavedWindowState? {
        val preferences = dataStore.data.first()
        val x = preferences[WindowXKey] ?: return null
        val y = preferences[WindowYKey] ?: return null
        val width = preferences[WindowWidthKey] ?: return null
        val height = preferences[WindowHeightKey] ?: return null
        val isMaximized = preferences[WindowMaximizedKey] ?: return null

        return SavedWindowState(x, y, width, height, isMaximized)
    }

    /** Atomically writes all window-state fields to DataStore Preferences. */
    override suspend fun save(state: SavedWindowState) {
        dataStore.edit { preferences ->
            preferences[WindowXKey] = state.x
            preferences[WindowYKey] = state.y
            preferences[WindowWidthKey] = state.width
            preferences[WindowHeightKey] = state.height
            preferences[WindowMaximizedKey] = state.isMaximized
        }
    }

    private companion object {
        val WindowXKey = intPreferencesKey("window_x")
        val WindowYKey = intPreferencesKey("window_y")
        val WindowWidthKey = intPreferencesKey("window_width")
        val WindowHeightKey = intPreferencesKey("window_height")
        val WindowMaximizedKey = booleanPreferencesKey("window_maximized")
    }
}
