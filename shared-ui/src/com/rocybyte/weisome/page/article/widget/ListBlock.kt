package com.rocybyte.weisome.page.article.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rocybyte.weisome.article.ListItem
import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.widget.WeiSomeText

/** Renders an ordered, unordered, nested, or task list block with the active theme's spacing. */
@Composable
internal fun ListBlock(block: MarkdownBlock.ListBlock) {
    val styles = LocalMarkdownPreviewStyles.current
    Column(
        Modifier.padding(
            start = styles.listPaddingStart.dp,
            top = styles.listTopMargin.dp,
            bottom = styles.listBottomMargin.dp,
        ),
    ) {
        ListItems(block)
    }
}

/** Renders the item rows of one list level without the level's outer margins. */
@Composable
private fun ListItems(block: MarkdownBlock.ListBlock) {
    block.items.forEachIndexed { index, item ->
        ListItemRow(block, item, index)
    }
}

/** Renders one list item row: its marker, content, and optional nested child list. */
@Composable
private fun ListItemRow(block: MarkdownBlock.ListBlock, item: ListItem, index: Int) {
    val styles = LocalMarkdownPreviewStyles.current
    Column(Modifier.fillMaxWidth().padding(top = if (index > 0) styles.listItemTopMargin.dp else 0.dp)) {
        Row {
            val marker = when {
                item.task == true -> "\u2611"
                item.task == false -> "\u2610"
                block.ordered -> "${index + 1}."
                else -> "\u2022"
            }
            WeiSomeText(marker, color = styles.bodyColor)
            Spacer(Modifier.width(8.dp))
            InlineMarkdownText(
                lines = listOf(item.content),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = if (block.ordered) styles.orderedItemExtraPaddingStart.dp else 0.dp),
                fontSize = styles.bodyFontSize,
                lineHeight = styles.bodyLineHeight,
                color = styles.bodyColor,
            )
        }
        item.child?.let { child ->
            Column(
                Modifier.padding(
                    start = styles.nestedListPaddingStart.dp,
                    top = styles.nestedListTopMargin.dp,
                ),
            ) {
                ListItems(child)
            }
        }
    }
}
