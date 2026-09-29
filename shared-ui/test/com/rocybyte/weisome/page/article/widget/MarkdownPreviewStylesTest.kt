package com.rocybyte.weisome.page.article.widget

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rocybyte.weisome.article.MarkdownThemeId
import kotlin.test.Test
import kotlin.test.assertEquals

class MarkdownPreviewStylesTest {
    @Test
    /** Verifies the Rim selector resolves its core typography and color tokens. */
    fun `selects rim preview styles`() {
        val styles = previewStylesFor(MarkdownThemeId.RIM)

        assertEquals(Color(0xFF13202C), styles.bodyColor)
        assertEquals(Color(0xFF3E3282), styles.linkColor)
        assertEquals(18.sp, styles.bodyFontSize)
        assertEquals(30.6.sp, styles.bodyLineHeight)
        assertEquals(34.2.sp, styles.headingFontSize(1))
        assertEquals(FontWeight.Bold, styles.headingFontWeight(1))
    }

    @Test
    /** Verifies the Rim preview keeps the upstream quote, table, and code-frame treatment. */
    fun `maps rim structural styles`() {
        val styles = previewStylesFor(MarkdownThemeId.RIM)

        assertEquals(Color(0xFF4E3E8B), styles.quoteBorder)
        assertEquals(3.dp, styles.quoteBorderWidth)
        assertEquals(Color(0xFFCCCCCC), styles.tableBorderColor)
        assertEquals(1.dp, styles.codeBlockBorderWidth)
        assertEquals(3.dp, styles.inlineCodeCornerRadius)
    }

    @Test
    /** Verifies the Paper preview mirrors the export's nested-quote treatment and mark anchoring. */
    fun `maps paper quote overrides`() {
        val styles = previewStylesFor(MarkdownThemeId.TYPORA_PAPER)

        assertEquals(Color(0xFFFAF6EB), styles.quoteNestedBackground)
        assertEquals(0.dp, styles.quoteNestedShadowElevation)
        assertEquals(0, styles.quoteNestedBottomMargin)
        assertEquals(6.dp, styles.quoteMarkInkTop)
        assertEquals(Color(0xFFE8E0CF), styles.inlineCodeBorderColor)
        assertEquals(37.dp, styles.quoteMinHeight)
        assertEquals(FontWeight.Bold, styles.quoteMarkWeight)
    }

    @Test
    /** Verifies the Cyanosis selector resolves its core typography and color tokens. */
    fun `selects cyanosis preview styles`() {
        val styles = previewStylesFor(MarkdownThemeId.CYANOSIS)

        assertEquals(Color(0xFF353535), styles.bodyColor)
        assertEquals(Color(0xFF005BB7), styles.headingColor)
        assertEquals(Color(0xFF3DA8F5), styles.linkColor)
        assertEquals(Color(0xFFC2185B), styles.inlineCodeColor)
        assertEquals(14.sp, styles.bodyFontSize)
        assertEquals(24.5.sp, styles.bodyLineHeight)
        assertEquals(24.sp, styles.headingFontSize(2))
        assertEquals(FontWeight.Bold, styles.headingFontWeight(2))
    }

    @Test
    /** Verifies the Cyanosis preview keeps the upstream quote, table, and rule treatment. */
    fun `maps cyanosis structural styles`() {
        val styles = previewStylesFor(MarkdownThemeId.CYANOSIS)

        assertEquals(Color(0xFF2196F3), styles.quoteBorder)
        assertEquals(4.dp, styles.quoteBorderWidth)
        assertEquals(Color(0xFFC3E0FD), styles.tableBorderColor)
        assertEquals(Color(0xFFDFF0FF), styles.tableHeaderBackground)
        assertEquals(1.dp, styles.ruleHeight)
        assertEquals(true, styles.ruleIsGradient)
        assertEquals(2.dp, styles.inlineCodeCornerRadius)
    }
}
