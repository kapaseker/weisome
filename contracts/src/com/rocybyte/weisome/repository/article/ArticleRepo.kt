package com.rocybyte.weisome.repository.article

import com.rocybyte.weisome.article.Article

interface ArticleRepo {
    /** 按修改时间降序返回全部文章。 */
    suspend fun list(): List<Article>

    /** 读取指定 id 的文章,不存在时返回 `null`。 */
    suspend fun load(id: String): Article?

    /** 生成新 id 与时间戳并落库,返回新建的文章。 */
    suspend fun create(title: String): Article

    /** 写入文章的完整内容与元数据。 */
    suspend fun save(article: Article)

    /** 删除指定 id 的文章。 */
    suspend fun delete(id: String)
}
