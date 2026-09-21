package com.rocybyte.weisome.page.article.widget

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownInline
import com.rocybyte.weisome.widget.WeiSomeText

/** Renders a heading block with the hydrogen typography, prefix, and border for its level. */
@Composable
internal fun Heading(block: MarkdownBlock.Heading) {
    if (WechatArticlePreviewStyles.headingHasBorder(block.level)) {
        BorderedHeading(block)
    } else {
        PlainHeading(block)
    }
}

/** Renders a heading without a border, prefixing level-one headings with the blue "#". */
@Composable
private fun PlainHeading(block: MarkdownBlock.Heading) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.padding(
            top = WechatArticlePreviewStyles.headingTopMargin(block.level),
            bottom = WechatArticlePreviewStyles.headingBottomMargin(block.level),
        ),
    ) {
        if (block.level == 1) {
            WeiSomeText(
                text = "#",
                color = WechatArticlePreviewStyles.themeColor,
                fontSize = WechatArticlePreviewStyles.headingFontSize(block.level),
                fontWeight = WechatArticlePreviewStyles.headingFontWeight(block.level),
                lineHeight = WechatArticlePreviewStyles.headingFontSize(block.level) * 1.5f,
            )
            Spacer(Modifier.width(10.dp))
        }
        InlineMarkdownText(
            lines = listOf(capitalized(block)),
            fontSize = WechatArticlePreviewStyles.headingFontSize(block.level),
            fontWeight = WechatArticlePreviewStyles.headingFontWeight(block.level),
            lineHeight = WechatArticlePreviewStyles.headingFontSize(block.level) * 1.5f,
            color = WechatArticlePreviewStyles.bodyColor,
            modifier = Modifier.weight(1f),
        )
    }
}

/** Renders a level-two heading with the grey left border that turns blue on hover. */
@Composable
private fun BorderedHeading(block: MarkdownBlock.Heading) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val borderColor by animateColorAsState(
        targetValue = if (hovered) {
            WechatArticlePreviewStyles.themeColor
        } else {
            WechatArticlePreviewStyles.headingBorderColor
        },
        animationSpec = tween(durationMillis = 300),
        label = "h2BorderColor",
    )
    // The border is painted with drawBehind because BoxWithConstraints-based text does not
    // support the intrinsic measurements an IntrinsicSize-based border Box would require.
    InlineMarkdownText(
        lines = listOf(capitalized(block)),
        fontSize = WechatArticlePreviewStyles.headingFontSize(block.level),
        fontWeight = WechatArticlePreviewStyles.headingFontWeight(block.level),
        lineHeight = WechatArticlePreviewStyles.headingFontSize(block.level) * 1.5f,
        color = WechatArticlePreviewStyles.bodyColor,
        modifier = Modifier
            .hoverable(interactionSource)
            .drawBehind {
                drawRect(borderColor, size = Size(5.dp.toPx(), size.height))
            }
            .padding(start = 15.dp)
            .padding(
                top = WechatArticlePreviewStyles.headingTopMargin(block.level),
                bottom = WechatArticlePreviewStyles.headingBottomMargin(block.level) + 5.dp,
            ),
    )
}

/** Applies hydrogen's first-letter rule to heading levels two and three. */
private fun capitalized(block: MarkdownBlock.Heading): List<MarkdownInline> =
    if (block.level in 2..3) capitalizeFirstLetter(block.content) else block.content
