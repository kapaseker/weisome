package com.rocybyte.weisome.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val weiSomeColorScheme = lightColorScheme(
    primary = WeiSomeColors.Primary,
    onPrimary = WeiSomeColors.OnPrimary,
    secondary = WeiSomeColors.Secondary,
    background = WeiSomeColors.Background,
    onBackground = WeiSomeColors.OnBackground,
    surface = WeiSomeColors.Surface,
)

/** Applies WeiSome colors, typography, and component defaults to [content]. */
@Composable
internal fun WeiSomeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = weiSomeColorScheme,
        content = content,
    )
}
