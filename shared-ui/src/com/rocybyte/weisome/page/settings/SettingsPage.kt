package com.rocybyte.weisome.page.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Density
import com.rocybyte.weisome.page.settings.biz.TextScaleUiState
import com.rocybyte.weisome.page.settings.biz.UiScaleUiState
import com.rocybyte.weisome.page.settings.screen.SettingsContentScreen

/** Navigation 3 destination for independent text and UI scaling preferences. */
@Composable
internal fun SettingsPage(
    textState: TextScaleUiState,
    uiState: UiScaleUiState,
    selectedTextScale: Float,
    systemDensity: Density,
    onTextScaleChanged: (Float) -> Unit,
    onTextScaleChangeFinished: () -> Unit,
    onResetTextScale: () -> Unit,
    onUiScaleChanged: (Float) -> Unit,
    onUiScaleChangeFinished: () -> Unit,
    onResetUiScale: () -> Unit,
    onBack: () -> Unit,
) {
    SettingsContentScreen(
        textState = textState,
        uiState = uiState,
        selectedTextScale = selectedTextScale,
        systemDensity = systemDensity,
        onTextScaleChanged = onTextScaleChanged,
        onTextScaleChangeFinished = onTextScaleChangeFinished,
        onResetTextScale = onResetTextScale,
        onUiScaleChanged = onUiScaleChanged,
        onUiScaleChangeFinished = onUiScaleChangeFinished,
        onResetUiScale = onResetUiScale,
        onBack = onBack,
    )
}
