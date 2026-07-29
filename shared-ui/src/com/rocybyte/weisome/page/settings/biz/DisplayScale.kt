package com.rocybyte.weisome.page.settings.biz

import androidx.compose.ui.unit.Density
import com.rocybyte.weisome.settings.DefaultUiScale
import com.rocybyte.weisome.settings.normalizeDisplayScale
import kotlin.math.roundToInt

/** Normalizes the text scale derived from the current display density. */
internal fun deviceDefaultTextScale(density: Float): Float = normalizeDisplayScale(density)

/** Returns the valid text override or the current device-default text scale. */
internal fun selectedTextScale(
    density: Float,
    userScale: Float?,
): Float = userScale
    ?.takeIf(Float::isFinite)
    ?.let(::normalizeDisplayScale)
    ?: deviceDefaultTextScale(density)

/** Returns the valid UI override or the neutral UI scale. */
internal fun selectedUiScale(userScale: Float?): Float = userScale
    ?.takeIf(Float::isFinite)
    ?.let(::normalizeDisplayScale)
    ?: DefaultUiScale

/** Creates a density that scales DP independently while preserving the selected text size. */
internal fun scaledDensity(
    systemDensity: Density,
    textScale: Float,
    uiScale: Float,
): Density {
    val density = systemDensity.density
    val normalizedUiScale = normalizeDisplayScale(uiScale)
    if (!density.isFinite() || density <= 0f) return systemDensity

    return Density(
        density = density * normalizedUiScale,
        fontScale = systemDensity.fontScale * normalizeDisplayScale(textScale) /
            (density * normalizedUiScale),
    )
}

/** Formats a normalized display scale with one decimal place. */
internal fun displayScaleLabel(scale: Float): String {
    val tenths = (normalizeDisplayScale(scale) * 10).roundToInt()
    return "${tenths / 10}.${tenths % 10}x"
}
