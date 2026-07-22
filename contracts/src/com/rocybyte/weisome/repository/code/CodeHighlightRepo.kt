package com.rocybyte.weisome.repository.code

import com.rocybyte.weisome.article.CodeHighlightSpan
import com.rocybyte.weisome.article.CodeLanguage

interface CodeHighlightRepo {
    /** Produces normalized, non-overlapping color spans for supported source code. */
    fun highlight(language: CodeLanguage, code: String): List<CodeHighlightSpan>
}
