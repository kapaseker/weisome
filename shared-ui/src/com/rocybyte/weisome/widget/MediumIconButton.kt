package com.rocybyte.weisome.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.rocybyte.weisome.ui.WeiSomeColors
import com.rocybyte.weisome.ui.WeiSomeShapes

/** Renders a consistently sized icon-only action button with a hover highlight. */
@Composable
internal fun MediumIconButton(
    onClick: () -> Unit,
    painter: Painter,
    contentDescription: String,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(52.dp)
            .clip(WeiSomeShapes.default)
            .background(if (hovered) WeiSomeColors.surfaceContainerHigh else WeiSomeColors.surface.copy(alpha = 0f))
            .hoverable(interactionSource)
            .clickable(interactionSource = interactionSource, enabled = enabled, onClick = onClick),
    ) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier.padding(10.dp).fillMaxSize(),
            colorFilter = ColorFilter.tint(WeiSomeColors.onSurfaceVariant),
        )
    }
}
