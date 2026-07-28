package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.WechatArticleStyles

/** Renders a paragraph block while preserving authored line breaks. */
internal fun renderParagraph(block: MarkdownBlock.Paragraph): String =
    "<p style=\"${WechatArticleStyles.paragraphCss}\">${block.lines.joinToString("<br/>", transform = ::renderInline)}</p>"
