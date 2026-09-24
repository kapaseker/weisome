package com.rocybyte.weisome.page.article.widget

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
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
    Box(Modifier.padding(top = verticalMargin.dp, bottom = verticalMargin.dp)) {
        // Background and left border are painted with drawBehind because the text inside is
        // a BoxWithConstraints, which does not support the intrinsic measurements that an
        // IntrinsicSize-based border Box would require.
        Box(
            Modifier
                .then(if (styles.quoteHasHover) Modifier.hoverable(interactionSource) else Modifier)
                .shadow(styles.quoteShadowElevation, RoundedCornerShape(styles.quoteCornerRadius))
                .clip(RoundedCornerShape(styles.quoteCornerRadius))
                .drawBehind {
                    if (styles.quoteHasBackground) drawRect(styles.quoteBackground)
                    if (styles.quoteBorderWidth > 0.dp) {
                        drawRect(borderColor, size = Size(styles.quoteBorderWidth.toPx(), size.height))
                    }
                }
                .padding(
                    start = styles.quotePaddingStart.dp,
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
            val openingOffsetX = if (styles.quoteHasClosingMark) 6.dp else 21.dp
            val openingOffsetY = if (styles.quoteHasClosingMark) 4.dp else 2.dp
            QuoteMark("\u201C", Modifier.align(Alignment.TopStart).offset(x = openingOffsetX, y = openingOffsetY), styles)
            if (styles.quoteHasClosingMark) {
                QuoteMark("\u201D", Modifier.align(Alignment.BottomEnd).offset(x = (-8).dp, y = 8.dp), styles)
            }
        }
    }
}

/** Renders one decorative quote mark replacing the theme's ::before/::after pseudo-elements. */
@Composable
private fun QuoteMark(mark: String, modifier: Modifier = Modifier, styles: MarkdownPreviewStyles) {
    WeiSomeText(
        text = mark,
        color = if (styles.quoteMarkColor == androidx.compose.ui.graphics.Color.Unspecified) {
            styles.quoteBorder.copy(alpha = 0.6f)
        } else {
            styles.quoteMarkColor
        },
        fontSize = styles.quoteMarkFontSize,
        fontWeight = FontWeight.ExtraBold,
        lineHeight = styles.quoteMarkFontSize,
        modifier = modifier,
    )
}
