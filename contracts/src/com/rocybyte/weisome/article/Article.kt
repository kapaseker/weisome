package com.rocybyte.weisome.article

/** 文章的领域模型,含列表元数据与正文内容。 */
data class Article(
    val id: String,
    val title: String,
    val markdown: String,
    val createdAt: Long,
    val updatedAt: Long,
    val codeTheme: CodeThemeId,
)
