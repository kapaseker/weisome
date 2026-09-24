package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.CodeTheme
import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownExportStyles
import com.rocybyte.weisome.article.toCssColor

/** Renders one code block with escaped text, inline color spans, and the code theme's base colors. */
internal fun renderCodeBlock(block: MarkdownBlock.CodeBlock, styles: MarkdownExportStyles, codeTheme: CodeTheme): String {
    val code = buildString {
        var cursor = 0
        block.highlights.forEach { span ->
            val start = span.start.coerceIn(cursor, block.code.length)
            val endExclusive = span.endExclusive.coerceIn(start, block.code.length)
            append(escapeHtml(block.code.substring(cursor, start)))
            append("<span style=\"color: ${span.foregroundRgb.toCssColor()};\">")
            append(escapeHtml(block.code.substring(start, endExclusive)))
            append("</span>")
            cursor = endExclusive
        }
        append(escapeHtml(block.code.substring(cursor)))
    }
    return "<pre style=\"${styles.codeBlockCss}\">${styles.codeBlockHeaderHtml}" +
        "<code style=\"${styles.codeElementCss(codeTheme)}\">$code</code></pre>"
}
