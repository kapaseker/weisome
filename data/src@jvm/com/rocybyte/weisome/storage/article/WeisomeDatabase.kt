package com.rocybyte.weisome.storage.article

import androidx.room3.Database
import androidx.room3.RoomDatabase

/** 应用本地数据库,目前仅包含文章表。 */
@Database(entities = [ArticleEntity::class], version = 1)
abstract class WeisomeDatabase : RoomDatabase() {
    /** 提供文章表的增删改查入口。 */
    abstract fun articleDao(): ArticleDao
}
