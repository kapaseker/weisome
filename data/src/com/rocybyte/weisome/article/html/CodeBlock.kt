package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.CodeTheme
import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownExportStyles
import com.rocybyte.weisome.article.toCssColor

/**
 * Renders one code block with escaped text, inline color spans, and the code theme's base colors.
 *
 * The serialization follows the doocs/md approach proven to survive the WeChat editor: a
 * `<pre>` frame containing a single `<code>` element, spaces encoded as `&nbsp;`, newlines as
 * `<br/>`, and tabs expanded to four spaces. The `pre` tag triggers the editor's code-block
 * handling (preserving `white-space: pre` and the monospace font stack), while entity-encoded
 * whitespace prevents the paste sanitizer from dropping edge whitespace.
 */
internal fun renderCodeBlock(block: MarkdownBlock.CodeBlock, styles: MarkdownExportStyles, codeTheme: CodeTheme): String {
    val code = buildString {
        var cursor = 0
        block.highlights.forEach { span ->
            val start = span.start.coerceIn(cursor, block.code.length)
            val endExclusive = span.endExclusive.coerceIn(start, block.code.length)
            append(escapeCodeText(block.code.substring(cursor, start)))
            append("<span style=\"color: ${span.foregroundRgb.toCssColor()};\">")
            append(escapeCodeText(block.code.substring(start, endExclusive)))
            append("</span>")
            cursor = endExclusive
        }
        append(escapeCodeText(block.code.substring(cursor)))
    }
    return "<pre style=\"${styles.codeBlockFrameCss(codeTheme)}\">${styles.codeBlockHeaderHtml(block.language)}" +
        "<code style=\"${styles.codeElementCss(codeTheme)}\">$code</code></pre>"
}

/**
 * Escapes code text and encodes whitespace for WeChat paste survival: spaces become `&nbsp;`,
 * newlines become `<br/>`, and tabs expand to four `&nbsp;`. This matches doocs/md's renderer
 * (renderer.ts lines 248–253) and prevents the editor from dropping whitespace or re-wrapping.
 */
private fun escapeCodeText(value: String): String = escapeHtml(value)
    .replace("\r\n", "<br/>")
    .replace("\n", "<br/>")
    .replace(" ", "&nbsp;")
    .replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;")
