package com.rocybyte.weisome.ui

object MarkdownToWechatHtml {
    fun render(markdown: String): String = render(MarkdownDocumentParser.parse(markdown))

    fun render(document: MarkdownDocument): String = document.blocks.joinToString("\n") { block ->
        when (block) {
            is MarkdownBlock.Heading -> "<h${block.level} style=\"${WechatArticleStyles.headingCss(block.level)}\">${html(block.content)}</h${block.level}>"
            is MarkdownBlock.Paragraph -> "<p style=\"${WechatArticleStyles.paragraphCss}\">${block.lines.joinToString("<br/>", transform = ::html)}</p>"
            is MarkdownBlock.ListBlock -> {
                val tag = if (block.ordered) "ol" else "ul"
                "<$tag style=\"${WechatArticleStyles.listCss}\">${block.items.joinToString("") { "<li style=\"${WechatArticleStyles.listItemCss}\">${html(it)}</li>" }}</$tag>"
            }
        }
    }

    private fun html(inlines: List<MarkdownInline>): String = inlines.joinToString("") { inline ->
        when (inline) {
            is MarkdownInline.Text -> escape(inline.value)
            is MarkdownInline.Bold -> "<strong>${escape(inline.value)}</strong>"
            is MarkdownInline.Italic -> "<em>${escape(inline.value)}</em>"
        }
    }

    private fun escape(value: String) = value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;")
}
