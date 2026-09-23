package com.rocybyte.weisome.storage.article

import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

/** 应用本地数据库,目前仅包含文章表。 */
@Database(entities = [ArticleEntity::class], version = 2)
abstract class WeisomeDatabase : RoomDatabase() {
    /** 提供文章表的增删改查入口。 */
    abstract fun articleDao(): ArticleDao
}

/** 注册到 Room 构建器的数据库迁移集合。 */
val WeisomeDatabaseMigrations = arrayOf<Migration>(WeisomeDatabaseMigrationsHolder.MIGRATION_1_2)

private object WeisomeDatabaseMigrationsHolder {
    /** v1→v2:文章表新增代码主题列,旧行回退默认主题。 */
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override suspend fun migrate(connection: SQLiteConnection) {
            connection.execSQL("ALTER TABLE articles ADD COLUMN codeTheme TEXT NOT NULL DEFAULT 'GITHUB_LIGHT'")
        }
    }
}
