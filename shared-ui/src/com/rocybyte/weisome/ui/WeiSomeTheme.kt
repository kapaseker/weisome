package com.rocybyte.weisome.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import com.rocybyte.weisome.page.settings.biz.scaledDensity

/** Applies WeiSome ambient text style and independently selected text and UI scales to [content]. */
@Composable
internal fun WeiSomeTheme(
    textScale: Float,
    uiScale: Float,
    content: @Composable () -> Unit,
) {
    val systemDensity = LocalDensity.current
    CompositionLocalProvider(
        LocalDensity provides scaledDensity(systemDensity, textScale, uiScale),
        LocalWeiSomeTextStyle provides WeiSomeTypography.bodyMd,
    ) {
        content()
    }
}
