package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.WechatArticleStyles

/** Renders a blockquote with hydrogen's decorative quote marks replacing the pseudo-elements. */
internal fun renderBlockQuote(block: MarkdownBlock.BlockQuote, inQuote: Boolean = false): String {
    val css = if (inQuote) WechatArticleStyles.nestedBlockquoteCss else WechatArticleStyles.blockquoteCss
    val inner = block.blocks.joinToString("\n") { renderBlock(it, inQuote = true) }
    val open = "<span style=\"${WechatArticleStyles.quoteOpenCss}\">\u201C</span>"
    val close = "<span style=\"${WechatArticleStyles.quoteCloseCss}\">\u201D</span>"
    return "<blockquote style=\"$css\">$open$inner$close</blockquote>"
}
