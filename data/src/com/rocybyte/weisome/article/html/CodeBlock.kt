package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.WechatArticleStyles

/** Renders one code block with escaped text and inline color spans. */
internal fun renderCodeBlock(block: MarkdownBlock.CodeBlock): String {
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
    return "<pre style=\"${WechatArticleStyles.codeBlockCss}\"><code style=\"${WechatArticleStyles.codeElementCss}\">$code</code></pre>"
}

/** Formats a packed RGB value as a six-digit CSS hexadecimal color. */
private fun Int.toCssColor(): String = "#%06x".format(this and 0xFFFFFF)
