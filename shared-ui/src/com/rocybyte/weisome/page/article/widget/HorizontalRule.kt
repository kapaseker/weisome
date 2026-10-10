package com.rocybyte.weisome.page.article.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rocybyte.weisome.widget.WeiSomeText

/** Renders a horizontal rule with the active theme's gradient line or solid bar. */
@Composable
internal fun HorizontalRule() {
    val styles = LocalMarkdownPreviewStyles.current
    val glyph = styles.ruleCenterGlyph
    if (glyph != null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = styles.ruleVerticalMargin.dp, bottom = styles.ruleVerticalMargin.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(Modifier.fillMaxWidth().height(styles.ruleHeight).background(styles.ruleSolidColor))
            // ponytail: the masking background assumes the preview surface is white, mirroring
            // the export's --paper token; revisit if the preview ever gains a tinted canvas.
            WeiSomeText(
                text = glyph,
                fontSize = 12.sp,
                lineHeight = 12.sp,
                color = styles.themeColor,
                modifier = Modifier
                    .background(Color.White)
                    .padding(horizontal = 10.dp),
            )
        }
        return
    }
    if (styles.ruleHasPaperAccents) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = styles.ruleVerticalMargin.dp, bottom = styles.ruleVerticalMargin.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                Modifier
                    .fillMaxWidth(styles.ruleWidthFraction)
                    .height(styles.ruleHeight)
                    .graphicsLayer(alpha = 0.78f)
                    .drawBehind {
                        val corner = CornerRadius(size.height / 2f, size.height / 2f)
                        drawRoundRect(
                            color = styles.unorderedMarkerColor,
                            topLeft = Offset(x = (-14).dp.toPx(), y = 0f),
                            size = androidx.compose.ui.geometry.Size(size.width, size.height),
                            cornerRadius = corner,
                        )
                        drawRoundRect(
                            color = styles.themeColor,
                            topLeft = Offset(x = 14.dp.toPx(), y = 0f),
                            size = androidx.compose.ui.geometry.Size(size.width, size.height),
                            cornerRadius = corner,
                        )
                        drawRoundRect(color = styles.ruleSolidColor, cornerRadius = corner)
                    },
            )
        }
        return
    }
    if (!styles.ruleIsGradient) {
        Box(
            modifier = Modifier
                .fillMaxWidth(styles.ruleWidthFraction)
                .padding(top = styles.ruleVerticalMargin.dp, bottom = styles.ruleVerticalMargin.dp)
                .height(styles.ruleHeight)
                .background(styles.ruleSolidColor),
        )
        return
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = styles.ruleVerticalMargin.dp, bottom = styles.ruleVerticalMargin.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .fillMaxWidth(0.98f)
                .height(styles.ruleHeight)
                .background(Brush.horizontalGradient(styles.ruleGradient)),
        )
    }
}
