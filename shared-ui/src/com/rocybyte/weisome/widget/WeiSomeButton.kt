package com.rocybyte.weisome.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rocybyte.weisome.ui.WeiSomeBorders
import com.rocybyte.weisome.ui.WeiSomeColors
import com.rocybyte.weisome.ui.WeiSomeShapes
import com.rocybyte.weisome.ui.WeiSomeTypography
import com.rocybyte.weisome.ui.weiSomeRipple

/**
 * Primary action button: Electric gradient fill with white label text per DESIGN.md.
 * Shows a faint tint-matched glow while hovered.
 */
@Composable
internal fun WeiSomePrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val shape = WeiSomeShapes.default
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .alpha(if (enabled) 1f else 0.4f)
            .heightIn(min = 40.dp)
            .then(
                if (hovered && enabled) {
                    Modifier.shadow(8.dp, shape, spotColor = WeiSomeColors.primary.copy(alpha = 0.2f))
                } else {
                    Modifier
                },
            )
            .clip(shape)
            .background(Brush.linearGradient(listOf(WeiSomeColors.primary, WeiSomeColors.primaryContainer)))
            .hoverable(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                enabled = enabled,
                // 深色渐变底：ripple 用内容色（白）而非全局 onSurface。
                indication = weiSomeRipple(WeiSomeColors.onPrimary),
                onClick = onClick,
            )
            .padding(horizontal = 24.dp, vertical = 12.dp),
    ) {
        WeiSomeText(text = text, style = WeiSomeTypography.labelSm, color = Color.White)
    }
}

/** Secondary button: white surface with a thin high-contrast border and primary label text. */
@Composable
internal fun WeiSomeSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val shape = WeiSomeShapes.default

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .alpha(if (enabled) 1f else 0.4f)
            .heightIn(min = 40.dp)
            .clip(shape)
            .background(WeiSomeColors.surfaceContainerLowest)
            .border(WeiSomeBorders.thin, WeiSomeColors.outlineVariant, shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
    ) {
        WeiSomeText(text = text, style = WeiSomeTypography.labelSm, color = WeiSomeColors.primary)
    }
}
