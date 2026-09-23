package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownExportStyles

/** Renders an ordered or unordered list block with nested and task items. */
internal fun renderListBlock(block: MarkdownBlock.ListBlock, styles: MarkdownExportStyles): String =
    renderList(block, styles.listCss, styles)

/** Renders a nested list with the tighter margin the theme defines for lists inside list items. */
private fun renderNestedList(block: MarkdownBlock.ListBlock, styles: MarkdownExportStyles): String =
    renderList(block, styles.nestedListCss, styles)

/** Renders one list level; nested items recurse with the nested margin override. */
private fun renderList(block: MarkdownBlock.ListBlock, containerCss: String, styles: MarkdownExportStyles): String {
    val tag = if (block.ordered) "ol" else "ul"
    val items = block.items.joinToString("") { item ->
        val itemCss = if (block.ordered) {
            styles.orderedListItemCss
        } else {
            styles.listItemCss
        }
        val taskCss = if (item.task != null) styles.taskItemPrefixCss else ""
        val marker = when (item.task) {
            true -> "\u2611 "
            false -> "\u2610 "
            null -> ""
        }
        val nested = item.child?.let { renderNestedList(it, styles) }.orEmpty()
        "<li style=\"$taskCss$itemCss\">$marker${renderInline(item.content, styles)}$nested</li>"
    }
    return "<$tag style=\"$containerCss\">$items</$tag>"
}
