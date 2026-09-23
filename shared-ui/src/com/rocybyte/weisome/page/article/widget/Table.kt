package com.rocybyte.weisome.page.article.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rocybyte.weisome.article.MarkdownBlock

/** Renders a GFM table with the active theme's outer border, header fill, and striped rows. */
@Composable
internal fun Table(block: MarkdownBlock.Table) {
    val styles = LocalMarkdownPreviewStyles.current
    val header = block.header
    val rows = block.rows
    val columns = header.size
    Column(
        modifier = Modifier.border(width = styles.tableBorderWidth, color = styles.tableBorderColor),
    ) {
        Row(
            modifier = Modifier.background(styles.tableHeaderBackground),
        ) {
            header.forEach { cell ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(
                            horizontal = styles.tableCellPaddingHorizontal.dp,
                            vertical = styles.tableCellPaddingVertical.dp,
                        ),
                ) {
                    InlineMarkdownText(
                        lines = listOf(cell),
                        fontSize = styles.tableFontSize,
                        lineHeight = styles.tableLineHeight,
                        fontWeight = styles.tableHeaderFontWeight,
                        color = styles.tableHeaderColor,
                    )
                }
            }
        }
        rows.forEachIndexed { index, row ->
            // ponytail: equal-weight columns replace CSS auto layout; per-column auto widths are not enforced.
            val rowBackground = if (index % 2 == 1) styles.tableStripeBackground else null
            Row(
                modifier = Modifier.then(
                    if (rowBackground != null) Modifier.background(rowBackground) else Modifier,
                ),
            ) {
                repeat(columns) { columnIndex ->
                    val cell = row.getOrNull(columnIndex) ?: emptyList()
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(
                                horizontal = styles.tableCellPaddingHorizontal.dp,
                                vertical = styles.tableCellPaddingVertical.dp,
                            ),
                    ) {
                        InlineMarkdownText(
                            lines = listOf(cell),
                            fontSize = styles.tableFontSize,
                            lineHeight = styles.tableLineHeight,
                            color = styles.bodyColor,
                        )
                    }
                }
            }
        }
    }
}
