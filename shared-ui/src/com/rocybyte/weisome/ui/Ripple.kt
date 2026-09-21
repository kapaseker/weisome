package com.rocybyte.weisome.ui

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.createRippleModifierNode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.unit.Dp

/**
 * 按压反馈：DESIGN.md 未定义 ripple，按 M3 默认规格落地——
 * bounded ripple + 内容色低透明度叠层。
 * material-ripple 官方路径：自定义设计系统用 createRippleModifierNode 构建自有 Indication。
 * [WeiSomeTheme] 将其 provide 为全局 LocalIndication 默认值；深色底需显式传 onPrimary 覆盖。
 */
private val WeiSomeRippleAlpha = RippleAlpha(
    pressedAlpha = 0.12f,
    focusedAlpha = 0.12f,
    hoveredAlpha = 0.08f,
    draggedAlpha = 0.12f,
)

/** [IndicationNodeFactory] 绘制内容色低透明度 bounded ripple。 */
private class WeiSomeRipple(private val color: Color) : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode =
        createRippleModifierNode(
            interactionSource = interactionSource,
            bounded = true,
            radius = Dp.Unspecified,
            color = ColorProducer { color },
            rippleAlpha = { WeiSomeRippleAlpha },
        )

    override fun equals(other: Any?): Boolean = other is WeiSomeRipple && other.color == color
    override fun hashCode(): Int = color.hashCode()
}

/** 深色底（如主按钮）传 onPrimary，浅底传 onSurface。 */
@Composable
internal fun weiSomeRipple(color: Color = WeiSomeColors.onSurface): Indication =
    remember(color) { WeiSomeRipple(color) }
