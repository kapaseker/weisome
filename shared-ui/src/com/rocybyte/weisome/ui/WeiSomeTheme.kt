package com.rocybyte.weisome.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import com.rocybyte.weisome.page.settings.biz.scaledDensity

private val weiSomeColorScheme = lightColorScheme(
    primary = WeiSomeColors.Primary,
    onPrimary = WeiSomeColors.OnPrimary,
    secondary = WeiSomeColors.Secondary,
    background = WeiSomeColors.Background,
    onBackground = WeiSomeColors.OnBackground,
    surface = WeiSomeColors.Surface,
)

/** Applies WeiSome colors and independently selected text and UI scales to [content]. */
@Composable
internal fun WeiSomeTheme(
    textScale: Float,
    uiScale: Float,
    content: @Composable () -> Unit,
) {
    val systemDensity = LocalDensity.current
    CompositionLocalProvider(
        LocalDensity provides scaledDensity(systemDensity, textScale, uiScale),
    ) {
        MaterialTheme(
            colorScheme = weiSomeColorScheme,
            content = content,
        )
    }
}
