package com.rocybyte.weisome.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.rocybyte.weisome.ui.WeiSomeColors
import kotlin.math.round

/**
 * Slider with a primary track fill, thumb, and optional discrete snapping.
 * Pressing anywhere on the track jumps the thumb immediately; dragging continues from there.
 * [onValueChangeFinished] fires when a tap is released or a drag ends.
 * ponytail: pointer input only — no keyboard semantics yet; add focus/keys if slider needs a11y.
 */
@Composable
internal fun WeiSomeSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val thumbSize = 16.dp
    val range = valueRange.endInclusive - valueRange.start

    /** Snaps a track fraction to the nearest allowed position, honoring [steps]. */
    fun snappedFraction(fraction: Float): Float {
        if (steps <= 0) return fraction.coerceIn(0f, 1f)
        val segments = steps + 1
        return (round(fraction * segments) / segments).coerceIn(0f, 1f)
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(32.dp)
            .hoverable(interactionSource)
            .pointerInput(valueRange, steps) {
                // 点击立即跳到按压位置；被拖拽接管时 tryAwaitRelease 返回 false，避免重复触发 finished。
                detectTapGestures(
                    onPress = { offset ->
                        onValueChange(
                            valueRange.start + snappedFraction(offset.x / size.width) * range,
                        )
                        if (tryAwaitRelease()) onValueChangeFinished?.invoke()
                    },
                )
            }
            .pointerInput(valueRange, steps) {
                detectDragGestures(
                    onDragStart = { offset ->
                        onValueChange(
                            valueRange.start + snappedFraction(offset.x / size.width) * range,
                        )
                    },
                    onDrag = { change, _ ->
                        onValueChange(
                            valueRange.start + snappedFraction(change.position.x / size.width) * range,
                        )
                    },
                    onDragEnd = { onValueChangeFinished?.invoke() },
                    onDragCancel = { onValueChangeFinished?.invoke() },
                )
            },
    ) {
        val fraction = snappedFraction(
            ((value - valueRange.start) / range).coerceIn(0f, 1f),
        )
        val trackWidth = maxWidth - thumbSize

        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = thumbSize / 2)
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape)
                .background(WeiSomeColors.surfaceContainerHighest),
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = thumbSize / 2)
                .width(trackWidth * fraction + thumbSize / 2)
                .height(4.dp)
                .clip(CircleShape)
                .background(WeiSomeColors.primary),
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = thumbSize / 2 + trackWidth * fraction)
                .size(thumbSize)
                .scale(if (hovered) 1.15f else 1f)
                .shadow(4.dp, CircleShape, spotColor = WeiSomeColors.primary.copy(alpha = 0.3f))
                .clip(CircleShape)
                .background(WeiSomeColors.primary),
        )
    }
}
