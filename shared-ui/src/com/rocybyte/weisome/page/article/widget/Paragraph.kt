package com.rocybyte.weisome.page.article.widget

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rocybyte.weisome.article.MarkdownBlock

/**
 * Renders a paragraph block with the active theme's body typography and first-letter rule.
 * When [isLead] is set and the theme defines a lead style, the paragraph directly after an
 * h1 renders larger and one gray step down (claudette's prose-lead).
 */
@Composable
internal fun Paragraph(block: MarkdownBlock.Paragraph, inQuote: Boolean = false, isLead: Boolean = false) {
    val styles = LocalMarkdownPreviewStyles.current
    val lines = if (styles.firstLetterCapitalized) {
        block.lines.mapIndexed { index, line ->
            if (index == 0) capitalizeFirstLetter(line) else line
        }
    } else {
        block.lines
    }
    val lead = isLead && styles.leadFontSize != null
    val color = when {
        lead -> styles.leadColor
        inQuote -> styles.quoteColor
        else -> styles.bodyColor
    }
    val topMargin = if (inQuote) styles.quoteParagraphTopMargin else styles.paragraphTopMargin
    val bottomMargin = if (inQuote) styles.quoteParagraphBottomMargin else styles.paragraphBottomMargin
    InlineMarkdownText(
        lines = lines,
        fontSize = if (lead) styles.leadFontSize else styles.bodyFontSize,
        lineHeight = if (lead) styles.leadLineHeight ?: styles.bodyLineHeight else styles.bodyLineHeight,
        color = color,
        fontFamily = if (inQuote && styles.quoteFontSerif) FontFamily.Serif else null,
        modifier = Modifier.padding(top = topMargin.dp, bottom = bottomMargin.dp),
    )
}
