package com.rocybyte.weisome.storage.article

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert

@Dao
interface ArticleDao {
    /** 查询全部文章,按修改时间降序排列。 */
    @Query("SELECT * FROM articles ORDER BY updatedAt DESC")
    suspend fun list(): List<ArticleEntity>

    /** 查询指定 id 的文章,不存在时返回 `null`。 */
    @Query("SELECT * FROM articles WHERE id = :id")
    suspend fun load(id: String): ArticleEntity?

    /** 插入新文章或按主键覆盖更新已存在的文章。 */
    @Upsert
    suspend fun save(article: ArticleEntity)

    /** 删除指定 id 的文章。 */
    @Query("DELETE FROM articles WHERE id = :id")
    suspend fun delete(id: String)
}
