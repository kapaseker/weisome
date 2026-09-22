package com.rocybyte.weisome.page.article.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownDocument

/**
 * Renders a structured Markdown document using the WeChat preview styles.
 *
 * @param onBlockPositioned when non-null, invoked for every top-level block with its
 * index and top offset in the scroll content, used for split-view scroll synchronization.
 */
@Composable
internal fun WechatArticlePreview(
    document: MarkdownDocument,
    modifier: Modifier = Modifier,
    onBlockPositioned: ((blockIndex: Int, topPx: Float) -> Unit)? = null,
) {
    Column(modifier) {
        RenderBlocks(document.blocks, onBlockPositioned = onBlockPositioned)
    }
}

/**
 * Dispatches each block type to its hydrogen-styled preview widget.
 *
 * @param onBlockPositioned when non-null, each top-level block is wrapped in a layout-neutral
 * Box that reports its top offset; nested invocations never pass it, so only top-level
 * blocks are measured.
 */
@Composable
internal fun RenderBlocks(
    blocks: List<MarkdownBlock>,
    inQuote: Boolean = false,
    onBlockPositioned: ((blockIndex: Int, topPx: Float) -> Unit)? = null,
) {
    blocks.forEachIndexed { index, block ->
        val content: @Composable () -> Unit = {
            when (block) {
                is MarkdownBlock.Heading -> Heading(block)
                is MarkdownBlock.Paragraph -> Paragraph(block, inQuote)
                is MarkdownBlock.ListBlock -> ListBlock(block)
                is MarkdownBlock.CodeBlock -> CodeBlock(block)
                is MarkdownBlock.BlockQuote -> BlockQuote(block, nested = inQuote)
                is MarkdownBlock.HorizontalRule -> HorizontalRule()
                is MarkdownBlock.Table -> Table(block)
            }
        }
        if (onBlockPositioned != null) {
            Box(
                Modifier.onGloballyPositioned { coordinates ->
                    // Block top relative to the wrapping Column (the scroll content root):
                    // the difference of the block and parent origins in window coordinates,
                    // taken within the same layout pass, is invariant under scrolling.
                    val originY = coordinates.localToWindow(Offset.Zero).y
                    val parentOriginY = coordinates.parentLayoutCoordinates?.localToWindow(Offset.Zero)?.y
                    onBlockPositioned(index, originY - (parentOriginY ?: 0f))
                },
            ) {
                content()
            }
        } else {
            content()
        }
    }
}
