package com.rocybyte.weisome.article

object MarkdownToWechatHtml {
    /** Converts Markdown source into clipboard-ready WeChat HTML. */
    fun render(markdown: String): String = render(MarkdownDocumentParser.parse(markdown))

    /** Renders structured document blocks as styled HTML elements. */
    internal fun render(document: MarkdownDocument): String = document.blocks.joinToString("\n") { block ->
        when (block) {
            is MarkdownBlock.Heading -> "<h${block.level} style=\"${WechatArticleStyles.headingCss(block.level)}\">${html(block.content)}</h${block.level}>"
            is MarkdownBlock.Paragraph -> "<p style=\"${WechatArticleStyles.paragraphCss}\">${block.lines.joinToString("<br/>", transform = ::html)}</p>"
            is MarkdownBlock.ListBlock -> {
                val tag = if (block.ordered) "ol" else "ul"
                "<$tag style=\"${WechatArticleStyles.listCss}\">${block.items.joinToString("") { "<li style=\"${WechatArticleStyles.listItemCss}\">${html(it)}</li>" }}</$tag>"
            }
            is MarkdownBlock.CodeBlock -> codeBlock(block)
        }
    }

    /** Renders one code block with escaped text and inline color spans. */
    private fun codeBlock(block: MarkdownBlock.CodeBlock): String {
        val code = buildString {
            var cursor = 0
            block.highlights.forEach { span ->
                val start = span.start.coerceIn(cursor, block.code.length)
                val endExclusive = span.endExclusive.coerceIn(start, block.code.length)
                append(escape(block.code.substring(cursor, start)))
                append("<span style=\"color: ${span.foregroundRgb.toCssColor()};\">")
                append(escape(block.code.substring(start, endExclusive)))
                append("</span>")
                cursor = endExclusive
            }
            append(escape(block.code.substring(cursor)))
        }
        return "<pre style=\"${WechatArticleStyles.codeBlockCss}\"><code>$code</code></pre>"
    }

    /** Renders inline spans while preserving their emphasis semantics. */
    private fun html(inlines: List<MarkdownInline>): String = inlines.joinToString("") { inline ->
        when (inline) {
            is MarkdownInline.Text -> escape(inline.value)
            is MarkdownInline.Bold -> "<strong>${escape(inline.value)}</strong>"
            is MarkdownInline.Italic -> "<em>${escape(inline.value)}</em>"
            is MarkdownInline.Code -> "<code style=\"${WechatArticleStyles.inlineCodeCss}\">${escape(inline.value)}</code>"
        }
    }

    /** Escapes text that would otherwise be interpreted as HTML markup. */
    private fun escape(value: String): String = value
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;")

    /** Formats a packed RGB value as a six-digit CSS hexadecimal color. */
    private fun Int.toCssColor(): String = "#%06x".format(this and 0xFFFFFF)
}
