package com.rocybyte.weisome.repository.settings

import com.rocybyte.weisome.settings.DisplaySettings
import com.rocybyte.weisome.settings.normalizeDisplayScale
import com.rocybyte.weisome.storage.settings.DisplaySettingsStore

internal class DisplaySettingsRepository(
    private val store: DisplaySettingsStore,
) : DisplaySettingsRepo {
    /** Loads normalized display-scale overrides from storage. */
    override suspend fun load(): DisplaySettings = store.load().normalized()

    /** Persists a normalized text-scale override or clears it when null. */
    override suspend fun saveUserTextScale(scale: Float?) {
        store.saveUserTextScale(scale?.let(::normalizeDisplayScale))
    }

    /** Persists a normalized UI-scale override or clears it when null. */
    override suspend fun saveUserUiScale(scale: Float?) {
        store.saveUserUiScale(scale?.let(::normalizeDisplayScale))
    }
}

/** Normalizes every finite display-scale override and discards invalid values. */
private fun DisplaySettings.normalized(): DisplaySettings = copy(
    userTextScale = userTextScale?.takeIf(Float::isFinite)?.let(::normalizeDisplayScale),
    userUiScale = userUiScale?.takeIf(Float::isFinite)?.let(::normalizeDisplayScale),
)
