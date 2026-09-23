package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.MarkdownInline
import com.rocybyte.weisome.article.MarkdownExportStyles

/** Renders inline spans while preserving their emphasis semantics. */
internal fun renderInline(
    inlines: List<MarkdownInline>,
    styles: MarkdownExportStyles,
    imageShadow: Boolean = true,
): String = inlines.joinToString("") { inline ->
    when (inline) {
        is MarkdownInline.Text -> escapeHtml(inline.value)
        is MarkdownInline.Bold -> "<strong>${escapeHtml(inline.value)}</strong>"
        is MarkdownInline.Italic -> "<em style=\"${styles.emCss}\">${escapeHtml(inline.value)}</em>"
        is MarkdownInline.Code -> "<code style=\"${styles.inlineCodeCss}\">${escapeHtml(inline.value)}</code>"
        is MarkdownInline.Link ->
            "<a href=\"${escapeHtml(inline.url)}\" style=\"${styles.linkCss}\">${escapeHtml(inline.text)}</a>${if (styles.linkHasIcon) styles.linkIconSpan else ""}"

        is MarkdownInline.Strikethrough -> "<del style=\"${styles.strikethroughCss}\">${escapeHtml(inline.value)}</del>"
        is MarkdownInline.Image ->
            "<img src=\"${escapeHtml(inline.url)}\" alt=\"${escapeHtml(inline.alt)}\" style=\"${if (imageShadow) styles.imgCss else styles.tableImgCss}\">"
    }
}

/**
 * Uppercases the first letter of the leading text-bearing inline, reproducing the
 * hydrogen `h2, h3, p::first-letter { text-transform: capitalize }` rule. Code and image
 * inlines are skipped because their content is not plain flowing text.
 */
internal fun capitalizeFirstLetter(inlines: List<MarkdownInline>): List<MarkdownInline> {
    if (inlines.isEmpty()) return inlines
    val head = inlines.first()
    val uppercased = when (head) {
        is MarkdownInline.Text -> head.copy(value = head.value.replaceFirstChar { it.uppercaseChar() })
        is MarkdownInline.Bold -> head.copy(value = head.value.replaceFirstChar { it.uppercaseChar() })
        is MarkdownInline.Italic -> head.copy(value = head.value.replaceFirstChar { it.uppercaseChar() })
        is MarkdownInline.Strikethrough -> head.copy(value = head.value.replaceFirstChar { it.uppercaseChar() })
        is MarkdownInline.Link -> head.copy(text = head.text.replaceFirstChar { it.uppercaseChar() })
        is MarkdownInline.Code, is MarkdownInline.Image -> return inlines
    }
    return listOf(uppercased) + inlines.drop(1)
}

/** Escapes text that would otherwise be interpreted as HTML markup. */
internal fun escapeHtml(value: String): String = value
    .replace("&", "&amp;")
    .replace("<", "&lt;")
    .replace(">", "&gt;")
    .replace("\"", "&quot;")
    .replace("'", "&#39;")
