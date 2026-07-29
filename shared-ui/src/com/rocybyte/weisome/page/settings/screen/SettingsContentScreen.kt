package com.rocybyte.weisome.page.settings.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.rocybyte.weisome.generated.resources.Res
import com.rocybyte.weisome.generated.resources.back
import com.rocybyte.weisome.generated.resources.custom_scale
import com.rocybyte.weisome.generated.resources.default_ui_scale
import com.rocybyte.weisome.generated.resources.device_default_scale
import com.rocybyte.weisome.generated.resources.ic_left
import com.rocybyte.weisome.generated.resources.ic_settings
import com.rocybyte.weisome.generated.resources.preview
import com.rocybyte.weisome.generated.resources.preview_body
import com.rocybyte.weisome.generated.resources.preview_button
import com.rocybyte.weisome.generated.resources.preview_label
import com.rocybyte.weisome.generated.resources.preview_title
import com.rocybyte.weisome.generated.resources.reset_text_scale
import com.rocybyte.weisome.generated.resources.reset_ui_scale
import com.rocybyte.weisome.generated.resources.settings
import com.rocybyte.weisome.generated.resources.text_scale
import com.rocybyte.weisome.generated.resources.text_scale_load_failed
import com.rocybyte.weisome.generated.resources.text_scale_save_failed
import com.rocybyte.weisome.generated.resources.ui_scale
import com.rocybyte.weisome.generated.resources.ui_scale_load_failed
import com.rocybyte.weisome.generated.resources.ui_scale_save_failed
import com.rocybyte.weisome.page.settings.biz.TextScaleUiState
import com.rocybyte.weisome.page.settings.biz.UiScaleUiState
import com.rocybyte.weisome.page.settings.biz.displayScaleLabel
import com.rocybyte.weisome.page.settings.biz.scaledDensity
import com.rocybyte.weisome.settings.DisplayScaleSliderSteps
import com.rocybyte.weisome.settings.DefaultUiScale
import com.rocybyte.weisome.settings.MaximumDisplayScale
import com.rocybyte.weisome.settings.MinimumDisplayScale
import com.rocybyte.weisome.widget.MediumIconButton
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** Renders independent text and UI scale controls with focused previews. */
@Composable
internal fun SettingsContentScreen(
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
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface),
    ) {
        SettingsHeader(onBack)
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TextScaleSettings(
                state = textState,
                selectedScale = selectedTextScale,
                onScaleChanged = onTextScaleChanged,
                onScaleChangeFinished = onTextScaleChangeFinished,
                onReset = onResetTextScale,
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            UiScaleSettings(
                state = uiState,
                selectedTextScale = selectedTextScale,
                systemDensity = systemDensity,
                onScaleChanged = onUiScaleChanged,
                onScaleChangeFinished = onUiScaleChangeFinished,
                onReset = onResetUiScale,
            )
        }
    }
}

/** Renders the text-scale slider, reset action, status, and typography preview. */
@Composable
private fun TextScaleSettings(
    state: TextScaleUiState,
    selectedScale: Float,
    onScaleChanged: (Float) -> Unit,
    onScaleChangeFinished: () -> Unit,
    onReset: () -> Unit,
) {
    Text(stringResource(Res.string.text_scale), style = MaterialTheme.typography.titleLarge)
    Text(
        text = if (state.userScale == null) {
            stringResource(Res.string.device_default_scale, displayScaleLabel(selectedScale))
        } else {
            stringResource(Res.string.custom_scale, displayScaleLabel(selectedScale))
        },
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Slider(
        value = selectedScale,
        onValueChange = onScaleChanged,
        modifier = Modifier.fillMaxWidth(),
        valueRange = MinimumDisplayScale..MaximumDisplayScale,
        steps = DisplayScaleSliderSteps,
        onValueChangeFinished = onScaleChangeFinished,
    )
    Button(onClick = onReset, enabled = state.userScale != null) {
        Text(stringResource(Res.string.reset_text_scale))
    }
    if (state.loadFailed) ErrorText(stringResource(Res.string.text_scale_load_failed))
    if (state.saveFailed) ErrorText(stringResource(Res.string.text_scale_save_failed))
    Text(stringResource(Res.string.preview), style = MaterialTheme.typography.titleMedium)
    Text(stringResource(Res.string.preview_title), style = MaterialTheme.typography.titleLarge)
    Text(stringResource(Res.string.preview_body), style = MaterialTheme.typography.bodyLarge)
    Text(
        stringResource(Res.string.preview_label),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

/** Renders the UI-scale slider, reset action, status, and button-only preview. */
@Composable
private fun UiScaleSettings(
    state: UiScaleUiState,
    selectedTextScale: Float,
    systemDensity: Density,
    onScaleChanged: (Float) -> Unit,
    onScaleChangeFinished: () -> Unit,
    onReset: () -> Unit,
) {
    Text(stringResource(Res.string.ui_scale), style = MaterialTheme.typography.titleLarge)
    Text(
        text = if (state.userScale == null && state.previewScale == DefaultUiScale) {
            stringResource(Res.string.default_ui_scale, displayScaleLabel(state.previewScale))
        } else {
            stringResource(Res.string.custom_scale, displayScaleLabel(state.previewScale))
        },
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Slider(
        value = state.previewScale,
        onValueChange = onScaleChanged,
        modifier = Modifier.fillMaxWidth(),
        valueRange = MinimumDisplayScale..MaximumDisplayScale,
        steps = DisplayScaleSliderSteps,
        onValueChangeFinished = onScaleChangeFinished,
    )
    Button(onClick = onReset, enabled = state.userScale != null) {
        Text(stringResource(Res.string.reset_ui_scale))
    }
    if (state.loadFailed) ErrorText(stringResource(Res.string.ui_scale_load_failed))
    if (state.saveFailed) ErrorText(stringResource(Res.string.ui_scale_save_failed))
    Text(stringResource(Res.string.preview), style = MaterialTheme.typography.titleMedium)
    UiScaleButtonPreview(
        textScale = selectedTextScale,
        uiScale = state.previewScale,
        systemDensity = systemDensity,
    )
}

/** Shows text and icon buttons at the pending UI scale without resizing the settings page. */
@Composable
private fun UiScaleButtonPreview(
    textScale: Float,
    uiScale: Float,
    systemDensity: Density,
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        CompositionLocalProvider(LocalDensity provides scaledDensity(systemDensity, textScale, uiScale)) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(onClick = {}) { Text(stringResource(Res.string.preview_button)) }
                MediumIconButton(
                    onClick = {},
                    painter = painterResource(Res.drawable.ic_settings),
                    contentDescription = stringResource(Res.string.settings),
                )
            }
        }
    }
}

/** Renders the settings destination title and back action. */
@Composable
private fun SettingsHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MediumIconButton(
            onClick = onBack,
            painter = painterResource(Res.drawable.ic_left),
            contentDescription = stringResource(Res.string.back),
        )
        Text(stringResource(Res.string.settings), style = MaterialTheme.typography.headlineSmall)
    }
}

/** Displays a settings error using the Material error role. */
@Composable
private fun ErrorText(text: String) {
    Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
}
