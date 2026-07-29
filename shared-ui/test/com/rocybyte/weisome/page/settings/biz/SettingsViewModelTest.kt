package com.rocybyte.weisome.page.settings.biz

import com.rocybyte.weisome.repository.settings.DisplaySettingsRepo
import com.rocybyte.weisome.settings.DisplaySettings
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SettingsViewModelTest {
    @Test
    /** Verifies persisted text and UI overrides are loaded into separate state holders. */
    fun `loads persisted display settings`() = runBlocking {
        val viewModel = SettingsViewModel(
            FakeDisplaySettingsRepo(DisplaySettings(userTextScale = 1.4f, userUiScale = 1.6f)),
        )

        withTimeout(1_000) { viewModel.uiScaleState.first { state -> state.isLoaded } }

        assertEquals(1.4f, viewModel.textScaleState.value.userScale)
        assertEquals(1.6f, viewModel.uiScaleState.value.userScale)
        assertEquals(1.6f, viewModel.uiScaleState.value.previewScale)
    }

    @Test
    /** Verifies UI dragging changes only its preview until the interaction is committed. */
    fun `ui scale preview applies only after slider finishes`() = runBlocking {
        val repository = FakeDisplaySettingsRepo()
        val viewModel = SettingsViewModel(repository)
        withTimeout(1_000) { viewModel.uiScaleState.first { state -> state.isLoaded } }

        viewModel.previewUiScale(2.04f)

        assertNull(viewModel.uiScaleState.value.userScale)
        assertEquals(2f, viewModel.uiScaleState.value.previewScale)

        viewModel.applyPreviewedUiScale()

        assertEquals(2f, viewModel.uiScaleState.value.userScale)
        assertEquals(2f, withTimeout(1_000) { repository.uiSaves.receive() })
    }

    @Test
    /** Verifies text preview is immediate and both reset actions remain independent. */
    fun `text and ui scales reset independently`() = runBlocking {
        val repository = FakeDisplaySettingsRepo(
            DisplaySettings(userTextScale = 1.5f, userUiScale = 1.8f),
        )
        val viewModel = SettingsViewModel(repository)
        withTimeout(1_000) { viewModel.uiScaleState.first { state -> state.isLoaded } }

        viewModel.previewTextScale(2.2f)
        assertEquals(2.2f, viewModel.textScaleState.value.userScale)
        assertEquals(1.8f, viewModel.uiScaleState.value.userScale)

        viewModel.resetTextScale()
        assertNull(viewModel.textScaleState.value.userScale)
        assertEquals(1.8f, viewModel.uiScaleState.value.userScale)
        assertNull(withTimeout(1_000) { repository.textSaves.receive() })

        viewModel.resetUiScale()
        assertNull(viewModel.uiScaleState.value.userScale)
        assertEquals(1f, viewModel.uiScaleState.value.previewScale)
        assertNull(withTimeout(1_000) { repository.uiSaves.receive() })
    }

    @Test
    /** Verifies failed saves retain the selected session value and expose an error. */
    fun `save failure retains session selection`() = runBlocking {
        val repository = FakeDisplaySettingsRepo(failTextSave = true)
        val viewModel = SettingsViewModel(repository)
        withTimeout(1_000) { viewModel.textScaleState.first { state -> state.isLoaded } }

        viewModel.previewTextScale(1.7f)
        viewModel.savePreviewedTextScale()
        withTimeout(1_000) { viewModel.textScaleState.first { state -> state.saveFailed } }

        assertEquals(1.7f, viewModel.textScaleState.value.userScale)
        assertTrue(viewModel.textScaleState.value.saveFailed)
        assertFalse(viewModel.uiScaleState.value.saveFailed)
    }

    @Test
    /** Verifies rapid UI commits leave the latest requested value persisted last. */
    fun `rapid ui commits persist latest value last`() = runBlocking {
        val firstSaveStarted = CompletableDeferred<Unit>()
        val allowFirstSave = CompletableDeferred<Unit>()
        val repository = FakeDisplaySettingsRepo(
            beforeUiSave = { scale ->
                if (scale == 1.5f) {
                    firstSaveStarted.complete(Unit)
                    allowFirstSave.await()
                }
            },
        )
        val viewModel = SettingsViewModel(repository)
        withTimeout(1_000) { viewModel.uiScaleState.first { state -> state.isLoaded } }

        viewModel.previewUiScale(1.5f)
        viewModel.applyPreviewedUiScale()
        withTimeout(1_000) { firstSaveStarted.await() }
        viewModel.previewUiScale(2f)
        viewModel.applyPreviewedUiScale()
        allowFirstSave.complete(Unit)

        assertEquals(1.5f, withTimeout(1_000) { repository.uiSaves.receive() })
        assertEquals(2f, withTimeout(1_000) { repository.uiSaves.receive() })
        assertEquals(2f, repository.savedUiScale)
    }
}

private class FakeDisplaySettingsRepo(
    private val loadedSettings: DisplaySettings = DisplaySettings(),
    private val failTextSave: Boolean = false,
    private val beforeUiSave: suspend (Float?) -> Unit = {},
) : DisplaySettingsRepo {
    val textSaves = Channel<Float?>(Channel.UNLIMITED)
    val uiSaves = Channel<Float?>(Channel.UNLIMITED)
    var savedUiScale: Float? = null

    /** Returns the configured display settings fixture. */
    override suspend fun load(): DisplaySettings = loadedSettings

    /** Records a text-scale save or throws the configured failure. */
    override suspend fun saveUserTextScale(scale: Float?) {
        if (failTextSave) error("Storage unavailable")
        textSaves.send(scale)
    }

    /** Records UI-scale saves after the optional synchronization hook. */
    override suspend fun saveUserUiScale(scale: Float?) {
        beforeUiSave(scale)
        savedUiScale = scale
        uiSaves.send(scale)
    }
}
