package com.rocybyte.weisome.repository.article

import com.rocybyte.weisome.article.CodeThemeId
import com.rocybyte.weisome.article.MarkdownDocument
import com.rocybyte.weisome.article.MarkdownThemeId

/** Encapsulates Markdown conversion and desktop clipboard integration for WeChat articles. */
interface WechatArticleRepository {
    /** Parses Markdown into the document model used by the article preview. */
    fun preview(markdown: String, codeTheme: CodeThemeId): MarkdownDocument

    /** Converts Markdown to WeChat HTML with the given themes and copies it to the platform clipboard. */
    fun copyAsHtml(markdown: String, codeTheme: CodeThemeId, markdownTheme: MarkdownThemeId): Boolean
}
