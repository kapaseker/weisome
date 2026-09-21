package com.rocybyte.weisome.article.html

import com.rocybyte.weisome.article.WechatArticleStyles

/** Renders a thematic break with the gradient line and centered juejin logo. */
internal fun renderHorizontalRule(): String =
    "<div style=\"${WechatArticleStyles.hrCss}\"><div style=\"${WechatArticleStyles.hrLogoCss}\"></div></div>"
