package com.rocybyte.weisome.page.article.widget

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rocybyte.weisome.article.MarkdownBlock

/** Renders a paragraph block with the active theme's body typography and first-letter rule. */
@Composable
internal fun Paragraph(block: MarkdownBlock.Paragraph, inQuote: Boolean = false) {
    val styles = LocalMarkdownPreviewStyles.current
    val lines = if (styles.firstLetterCapitalized) {
        block.lines.mapIndexed { index, line ->
            if (index == 0) capitalizeFirstLetter(line) else line
        }
    } else {
        block.lines
    }
    val color = if (inQuote) styles.quoteColor else styles.bodyColor
    val topMargin = if (inQuote) styles.quoteParagraphTopMargin else styles.paragraphTopMargin
    val bottomMargin = if (inQuote) styles.quoteParagraphBottomMargin else styles.paragraphBottomMargin
    InlineMarkdownText(
        lines = lines,
        fontSize = styles.bodyFontSize,
        lineHeight = styles.bodyLineHeight,
        color = color,
        modifier = Modifier.padding(top = topMargin.dp, bottom = bottomMargin.dp),
    )
}
