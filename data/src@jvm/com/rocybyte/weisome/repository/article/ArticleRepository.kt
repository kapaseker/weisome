package com.rocybyte.weisome.repository.article

import com.rocybyte.weisome.article.Article
import com.rocybyte.weisome.article.CodeThemeId
import com.rocybyte.weisome.storage.article.ArticleDao
import com.rocybyte.weisome.storage.article.ArticleEntity
import java.util.UUID

/**
 * 基于 Room DAO 的文章仓库实现。
 *
 * ponytail: 刻意不引入 XxxStore/xxxStorage 层——Room 的 DAO 本身就是数据访问抽象,
 * 再包一层是冗余;若未来需要更换持久化实现,在 [ArticleDao] 与本类之间再抽层即可。
 */
internal class ArticleRepository(
    private val dao: ArticleDao,
) : ArticleRepo {
    /** 查询全部文章并映射为领域模型,顺序由 DAO 的 SQL 保证(修改时间降序)。 */
    override suspend fun list(): List<Article> = dao.list().map(ArticleEntity::toArticle)

    /** 查询指定 id 的文章并映射为领域模型,不存在时返回 `null`。 */
    override suspend fun load(id: String): Article? = dao.load(id)?.toArticle()

    /** 生成 UUID 与时间戳后落库,返回新建的文章。 */
    override suspend fun create(title: String): Article {
        val now = System.currentTimeMillis()
        val article = Article(
            id = UUID.randomUUID().toString(),
            title = title,
            markdown = "",
            createdAt = now,
            updatedAt = now,
            codeTheme = CodeThemeId.GITHUB_LIGHT,
        )
        dao.save(article.toEntity())
        return article
    }

    /** 将领域模型完整写入数据库(按主键覆盖)。 */
    override suspend fun save(article: Article) = dao.save(article.toEntity())

    /** 删除指定 id 的文章。 */
    override suspend fun delete(id: String) = dao.delete(id)
}

/** Room 实体转领域模型;未知主题值回退默认主题。 */
private fun ArticleEntity.toArticle() = Article(id, title, markdown, createdAt, updatedAt, codeTheme.toCodeThemeId())

/** 领域模型转 Room 实体。 */
private fun Article.toEntity() = ArticleEntity(id, title, markdown, createdAt, updatedAt, codeTheme.name)

/** 解析存储的主题名,无法识别时回退 GITHUB_LIGHT。 */
private fun String.toCodeThemeId(): CodeThemeId =
    runCatching { CodeThemeId.valueOf(this) }.getOrDefault(CodeThemeId.GITHUB_LIGHT)
