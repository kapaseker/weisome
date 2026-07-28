package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.WechatArticleStyles

/** Renders a heading block as a styled heading element. */
internal fun renderHeading(block: MarkdownBlock.Heading): String =
    "<h${block.level} style=\"${WechatArticleStyles.headingCss(block.level)}\">${renderInline(block.content)}</h${block.level}>"
