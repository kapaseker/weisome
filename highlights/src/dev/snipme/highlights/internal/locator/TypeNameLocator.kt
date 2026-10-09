package dev.snipme.highlights.internal.locator

import dev.snipme.highlights.internal.SyntaxTokens.TOKEN_DELIMITERS
import dev.snipme.highlights.internal.indicesOf
import dev.snipme.highlights.internal.isIndependentPhrase
import dev.snipme.highlights.model.PhraseLocation

internal object TypeNameLocator {

    /**
     * Locates type-name identifiers by the uppercase-first heuristic.
     *
     * A token qualifies when it starts with an uppercase letter, is an independent phrase in the
     * code, is not a language keyword, and does not fall inside any ignored range (strings and
     * comments). Constructor usages like `CoroutineScope(` are included so calls read as types.
     */
    fun locate(
        code: String,
        keywords: Set<String>,
        ignoreRanges: Set<IntRange> = emptySet(),
    ): Set<PhraseLocation> {
        val locations = mutableSetOf<PhraseLocation>()

        code.split(*TOKEN_DELIMITERS.toTypedArray())
            .asSequence()
            .filter { it.isNotBlank() }
            .filter { it !in keywords }
            .filter { it.first().isUpperCase() }
            .forEach { token ->
                code.indicesOf(token)
                    .filter { token.isIndependentPhrase(code, it) }
                    .filterNot { index -> ignoreRanges.any { index in it } }
                    .forEach { index ->
                        locations.add(PhraseLocation(index, index + token.length))
                    }
            }

        return locations.toSet()
    }
}
