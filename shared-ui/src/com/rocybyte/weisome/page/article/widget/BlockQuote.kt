package com.rocybyte.weisome.page.article.widget

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.widget.WeiSomeText

/** Renders a blockquote with the active theme's border, background, and quote marks. */
@Composable
internal fun BlockQuote(block: MarkdownBlock.BlockQuote, nested: Boolean = false) {
    val styles = LocalMarkdownPreviewStyles.current
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val borderColor by animateColorAsState(
        targetValue = if (styles.quoteHasHover && hovered) styles.themeColor else styles.quoteBorder,
        animationSpec = tween(durationMillis = 200),
        label = "quoteBorderColor",
    )
    val verticalMargin = if (nested) styles.quoteNestedVerticalMargin else styles.quoteVerticalMargin
    val bottomMargin = if (nested) (styles.quoteNestedBottomMargin ?: styles.quoteNestedVerticalMargin) else verticalMargin
    val background = if (nested) (styles.quoteNestedBackground ?: styles.quoteBackground) else styles.quoteBackground
    val shadow = if (nested) (styles.quoteNestedShadowElevation ?: styles.quoteShadowElevation) else styles.quoteShadowElevation
    val density = LocalDensity.current
    var openingLayout by remember { mutableStateOf<TextLayoutResult?>(null) }
    // Left margin mirrors the ink-top margin so the mark sits evenly inside the card corner.
    val openingOffsetX = if (styles.quoteHasClosingMark) 6.dp else (styles.quoteMarkInkTop ?: 21.dp)
    // When the mark is ink-anchored, text starts a gap equal to the mark's ink-top margin right
    // of the measured mark ink, keeping the mark-to-text spacing consistent across fonts.
    val startPadding = styles.quoteMarkInkTop?.let { gap ->
        openingLayout?.getBoundingBox(0)?.let { bbox ->
            with(density) { openingOffsetX + (bbox.right - bbox.left).toDp() + gap }
        }
    } ?: styles.quotePaddingStart.dp
    Box(Modifier.padding(top = verticalMargin.dp, bottom = bottomMargin.dp)) {
        // Background and left border are painted with drawBehind because the text inside is
        // a BoxWithConstraints, which does not support the intrinsic measurements that an
        // IntrinsicSize-based border Box would require.
        Box(
            Modifier
                .then(if (styles.quoteHasHover) Modifier.hoverable(interactionSource) else Modifier)
                .shadow(shadow, RoundedCornerShape(styles.quoteCornerRadius))
                .clip(RoundedCornerShape(styles.quoteCornerRadius))
                .heightIn(min = styles.quoteMinHeight)
                .drawBehind {
                    if (styles.quoteHasBackground) drawRect(background)
                    if (styles.quoteBorderWidth > 0.dp) {
                        if (styles.quoteHasBoxBorder) {
                            // CSS borders draw inside the box; inset the stroke by half its
                            // width so the rounded clip keeps the full stroke visible.
                            val inset = styles.quoteBorderWidth / 2
                            drawRoundRect(
                                borderColor,
                                topLeft = Offset(inset.toPx(), inset.toPx()),
                                size = Size(size.width - inset.toPx() * 2, size.height - inset.toPx() * 2),
                                cornerRadius = CornerRadius((styles.quoteCornerRadius - inset).coerceAtLeast(0.dp).toPx()),
                                style = Stroke(styles.quoteBorderWidth.toPx()),
                            )
                        } else {
                            drawRect(borderColor, size = Size(styles.quoteBorderWidth.toPx(), size.height))
                        }
                    }
                }
                .padding(
                    start = startPadding,
                    end = styles.quotePaddingEnd.dp,
                    top = styles.quotePaddingTop.dp,
                    bottom = styles.quotePaddingBottom.dp,
                ),
        ) {
            Column {
                RenderBlocks(block.blocks, inQuote = true)
            }
        }
        if (styles.quoteHasMarks) {
            // Ink-anchored marks position themselves from the measured glyph bounds inside QuoteMark.
            val openingModifier = if (styles.quoteMarkInkTop != null) {
                Modifier
            } else {
                val openingOffsetY = if (styles.quoteHasClosingMark) 4.dp else 2.dp
                Modifier.offset(x = openingOffsetX, y = openingOffsetY)
            }
            QuoteMark(
                styles.quoteOpenMark,
                Modifier.align(Alignment.TopStart).then(openingModifier),
                styles,
                styles.quoteMarkInkTop,
                openingLayout,
            ) { result -> openingLayout = result }
            if (styles.quoteHasClosingMark) {
                QuoteMark(styles.quoteCloseMark, Modifier.align(Alignment.BottomEnd).offset(x = (-8).dp, y = 8.dp), styles)
            }
        }
    }
}

/**
 * Renders one decorative quote mark replacing the theme's ::before/::after pseudo-elements.
 * When [inkTop] is set, the glyph's measured ink top and ink left are both anchored that far
 * inside the card, independent of font metrics; the offset applies one frame after the first
 * layout pass. [layout] and [onLayout] surface the measured text layout so the caller can
 * derive spacing from the mark's ink bounds.
 */
@Composable
private fun QuoteMark(
    mark: String,
    modifier: Modifier = Modifier,
    styles: MarkdownPreviewStyles,
    inkTop: Dp? = null,
    layout: TextLayoutResult? = null,
    onLayout: (TextLayoutResult?) -> Unit = {},
) {
    val density = LocalDensity.current
    val anchoredModifier = if (inkTop != null) {
        val bbox = layout?.getBoundingBox(0)
        if (bbox != null) {
            modifier.offset(
                x = inkTop - with(density) { bbox.left.toDp() },
                y = inkTop - with(density) { bbox.top.toDp() },
            )
        } else {
            modifier
        }
    } else {
        modifier
    }
    WeiSomeText(
        text = mark,
        color = if (styles.quoteMarkColor == androidx.compose.ui.graphics.Color.Unspecified) {
            styles.quoteBorder.copy(alpha = 0.6f)
        } else {
            styles.quoteMarkColor
        },
        fontSize = styles.quoteMarkFontSize,
        fontWeight = styles.quoteMarkWeight,
        lineHeight = styles.quoteMarkFontSize,
        modifier = anchoredModifier,
        onTextLayout = onLayout,
    )
}
