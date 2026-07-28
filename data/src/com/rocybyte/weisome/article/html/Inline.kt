package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.MarkdownInline
import com.rocybyte.weisome.article.WechatArticleStyles

/** Renders inline spans while preserving their emphasis semantics. */
internal fun renderInline(inlines: List<MarkdownInline>): String = inlines.joinToString("") { inline ->
    when (inline) {
        is MarkdownInline.Text -> escapeHtml(inline.value)
        is MarkdownInline.Bold -> "<strong>${escapeHtml(inline.value)}</strong>"
        is MarkdownInline.Italic -> "<em>${escapeHtml(inline.value)}</em>"
        is MarkdownInline.Code -> "<code style=\"${WechatArticleStyles.inlineCodeCss}\">${escapeHtml(inline.value)}</code>"
    }
}

/** Escapes text that would otherwise be interpreted as HTML markup. */
internal fun escapeHtml(value: String): String = value
    .replace("&", "&amp;")
    .replace("<", "&lt;")
    .replace(">", "&gt;")
    .replace("\"", "&quot;")
    .replace("'", "&#39;")
