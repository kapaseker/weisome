package com.rocybyte.weisome.page.article.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Renders a horizontal rule with hydrogen's gradient line and centered juejin logo. */
@Composable
internal fun HorizontalRule() {
    val styles = WechatArticlePreviewStyles
    val logo = remember { HydrogenAssets.decodeBase64Image(HydrogenAssets.juejinLogoBase64) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .fillMaxWidth(0.98f)
                .height(1.dp)
                .background(Brush.horizontalGradient(styles.ruleGradient)),
        )
        if (logo != null) {
            Box(
                modifier = Modifier
                    .size(width = 60.dp, height = 20.dp)
                    .background(Color.White),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    bitmap = logo,
                    contentDescription = null,
                    modifier = Modifier
                        .height(20.dp)
                        .aspectRatio(logo.width.toFloat() / logo.height),
                )
            }
        }
    }
}
