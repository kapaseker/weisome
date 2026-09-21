package com.rocybyte.weisome.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/** Spacing tokens from DESIGN.md, derived from a 4px/8px base unit. */
internal object WeiSomeSpacing {
    val unit = 8.dp
    val gutter = 24.dp
    val margin = 32.dp
    val stackXs = 4.dp
    val stackSm = 12.dp
    val stackMd = 24.dp
    val stackLg = 48.dp
    val stackXl = 80.dp
    val containerMax = 1280.dp
}

/** Shape tokens from DESIGN.md (Rounded Level 2; rem converted at 16px). */
internal object WeiSomeShapes {
    val sm = RoundedCornerShape(4.dp)
    val default = RoundedCornerShape(8.dp)
    val md = RoundedCornerShape(12.dp)
    val lg = RoundedCornerShape(16.dp)
    val xl = RoundedCornerShape(24.dp)

    /** Pill shape reserved for chips, tags, and segmented tracks. */
    val full = RoundedCornerShape(50)
}

/** Border metrics from DESIGN.md: every elevated container keeps a thin high-contrast edge. */
internal object WeiSomeBorders {
    val thin = 1.dp
}
