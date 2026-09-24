package com.rocybyte.weisome.page.article.widget

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownInline
import com.rocybyte.weisome.widget.WeiSomeText

/** Renders a heading block with the active theme's typography, prefix, and borders. */
@Composable
internal fun Heading(block: MarkdownBlock.Heading) {
    val styles = LocalMarkdownPreviewStyles.current
    val spec = styles.headingSpec(block.level)
    val content =
        if (styles.firstLetterCapitalized && block.level in 2..3) capitalizeFirstLetter(block.content) else block.content
    if (spec.borderLeft) {
        BorderedHeading(block.level, content, styles)
    } else {
        PlainHeading(block.level, content, styles)
    }
}

/** Renders a heading without a left border, with the theme's "#" prefix and bottom border when enabled. */
@Composable
private fun PlainHeading(level: Int, content: List<MarkdownInline>, styles: MarkdownPreviewStyles) {
    val spec = styles.headingSpec(level)
    Column(
        Modifier
            .padding(top = spec.top.dp, bottom = spec.bottom.dp)
            .padding(bottom = if (spec.borderBottom) (spec.size * 0.3f).dp else 0.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            if (level == 1 && styles.h1HasPrefix) {
                WeiSomeText(
                    text = "#",
                    color = styles.themeColor,
                    fontSize = spec.size.sp,
                    fontWeight = spec.weight,
                    lineHeight = spec.size.sp * spec.lineHeightMultiplier,
                )
                Spacer(Modifier.width(10.dp))
            }
            if (level == 2 && styles.h2AccentDots) {
                Box(
                    Modifier
                        .size(23.dp)
                        .drawBehind {
                            drawCircle(styles.ruleSolidColor, radius = 7.5.dp.toPx(), center = androidx.compose.ui.geometry.Offset(15.5.dp.toPx(), 14.5.dp.toPx()))
                            drawCircle(styles.themeColor, radius = 7.5.dp.toPx(), center = androidx.compose.ui.geometry.Offset(7.5.dp.toPx(), 7.5.dp.toPx()))
                        },
                )
                Spacer(Modifier.width(7.dp))
            }
            InlineMarkdownText(
                lines = listOf(content),
                fontSize = spec.size.sp,
                fontWeight = spec.weight,
                lineHeight = spec.size.sp * spec.lineHeightMultiplier,
                color = if (spec.muted) styles.mutedColor else styles.headingColor,
                textAlign = if (level == 1 && styles.h1Centered) TextAlign.Center else null,
                modifier = Modifier.weight(1f),
            )
        }
        if (level == 1 && styles.h1AccentBar) {
            Box(
                Modifier
                    .padding(top = 8.dp)
                    .width(51.2.dp)
                    .height(6.72.dp)
                    .rotate(-1.5f)
                    .background(styles.ruleSolidColor, RoundedCornerShape(99.dp)),
            )
        }
        if (spec.borderBottom) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(styles.headingBottomBorderColor),
            )
        }
    }
}

/** Renders a heading with the theme's left border, which turns blue on hover. */
@Composable
private fun BorderedHeading(level: Int, content: List<MarkdownInline>, styles: MarkdownPreviewStyles) {
    val spec = styles.headingSpec(level)
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val borderColor by animateColorAsState(
        targetValue = if (hovered) styles.themeColor else styles.headingBorderColor,
        animationSpec = tween(durationMillis = 300),
        label = "h2BorderColor",
    )
    // The border is painted with drawBehind because BoxWithConstraints-based text does not
    // support the intrinsic measurements an IntrinsicSize-based border Box would require.
    InlineMarkdownText(
        lines = listOf(content),
        fontSize = spec.size.sp,
        fontWeight = spec.weight,
        lineHeight = spec.size.sp * spec.lineHeightMultiplier,
        color = styles.headingColor,
        modifier = Modifier
            .hoverable(interactionSource)
            .drawBehind {
                drawRect(borderColor, size = Size(spec.borderWidth.toPx(), size.height))
            }
            // Every bordered theme pads 10px after the border; the widget's own padding must also
            // cover the painted border width, which a CSS border would not consume.
            .padding(start = spec.borderWidth + 10.dp)
            .padding(
                top = spec.top.dp,
                bottom = spec.bottom.dp,
            ),
    )
}
