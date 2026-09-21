package com.rocybyte.weisome.widget

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.rocybyte.weisome.ui.WeiSomeColors

/** Indeterminate circular progress indicator with a rotating primary arc. */
@Composable
internal fun WeiSomeCircularProgressIndicator(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "progressRotation")
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 900, easing = LinearEasing)),
        label = "progressAngle",
    )

    Canvas(modifier = modifier.size(32.dp)) {
        val stroke = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        val arcSize = Size(size.width - stroke.width, size.height - stroke.width)
        val topLeft = Offset(stroke.width / 2f, stroke.width / 2f)
        rotate(angle) {
            drawArc(
                color = WeiSomeColors.primary,
                startAngle = -90f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke,
            )
        }
    }
}
