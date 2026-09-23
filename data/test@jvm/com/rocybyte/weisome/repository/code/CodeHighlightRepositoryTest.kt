package com.rocybyte.weisome.repository.code

import com.rocybyte.weisome.article.CodeLanguage
import com.rocybyte.weisome.article.CodeThemeId
import com.rocybyte.weisome.article.CodeThemes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CodeHighlightRepositoryTest {
    @Test
    /** Verifies Java, Kotlin, and Rust keywords receive the shared keyword color. */
    fun `highlights keywords in all supported languages`() {
        val repository = CodeHighlightRepository()
        val githubLight = CodeThemes.forId(CodeThemeId.GITHUB_LIGHT)

        assertEquals(
            githubLight.keywordRgb,
            colorAt("public class Example {}", "public", repository, CodeLanguage.Java),
        )
        assertEquals(
            githubLight.keywordRgb,
            colorAt("fun main() = Unit", "fun", repository, CodeLanguage.Kotlin),
        )
        assertEquals(
            githubLight.keywordRgb,
            colorAt("fn main() {}", "fn", repository, CodeLanguage.Rust),
        )
    }

    @Test
    /** Verifies the palette switch produces theme-specific keyword colors. */
    fun `applies the selected theme palette`() {
        val repository = CodeHighlightRepository()

        assertEquals(
            CodeThemes.forId(CodeThemeId.DARCULA).keywordRgb,
            colorAt("fun main() = Unit", "fun", repository, CodeLanguage.Kotlin, CodeThemeId.DARCULA),
        )
        assertEquals(
            CodeThemes.forId(CodeThemeId.ATOM_ONE).keywordRgb,
            colorAt("fun main() = Unit", "fun", repository, CodeLanguage.Kotlin, CodeThemeId.ATOM_ONE),
        )
    }

    @Test
    /** Verifies adapter output is ordered, bounded, and free of overlapping ranges. */
    fun `normalizes dependency highlights for shared renderers`() {
        val code = "// comment\nval answer = 42"
        val highlights = CodeHighlightRepository().highlight(CodeLanguage.Kotlin, code, CodeThemeId.GITHUB_LIGHT)

        assertTrue(highlights.all { span -> span.start >= 0 && span.endExclusive <= code.length })
        assertTrue(highlights.zipWithNext().all { (first, second) -> first.endExclusive <= second.start })
    }

    /** Returns the resolved highlight color at the start of a selected source phrase. */
    private fun colorAt(
        code: String,
        phrase: String,
        repository: CodeHighlightRepository,
        language: CodeLanguage,
        theme: CodeThemeId = CodeThemeId.GITHUB_LIGHT,
    ): Int? {
        val index = code.indexOf(phrase)
        return repository.highlight(language, code, theme)
            .firstOrNull { span -> index in span.start until span.endExclusive }
            ?.foregroundRgb
    }
}
