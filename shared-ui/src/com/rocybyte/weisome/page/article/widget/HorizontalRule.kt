package com.rocybyte.weisome.page.article.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

/** Renders a horizontal rule with the active theme's gradient line or solid bar. */
@Composable
internal fun HorizontalRule() {
    val styles = LocalMarkdownPreviewStyles.current
    if (!styles.ruleIsGradient) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
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
