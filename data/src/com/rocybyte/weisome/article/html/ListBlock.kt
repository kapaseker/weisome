package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.WechatArticleStyles

/** Renders an ordered or unordered list block with nested and task items. */
internal fun renderListBlock(block: MarkdownBlock.ListBlock): String =
    renderList(block, WechatArticleStyles.listCss)

/** Renders a nested list with the tighter margin hydrogen defines for lists inside list items. */
private fun renderNestedList(block: MarkdownBlock.ListBlock): String =
    renderList(block, WechatArticleStyles.nestedListCss)

/** Renders one list level; nested items recurse with the nested margin override. */
private fun renderList(block: MarkdownBlock.ListBlock, containerCss: String): String {
    val tag = if (block.ordered) "ol" else "ul"
    val items = block.items.joinToString("") { item ->
        val itemCss = if (block.ordered) {
            WechatArticleStyles.orderedListItemCss
        } else {
            WechatArticleStyles.listItemCss
        }
        val taskCss = if (item.task != null) WechatArticleStyles.taskItemPrefixCss else ""
        val marker = when (item.task) {
            true -> "\u2611 "
            false -> "\u2610 "
            null -> ""
        }
        val nested = item.child?.let { renderNestedList(it) }.orEmpty()
        "<li style=\"$taskCss$itemCss\">$marker${renderInline(item.content)}$nested</li>"
    }
    return "<$tag style=\"$containerCss\">$items</$tag>"
}
