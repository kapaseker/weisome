package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.WechatArticleStyles

/** Renders an ordered or unordered list block with styled list items. */
internal fun renderListBlock(block: MarkdownBlock.ListBlock): String {
    val tag = if (block.ordered) "ol" else "ul"
    val items = block.items.joinToString("") { item ->
        "<li style=\"${WechatArticleStyles.listItemCss}\">${renderInline(item)}</li>"
    }
    return "<$tag style=\"${WechatArticleStyles.listCss}\">$items</$tag>"
}
