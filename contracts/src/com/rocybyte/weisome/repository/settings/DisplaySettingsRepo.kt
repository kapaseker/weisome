package com.rocybyte.weisome.repository.settings

import com.rocybyte.weisome.settings.DisplaySettings

interface DisplaySettingsRepo {
    /** Loads the persisted display-scale overrides. */
    suspend fun load(): DisplaySettings

    /** Persists or clears the user-selected text-scale override. */
    suspend fun saveUserTextScale(scale: Float?)

    /** Persists or clears the user-selected UI-scale override. */
    suspend fun saveUserUiScale(scale: Float?)
}
