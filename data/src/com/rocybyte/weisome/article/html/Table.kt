package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.WechatArticleStyles

/** Renders a GFM table with the striped body rows hydrogen styles via nth-child. */
internal fun renderTable(block: MarkdownBlock.Table): String {
    val header = block.header.joinToString("") { cell ->
        "<th style=\"${WechatArticleStyles.thCss}\">${renderInline(cell, imageShadow = false)}</th>"
    }
    val rows = block.rows.mapIndexed { index, row ->
        val cellCss = if (index % 2 == 1) WechatArticleStyles.stripedTdCss else WechatArticleStyles.tdCss
        val cells = row.joinToString("") { cell ->
            "<td style=\"$cellCss\">${renderInline(cell, imageShadow = false)}</td>"
        }
        "<tr>$cells</tr>"
    }.joinToString("")
    return "<table style=\"${WechatArticleStyles.tableCss}\"><thead><tr>$header</tr></thead><tbody>$rows</tbody></table>"
}
