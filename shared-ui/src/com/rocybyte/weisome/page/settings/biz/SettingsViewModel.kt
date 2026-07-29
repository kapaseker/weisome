package com.rocybyte.weisome.page.settings.biz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rocybyte.weisome.repository.settings.DisplaySettingsRepo
import com.rocybyte.weisome.settings.DefaultUiScale
import com.rocybyte.weisome.settings.normalizeDisplayScale
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class TextScaleUiState(
    val isLoaded: Boolean = false,
    val userScale: Float? = null,
    val loadFailed: Boolean = false,
    val saveFailed: Boolean = false,
)

data class UiScaleUiState(
    val isLoaded: Boolean = false,
    val userScale: Float? = null,
    val previewScale: Float = DefaultUiScale,
    val loadFailed: Boolean = false,
    val saveFailed: Boolean = false,
)

class SettingsViewModel(
    private val repository: DisplaySettingsRepo,
) : ViewModel() {
    private val _textScaleState = MutableStateFlow(TextScaleUiState())
    val textScaleState: StateFlow<TextScaleUiState> = _textScaleState.asStateFlow()
    private val _uiScaleState = MutableStateFlow(UiScaleUiState())
    val uiScaleState: StateFlow<UiScaleUiState> = _uiScaleState.asStateFlow()
    private val textSaveMutex = Mutex()
    private val uiSaveMutex = Mutex()
    private val textSaveGeneration = AtomicLong()
    private val uiSaveGeneration = AtomicLong()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val settings = repository.load()
                _textScaleState.value = TextScaleUiState(
                    isLoaded = true,
                    userScale = settings.userTextScale,
                )
                _uiScaleState.value = UiScaleUiState(
                    isLoaded = true,
                    userScale = settings.userUiScale,
                    previewScale = selectedUiScale(settings.userUiScale),
                )
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                _textScaleState.value = TextScaleUiState(isLoaded = true, loadFailed = true)
                _uiScaleState.value = UiScaleUiState(isLoaded = true, loadFailed = true)
            }
        }
    }

    /** Updates the in-memory text scale so the full application previews it immediately. */
    fun previewTextScale(scale: Float) {
        _textScaleState.update { state ->
            state.copy(userScale = normalizeDisplayScale(scale), saveFailed = false)
        }
    }

    /** Persists the currently previewed text scale without blocking the UI. */
    fun savePreviewedTextScale() {
        val scale = _textScaleState.value.userScale ?: return
        persistTextScale(scale)
    }

    /** Restores the device-default text scale and removes its persisted override. */
    fun resetTextScale() {
        _textScaleState.update { state -> state.copy(userScale = null, saveFailed = false) }
        persistTextScale(null)
    }

    /** Updates only the button preview until the UI-scale slider interaction finishes. */
    fun previewUiScale(scale: Float) {
        _uiScaleState.update { state ->
            state.copy(previewScale = normalizeDisplayScale(scale), saveFailed = false)
        }
    }

    /** Applies and persists the currently previewed UI scale. */
    fun applyPreviewedUiScale() {
        val scale = _uiScaleState.value.previewScale
        _uiScaleState.update { state -> state.copy(userScale = scale, saveFailed = false) }
        persistUiScale(scale)
    }

    /** Restores the neutral UI scale and removes its persisted override. */
    fun resetUiScale() {
        _uiScaleState.update { state ->
            state.copy(userScale = null, previewScale = DefaultUiScale, saveFailed = false)
        }
        persistUiScale(null)
    }

    /** Serializes text-scale writes and suppresses failures from superseded requests. */
    private fun persistTextScale(scale: Float?) {
        val generation = textSaveGeneration.incrementAndGet()
        viewModelScope.launch(Dispatchers.Default) {
            textSaveMutex.withLock {
                if (generation != textSaveGeneration.get()) return@withLock
                try {
                    repository.saveUserTextScale(scale)
                } catch (error: CancellationException) {
                    throw error
                } catch (_: Exception) {
                    if (generation == textSaveGeneration.get()) {
                        _textScaleState.update { state -> state.copy(saveFailed = true) }
                    }
                }
            }
        }
    }

    /** Serializes UI-scale writes and suppresses failures from superseded requests. */
    private fun persistUiScale(scale: Float?) {
        val generation = uiSaveGeneration.incrementAndGet()
        viewModelScope.launch(Dispatchers.Default) {
            uiSaveMutex.withLock {
                if (generation != uiSaveGeneration.get()) return@withLock
                try {
                    repository.saveUserUiScale(scale)
                } catch (error: CancellationException) {
                    throw error
                } catch (_: Exception) {
                    if (generation == uiSaveGeneration.get()) {
                        _uiScaleState.update { state -> state.copy(saveFailed = true) }
                    }
                }
            }
        }
    }
}
