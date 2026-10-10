package com.rocybyte.weisome.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/** Spacing tokens from DESIGN.md, derived from a 4px/8px base unit. */
internal object WeiSomeSpacing {
    val unit = 8.dp
    val gutter = 24.dp
    val margin = 32.dp

    /** Control-internal padding and group spacing step (16px); not part of the stack ladder. */
    val controlPadding = 16.dp
    val stackExtraSmall = 4.dp
    val stackSmall = 12.dp
    val stackMedium = 24.dp
    val stackLarge = 48.dp
    val stackExtraLarge = 80.dp
    val containerMax = 1280.dp
}

/** Shape tokens from DESIGN.md (Rounded Level 2; rem converted at 16px). */
internal object WeiSomeShapes {
    val small = RoundedCornerShape(4.dp)
    val default = RoundedCornerShape(8.dp)
    val medium = RoundedCornerShape(12.dp)
    val large = RoundedCornerShape(16.dp)
    val extraLarge = RoundedCornerShape(24.dp)

    /** Pill shape reserved for chips, tags, and segmented tracks. */
    val full = RoundedCornerShape(50)
}

/** Border metrics from DESIGN.md: every elevated container keeps a thin high-contrast edge. */
internal object WeiSomeBorders {
    val thin = 1.dp

    /** Focus-state border width for input-like controls (DESIGN.md › Input Fields › Focus State). */
    val focusBorder = 2.dp
}

/** Component size tokens from DESIGN.md, derived from recurring metrics across shared widgets. */
internal object WeiSomeSizes {
    /** Inline icon size inside buttons, menu rows, and toolbars. */
    val iconSmall = 16.dp

    /** Emphasized icon size (e.g. the home FAB glyph). */
    val iconMedium = 20.dp

    /** Minimum height of buttons and menu items. */
    val controlHeight = 40.dp

    /** Round floating action button diameter (home FAB and medium icon buttons). */
    val fabSize = 52.dp
}

/** Elevation tokens from DESIGN.md: faint tint-matched glows instead of heavy shadows. */
internal object WeiSomeElevation {
    /** Primary-tinted glow shadow cast by elevated action surfaces. */
    val glow = 8.dp
}
