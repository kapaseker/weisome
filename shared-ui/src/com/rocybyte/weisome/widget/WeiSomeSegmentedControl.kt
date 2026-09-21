package com.rocybyte.weisome.widget

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.rocybyte.weisome.ui.WeiSomeColors
import com.rocybyte.weisome.ui.WeiSomeSpacing

/** An icon-only selectable option rendered inside a [WeiSomeSegmentedControl]. */
internal class WeiSomeSegmentedOption(
    val painter: Painter,
    val contentDescription: String?,
)

/**
 * Apple-style segmented control: a light gray track with a sliding white pill and
 * icon options. Options are fixed-width, which suits icon-only controls.
 * ponytail: switch to measured per-item widths when text options are needed.
 */
@Composable
internal fun WeiSomeSegmentedControl(
    options: List<WeiSomeSegmentedOption>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    // ponytail: fixed 48dp items keep the pill math trivial; text options need intrinsic widths.
    val itemWidth = 48.dp
    val trackHeight = 36.dp
    val trackShape = RoundedCornerShape(50)
    val pillOffset by animateDpAsState(
        targetValue = itemWidth * selectedIndex,
        label = "segmentedPillOffset",
    )

    Box(
        modifier = modifier
            .width(itemWidth * options.size)
            .height(trackHeight)
            .clip(trackShape)
            .background(WeiSomeColors.surfaceContainerHigh, trackShape),
    ) {
        Box(
            modifier = Modifier
                .offset(x = pillOffset)
                .size(width = itemWidth, height = trackHeight)
                .padding(WeiSomeSpacing.stackXs)
                .clip(trackShape)
                .background(WeiSomeColors.surfaceContainerLowest, trackShape),
        )
        Row(Modifier.fillMaxSize()) {
            options.forEachIndexed { index, option ->
                Box(
                    modifier = Modifier
                        .size(width = itemWidth, height = trackHeight)
                        .clickable { onSelected(index) },
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = option.painter,
                        contentDescription = option.contentDescription,
                        colorFilter = ColorFilter.tint(
                            if (index == selectedIndex) WeiSomeColors.primary else WeiSomeColors.onSurfaceVariant,
                        ),
                    )
                }
            }
        }
    }
}
