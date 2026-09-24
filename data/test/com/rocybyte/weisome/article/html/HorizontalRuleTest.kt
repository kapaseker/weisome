package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.GitHubExportStyles
import com.rocybyte.weisome.article.HydrogenExportStyles
import kotlin.test.Test
import kotlin.test.assertEquals

class HorizontalRuleTest {
    @Test
    /** Verifies the hydrogen theme renders only the gradient line, without the upstream logo mark. */
    fun `renders hydrogen gradient line`() {
        val html = renderHorizontalRule(HydrogenExportStyles)

        assertEquals(
            "<div style=\"position: relative; width: 98%; height: 1px; border: none; margin: 32px 0; " +
                "background-image: linear-gradient(to right, #dddddd, #999999, #dddddd); overflow: visible;\"></div>",
            html,
        )
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
