package com.rocybyte.weisome.page.article.widget

import kotlin.test.Test
import kotlin.test.assertEquals

class InlineTest {
    @Test
    /** Verifies ordinary labels stay atomic while an overlong label is split to fit. */
    fun `splits only code labels wider than the available line`() {
        assertEquals(listOf("launch"), inlineCodeChunks("launch", 8f) { it.length.toFloat() })
        assertEquals(listOf("laun", "ch"), inlineCodeChunks("launch", 4f) { it.length.toFloat() })
        assertEquals(listOf("😀", "x"), inlineCodeChunks("😀x", 1f) { it.length.toFloat() })
    }
}
