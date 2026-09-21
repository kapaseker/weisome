package com.rocybyte.weisome.repository.article

import com.rocybyte.weisome.article.MarkdownDocument

/** Encapsulates Markdown conversion and desktop clipboard integration for WeChat articles. */
interface WechatArticleRepository {
    /** Parses Markdown into the document model used by the article preview. */
    fun preview(markdown: String): MarkdownDocument

    /** Converts Markdown to WeChat HTML and copies it to the platform clipboard. */
    fun copyAsHtml(markdown: String): Boolean
}
