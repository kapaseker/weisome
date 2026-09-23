package com.rocybyte.weisome.repository.code

import com.rocybyte.weisome.article.CodeHighlightSpan
import com.rocybyte.weisome.article.CodeLanguage
import com.rocybyte.weisome.article.CodeTheme
import com.rocybyte.weisome.article.CodeThemeId
import com.rocybyte.weisome.article.CodeThemes
import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.ColorHighlight
import dev.snipme.highlights.model.SyntaxLanguage
import dev.snipme.highlights.model.SyntaxTheme

internal class CodeHighlightRepository : CodeHighlightRepo {
    /** Highlights source code and collapses third-party results into stable renderer spans. */
    override fun highlight(language: CodeLanguage, code: String, theme: CodeThemeId): List<CodeHighlightSpan> {
        if (code.isEmpty()) return emptyList()
        val palette = CodeThemes.forId(theme)
        val highlights = Highlights.Builder()
            .code(code)
            .language(language.toSyntaxLanguage())
            .theme(palette.toSyntaxTheme(theme))
            .build()
            .getHighlights()
            .filterIsInstance<ColorHighlight>()

        return normalize(code.length, palette.codeRgb, highlights)
    }

    /** Applies highlights in dependency order and returns sorted, non-overlapping colored ranges. */
    private fun normalize(codeLength: Int, defaultColorRgb: Int, highlights: List<ColorHighlight>): List<CodeHighlightSpan> {
        val colors = IntArray(codeLength) { defaultColorRgb }
        highlights.forEach { highlight ->
            val start = highlight.location.start.coerceIn(0, codeLength)
            val endExclusive = highlight.location.end.coerceIn(start, codeLength)
            for (index in start until endExclusive) {
                colors[index] = highlight.rgb
            }
        }

        return buildList {
            var start = 0
            while (start < codeLength) {
                val color = colors[start]
                var endExclusive = start + 1
                while (endExclusive < codeLength && colors[endExclusive] == color) {
                    endExclusive++
                }
                if (color != defaultColorRgb) {
                    add(CodeHighlightSpan(start, endExclusive, color))
                }
                start = endExclusive
            }
        }
    }

    /** Maps the application language model to the Highlights dependency model. */
    private fun CodeLanguage.toSyntaxLanguage(): SyntaxLanguage = when (this) {
        CodeLanguage.Java -> SyntaxLanguage.JAVA
        CodeLanguage.Kotlin -> SyntaxLanguage.KOTLIN
        CodeLanguage.Rust -> SyntaxLanguage.RUST
    }

    /** Converts the shared application theme to the dependency's syntax theme. */
    private fun CodeTheme.toSyntaxTheme(id: CodeThemeId): SyntaxTheme = SyntaxTheme(
        key = id.name.lowercase(),
        code = codeRgb,
        keyword = keywordRgb,
        string = stringRgb,
        literal = literalRgb,
        comment = commentRgb,
        metadata = metadataRgb,
        multilineComment = multilineCommentRgb,
        punctuation = punctuationRgb,
        mark = markRgb,
    )
}
