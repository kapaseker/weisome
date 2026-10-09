package dev.snipme.highlights.internal.locator

import dev.snipme.highlights.internal.SyntaxTokens.TOKEN_DELIMITERS
import dev.snipme.highlights.internal.indicesOf
import dev.snipme.highlights.internal.isIndependentPhrase
import dev.snipme.highlights.model.PhraseLocation

internal object FunctionCallLocator {

    /**
     * Locates function-call identifiers by lexical heuristics.
     *
     * A token qualifies when it starts with a lowercase letter, is an independent phrase in the
     * code, is not a language keyword, does not fall inside any ignored range (strings and
     * comments), and is either directly followed by `(` or `{` (an invocation like `launch(` or a
     * trailing-lambda call like `launch {`), or directly preceded by `::` (a callable reference
     * like `stateStore::updateArticles`).
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
            .filter { it.first().isLowerCase() }
            .forEach { token ->
                code.indicesOf(token)
                    .filter { token.isIndependentPhrase(code, it) }
                    .filterNot { index -> ignoreRanges.any { index in it } }
                    .forEach { index ->
                        // Look past whitespace so both `launch(` and trailing-lambda `launch {` match.
                        val nextChar = code.drop(index + token.length)
                            .firstOrNull { !it.isWhitespace() }
                        val isInvoked = nextChar == '(' || nextChar == '{'
                        val isReference = code.getOrNull(index - 1) == ':' &&
                                code.getOrNull(index - 2) == ':'
                        if (isInvoked || isReference) {
                            locations.add(PhraseLocation(index, index + token.length))
                        }
                    }
            }

        return locations.toSet()
    }
}
