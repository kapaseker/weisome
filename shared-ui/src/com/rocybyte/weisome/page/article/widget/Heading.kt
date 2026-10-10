package com.rocybyte.weisome.page.article.widget

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.offset
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
        Modifier.padding(top = spec.top.dp, bottom = spec.bottom.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            // Yu renders an animal emoji before each heading, inside the 50px icon gutter.
            if (spec.prefixEmoji.isNotEmpty()) {
                WeiSomeText(
                    text = spec.prefixEmoji,
                    color = styles.headingColor,
                    fontSize = spec.prefixEmojiSize.sp,
                    lineHeight = spec.prefixEmojiSize.sp,
                    modifier = Modifier
                        .width(50.dp)
                        .offset(x = spec.prefixEmojiLeft.dp),
                )
            }
            // Chocolate renders its piece icon before h1/h2 text, sized to the heading font.
            styles.iconFor(level)?.let { icon ->
                Image(
                    bitmap = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(spec.size.dp)
                        .align(Alignment.CenterVertically),
                )
                Spacer(Modifier.width(6.dp))
            }
            if (level == 1 && styles.h1HasPrefix) {
                WeiSomeText(
                    text = "#",
                    color = styles.themeColor,
                    fontSize = spec.size.sp,
                    fontWeight = spec.weight,
                    lineHeight = spec.lineHeight ?: (spec.size.sp * spec.lineHeightMultiplier),
                )
                Spacer(Modifier.width(10.dp))
            }
            if (level == 2 && styles.h2AccentDots) {
                // Paper's dot pair keeps its 23px reference geometry, scaled to the heading font size
                // so the icon matches the text; CenterVertically lifts it off the Row's top edge to
                // sit against the glyphs inside the taller line box.
                Box(
                    Modifier
                        .align(Alignment.CenterVertically)
                        .size(spec.size.dp)
                        .drawBehind {
                            val unit = size.width / 23f
                            drawCircle(
                                color = styles.ruleSolidColor,
                                radius = 7.5f * unit,
                                center = androidx.compose.ui.geometry.Offset(15.5f * unit, 14.5f * unit),
                            )
                            drawCircle(
                                color = styles.themeColor,
                                radius = 7.5f * unit,
                                center = androidx.compose.ui.geometry.Offset(7.5f * unit, 7.5f * unit),
                            )
                        },
                )
                Spacer(Modifier.width(8.dp))
            }
            InlineMarkdownText(
                lines = listOf(content),
                fontSize = spec.size.sp,
                fontWeight = spec.weight,
                lineHeight = spec.lineHeight ?: (spec.size.sp * spec.lineHeightMultiplier),
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
            // The export paints the border below its padding-bottom, so the gap precedes the line.
            Spacer(Modifier.height(spec.borderBottomGap))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(spec.borderBottomWidth)
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
        lineHeight = spec.lineHeight ?: (spec.size.sp * spec.lineHeightMultiplier),
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
