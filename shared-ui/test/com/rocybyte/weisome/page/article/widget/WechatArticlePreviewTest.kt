package com.rocybyte.weisome.page.article.widget

import androidx.compose.ui.graphics.Color
import com.rocybyte.weisome.article.CodeHighlightSpan
import com.rocybyte.weisome.article.CodeLanguage
import com.rocybyte.weisome.article.MarkdownBlock
import kotlin.test.Test
import kotlin.test.assertEquals

class WechatArticlePreviewTest {
    @Test
    /** Verifies ordinary labels stay atomic while an overlong label is split to fit. */
    fun `splits only code labels wider than the available line`() {
        assertEquals(listOf("launch"), inlineCodeChunks("launch", 8f) { it.length.toFloat() })
        assertEquals(listOf("laun", "ch"), inlineCodeChunks("launch", 4f) { it.length.toFloat() })
        assertEquals(listOf("😀", "x"), inlineCodeChunks("😀x", 1f) { it.length.toFloat() })
    }

    @Test
    /** Verifies Compose text applies the exact RGB ranges carried by the shared code model. */
    fun `uses shared highlight spans in compose code text`() {
        val text = highlightedCodeText(
            MarkdownBlock.CodeBlock(
                language = CodeLanguage.Kotlin,
                code = "fun main()",
                highlights = listOf(CodeHighlightSpan(0, 3, 0xCF222E)),
            ),
        )

        assertEquals("fun main()", text.text)
        assertEquals(0, text.spanStyles.single().start)
        assertEquals(3, text.spanStyles.single().end)
        assertEquals(Color(0xFFCF222E), text.spanStyles.single().item.color)
    }
}
