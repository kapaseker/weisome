package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.HydrogenExportStyles
import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownInline
import com.rocybyte.weisome.article.RimExportStyles
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TableTest {
    @Test
    /** Verifies the table renders the outer border and styled header cells. */
    fun `renders header cells with hydrogen styles`() {
        val html = renderTable(table("a", "b", "1", "2"), HydrogenExportStyles)

        assertTrue(html.startsWith("<table style=\"margin: 0 auto 10px; font-size: 12px; width: auto; max-width: 100%; overflow: auto; border: 2px solid #c6c6c6;\">"))
        assertTrue(
            html.contains(
                "<th style=\"background: #f6f6f6; color: #000000; text-align: left; padding: 12px 7px; line-height: 24px; font-size: 12px;\">a</th>",
            ),
        )
    }

    @Test
    /** Verifies only even body rows carry the striped background across all their cells. */
    fun `stripes only even body rows`() {
        val html = renderTable(table("a", "b", "1", "2", "3"), HydrogenExportStyles)

        val stripedCount = Regex("background: #fcfcfc;").findAll(html).count()
        val plainCount = Regex("padding: 12px 7px; line-height: 24px; font-size: 12px; min-width: 120px;\">").findAll(html).count()

        // Three two-column rows: the middle row stripes both cells, the other four stay plain.
        assertEquals(2, stripedCount)
        assertEquals(4, plainCount)
    }

    @Test
    /** Verifies Rim tables keep the compact font and understated outer border. */
    fun `renders rim table with a compact outer border`() {
        val html = renderTable(table("a", "b", "1", "2"), RimExportStyles)

        assertTrue(html.contains("font-size: 14.4px;"))
        assertTrue(html.contains("font-weight: 700;"))
        assertEquals(1, Regex("border: 1px solid #cccccc;").findAll(html).count())
    }

    /** Builds a two-column table from alternating header and row cell texts. */
    private fun table(headerLeft: String, headerRight: String, vararg rows: String): MarkdownBlock.Table {
        val header = listOf(
            listOf(MarkdownInline.Text(headerLeft)),
            listOf(MarkdownInline.Text(headerRight)),
        )
        val body = rows.map { listOf(listOf(MarkdownInline.Text(it)), listOf(MarkdownInline.Text(it))) }
        return MarkdownBlock.Table(header, body)
    }
}
