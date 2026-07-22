package com.rocybyte.weisome.repository.article

import com.rocybyte.weisome.article.MarkdownDocument

/** Encapsulates Markdown conversion and desktop clipboard integration for WeChat articles. */
interface WechatArticleRepository {
    fun preview(markdown: String): MarkdownDocument

    fun copyAsHtml(markdown: String): Boolean
}
