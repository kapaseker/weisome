package com.rocybyte.weisome.repository.code

import com.rocybyte.weisome.article.CodeLanguage
import com.rocybyte.weisome.article.WeiSomeLightCodeTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CodeHighlightRepositoryTest {
    @Test
    /** Verifies Java, Kotlin, and Rust keywords receive the shared keyword color. */
    fun `highlights keywords in all supported languages`() {
        val repository = CodeHighlightRepository()

        assertEquals(
            WeiSomeLightCodeTheme.keywordRgb,
            colorAt("public class Example {}", "public", repository, CodeLanguage.Java),
        )
        assertEquals(
            WeiSomeLightCodeTheme.keywordRgb,
            colorAt("fun main() = Unit", "fun", repository, CodeLanguage.Kotlin),
        )
        assertEquals(
            WeiSomeLightCodeTheme.keywordRgb,
            colorAt("fn main() {}", "fn", repository, CodeLanguage.Rust),
        )
    }

    @Test
    /** Verifies adapter output is ordered, bounded, and free of overlapping ranges. */
    fun `normalizes dependency highlights for shared renderers`() {
        val code = "// comment\nval answer = 42"
        val highlights = CodeHighlightRepository().highlight(CodeLanguage.Kotlin, code)

        assertTrue(highlights.all { span -> span.start >= 0 && span.endExclusive <= code.length })
        assertTrue(highlights.zipWithNext().all { (first, second) -> first.endExclusive <= second.start })
    }

    /** Returns the resolved highlight color at the start of a selected source phrase. */
    private fun colorAt(
        code: String,
        phrase: String,
        repository: CodeHighlightRepository,
        language: CodeLanguage,
    ): Int? {
        val index = code.indexOf(phrase)
        return repository.highlight(language, code)
            .firstOrNull { span -> index in span.start until span.endExclusive }
            ?.foregroundRgb
    }
}
