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
}
