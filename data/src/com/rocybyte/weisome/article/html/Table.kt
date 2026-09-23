package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownExportStyles

/** Renders a GFM table; even body rows carry the striped background inline CSS cannot express. */
internal fun renderTable(block: MarkdownBlock.Table, styles: MarkdownExportStyles): String {
    val header = block.header.joinToString("") { cell ->
        "<th style=\"${styles.thCss}\">${renderInline(cell, styles, imageShadow = false)}</th>"
    }
    val rows = block.rows.mapIndexed { index, row ->
        val cellCss = if (index % 2 == 1) styles.stripedTdCss else styles.tdCss
        val cells = row.joinToString("") { cell ->
            "<td style=\"$cellCss\">${renderInline(cell, styles, imageShadow = false)}</td>"
        }
        "<tr>$cells</tr>"
    }.joinToString("")
    return "<table style=\"${styles.tableCss}\"><thead><tr>$header</tr></thead><tbody>$rows</tbody></table>"
}
