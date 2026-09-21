package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.WechatArticleStyles

/** Renders a heading block as a styled heading element with hydrogen decorations. */
internal fun renderHeading(block: MarkdownBlock.Heading): String {
    val content = if (block.level in 2..3) capitalizeFirstLetter(block.content) else block.content
    val prefix = if (block.level == 1) {
        "<span style=\"${WechatArticleStyles.h1PrefixCss}\">#</span>"
    } else {
        ""
    }
    return "<h${block.level} style=\"${WechatArticleStyles.headingCss(block.level)}\">$prefix${renderInline(content)}</h${block.level}>"
}
