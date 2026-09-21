package com.rocybyte.weisome.article.html

import kotlin.test.Test
import kotlin.test.assertTrue

class HorizontalRuleTest {
    @Test
    /** Verifies the rule renders the gradient line with the centered juejin logo. */
    fun `renders gradient line with centered logo`() {
        val html = renderHorizontalRule()

        assertTrue(html.startsWith("<div style=\"position: relative; width: 98%; height: 1px; border: none; margin: 32px 0;"))
        assertTrue(html.contains("linear-gradient(to right, #dddddd, #999999, #dddddd)"))
        assertTrue(html.contains("width: 60px; height: 20px;"))
        assertTrue(html.contains("data:image/png;base64,"))
    }
}
