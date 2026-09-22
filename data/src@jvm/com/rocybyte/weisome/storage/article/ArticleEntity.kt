package com.rocybyte.weisome.storage.article

import androidx.room3.Entity
import androidx.room3.PrimaryKey

/** `articles` 表的 Room 实体,与 contracts 的 [com.rocybyte.weisome.article.Article] 一一对应。 */
@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val markdown: String,
    val createdAt: Long,
    val updatedAt: Long,
)
