package com.rocybyte.weisome.settings

import kotlin.math.roundToInt

const val MinimumDisplayScale = 0.5f
const val MaximumDisplayScale = 4.0f
const val DisplayScaleStep = 0.1f
const val DisplayScaleSliderSteps = 34
const val DefaultUiScale = 1.0f

data class DisplaySettings(
    val userTextScale: Float? = null,
    val userUiScale: Float? = null,
)

/** Rounds a display scale to the supported step and bounds. */
fun normalizeDisplayScale(value: Float): Float {
    if (!value.isFinite()) return MinimumDisplayScale

    return ((value / DisplayScaleStep).roundToInt() * DisplayScaleStep)
        .coerceIn(MinimumDisplayScale, MaximumDisplayScale)
}
