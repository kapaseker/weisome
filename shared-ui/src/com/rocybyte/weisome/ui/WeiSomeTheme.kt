package com.rocybyte.weisome.ui

import androidx.compose.foundation.LocalIndication
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import com.rocybyte.weisome.page.settings.biz.scaledDensity

/** Applies WeiSome ambient text style, global ripple indication, and independently selected text and UI scales to [content]. */
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
        // 全局按压反馈默认值：任何 clickable 不显式传 indication 时也能有 ripple。
        // 深色底组件（如主按钮）仍需显式传 weiSomeRipple(onPrimary) 覆盖。
        LocalIndication provides weiSomeRipple(WeiSomeColors.onSurface),
    ) {
        content()
    }
}
