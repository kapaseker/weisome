package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.GitHubExportStyles
import com.rocybyte.weisome.article.HydrogenExportStyles
import com.rocybyte.weisome.article.TyporaPaperExportStyles
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

    @Test
    /** Verifies Paper's short yellow rule keeps its blue and red offset accents. */
    fun `renders typora paper accent rule`() {
        assertEquals(
            "<div style=\"width: 42%; height: 5px; margin: 51.2px auto; border: 0; border-radius: 50%; " +
                "background: #f4d758; box-shadow: 14px 0 0 #2b7fd8, -14px 0 0 #e84a5f; opacity: 0.78;\"></div>",
            renderHorizontalRule(TyporaPaperExportStyles),
        )
    }
}
