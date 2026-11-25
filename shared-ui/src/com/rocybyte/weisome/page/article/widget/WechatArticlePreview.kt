package com.rocybyte.weisome.page.article.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.rocybyte.weisome.article.CodeThemeId
import com.rocybyte.weisome.article.CodeThemes
import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownDocument
import com.rocybyte.weisome.article.MarkdownThemeId

/**
 * Renders a structured Markdown document using the theme's preview styles.
 *
 * @param markdownTheme style set applied to every block widget via LocalMarkdownPreviewStyles.
 * @param codeTheme code highlight theme applied to code blocks through LocalCodeTheme.
 * @param onBlockPositioned when non-null, invoked for every top-level block with its
 * index and top offset in the scroll content, used for split-view scroll synchronization.
 */
@Composable
internal fun WechatArticlePreview(
    document: MarkdownDocument,
    markdownTheme: MarkdownThemeId,
    codeTheme: CodeThemeId,
    modifier: Modifier = Modifier,
    onBlockPositioned: ((blockIndex: Int, topPx: Float) -> Unit)? = null,
) {
    CompositionLocalProvider(
        LocalMarkdownPreviewStyles provides previewStylesFor(markdownTheme),
        LocalCodeTheme provides CodeThemes.forId(codeTheme),
    ) {
        Column(if (markdownTheme == MarkdownThemeId.CHOCOLATE) modifier.chocolateGrid() else modifier) {
            RenderBlocks(document.blocks, onBlockPositioned = onBlockPositioned)
        }
    }
}

/** Paints Chocolate's centered 20dp tiles behind the content; opaque code blocks cover the grid. */
private fun Modifier.chocolateGrid(): Modifier = drawBehind {
    val step = 20.dp.toPx()
    val lineWidth = step * 0.05f
    val color = Color(72, 42, 10).copy(alpha = 0.05f)
    // CSS centers a tile, then repeats it in both directions; 360deg puts the horizontal line at its bottom.
    var x = ((size.width - step) / 2f % step + step) % step - step
    while (x < size.width) {
        val left = x.coerceAtLeast(0f)
        val right = (x + lineWidth).coerceAtMost(size.width)
        if (right > left) drawRect(color, Offset(left, 0f), Size(right - left, size.height))
        x += step
    }
    var y = ((size.height - step) / 2f % step + step) % step - lineWidth
    while (y < size.height) {
        val top = y.coerceAtLeast(0f)
        val bottom = (y + lineWidth).coerceAtMost(size.height)
        if (bottom > top) drawRect(color, Offset(0f, top), Size(size.width, bottom - top))
        y += step
    }
}

/**
 * Dispatches each block type to its theme-styled preview widget.
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
        // Claudette's prose-lead: the paragraph directly after a top-level h1 renders larger.
        val prev = blocks.getOrNull(index - 1)
        val isLead = !inQuote &&
            block is MarkdownBlock.Paragraph &&
            prev is MarkdownBlock.Heading &&
            prev.level == 1
        // Only the document's first top-level block loses its top margin (claudette's `:first-child` rule).
        val atDocumentStart = index == 0 && !inQuote
        val content: @Composable () -> Unit = {
            when (block) {
                is MarkdownBlock.Heading -> Heading(block, atDocumentStart)
                is MarkdownBlock.Paragraph -> Paragraph(block, inQuote, isLead)
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
