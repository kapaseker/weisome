package com.rocybyte.weisome.storage.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import com.rocybyte.weisome.settings.DisplaySettings
import kotlinx.coroutines.flow.first

internal class DisplaySettingsStorage(
    private val dataStore: DataStore<Preferences>,
) : DisplaySettingsStore {
    /** Reads both optional display-scale overrides from one preferences snapshot. */
    override suspend fun load(): DisplaySettings {
        val preferences = dataStore.data.first()
        return DisplaySettings(
            userTextScale = preferences[UserTextScaleKey],
            userUiScale = preferences[UserUiScaleKey],
        )
    }

    /** Stores the text-scale override or removes its preference when null. */
    override suspend fun saveUserTextScale(scale: Float?) {
        dataStore.edit { preferences ->
            if (scale == null) {
                preferences.remove(UserTextScaleKey)
            } else {
                preferences[UserTextScaleKey] = scale
            }
        }
    }

    /** Stores the UI-scale override or removes its preference when null. */
    override suspend fun saveUserUiScale(scale: Float?) {
        dataStore.edit { preferences ->
            if (scale == null) {
                preferences.remove(UserUiScaleKey)
            } else {
                preferences[UserUiScaleKey] = scale
            }
        }
    }

    private companion object {
        val UserTextScaleKey = floatPreferencesKey("user_text_scale")
        val UserUiScaleKey = floatPreferencesKey("user_ui_scale")
    }
}
