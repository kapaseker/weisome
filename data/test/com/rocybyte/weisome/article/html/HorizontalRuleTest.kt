package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.GitHubExportStyles
import com.rocybyte.weisome.article.HydrogenExportStyles
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HorizontalRuleTest {
    @Test
    /** Verifies the rule renders the gradient line with the centered juejin logo. */
    fun `renders gradient line with centered logo`() {
        val html = renderHorizontalRule(HydrogenExportStyles)

        assertTrue(html.startsWith("<div style=\"position: relative; width: 98%; height: 1px; border: none; margin: 32px 0;"))
        assertTrue(html.contains("linear-gradient(to right, #dddddd, #999999, #dddddd)"))
        assertTrue(html.contains("width: 60px; height: 20px;"))
        assertTrue(html.contains("data:image/png;base64,"))
    }

    @Test
    /** Verifies the GitHub theme renders the solid rule bar without any logo decoration. */
    fun `renders github solid rule without logo`() {
        assertEquals(
            "<div style=\"height: 0.25em; padding: 0; margin: 24px 0; background-color: #d1d9e0; border: 0;\"></div>",
            renderHorizontalRule(GitHubExportStyles),
        )
    }
}
