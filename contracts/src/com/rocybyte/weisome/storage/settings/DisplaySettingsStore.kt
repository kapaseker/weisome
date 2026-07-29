package com.rocybyte.weisome.storage.settings

import com.rocybyte.weisome.settings.DisplaySettings

interface DisplaySettingsStore {
    /** Reads the stored display-scale overrides. */
    suspend fun load(): DisplaySettings

    /** Writes or removes the stored text-scale override. */
    suspend fun saveUserTextScale(scale: Float?)

    /** Writes or removes the stored UI-scale override. */
    suspend fun saveUserUiScale(scale: Float?)
}
