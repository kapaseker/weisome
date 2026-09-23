package com.rocybyte.weisome.storage.article

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking

class WeisomeDatabaseTest {
    /** Creates an in-memory database backed by the bundled desktop SQLite driver. */
    private fun createDao(): ArticleDao =
        Room.inMemoryDatabaseBuilder<WeisomeDatabase>()
            .setDriver(BundledSQLiteDriver())
            .build()
            .articleDao()

    @Test
    /** Verifies an inserted article is returned unchanged by a subsequent load. */
    fun `save then load returns the same article`() = runBlocking {
        val dao = createDao()
        val article = article(id = "a1", title = "标题", markdown = "# hello", updatedAt = 100)

        dao.save(article)
        assertEquals(article, dao.load("a1"))
    }

    @Test
    /** Verifies list ordering follows the updatedAt DESC contract. */
    fun `list orders articles by updatedAt descending`() = runBlocking {
        val dao = createDao()
        dao.save(article(id = "old", updatedAt = 100))
        dao.save(article(id = "newest", updatedAt = 300))
        dao.save(article(id = "middle", updatedAt = 200))

        assertEquals(listOf("newest", "middle", "old"), dao.list().map { it.id })
    }

    @Test
    /** Verifies @Upsert overwrites the row with the same primary key instead of inserting a duplicate. */
    fun `upsert overwrites an existing article`() = runBlocking {
        val dao = createDao()
        dao.save(article(id = "a1", title = "旧标题", markdown = "old", updatedAt = 100))

        dao.save(article(id = "a1", title = "新标题", markdown = "new", updatedAt = 200))

        val stored = dao.load("a1")
        assertEquals("新标题", stored?.title)
        assertEquals("new", stored?.markdown)
        assertEquals(200, stored?.updatedAt)
        assertEquals(1, dao.list().size)
    }

    @Test
    /** Verifies a deleted article can no longer be loaded. */
    fun `delete removes the article`() = runBlocking {
        val dao = createDao()
        dao.save(article(id = "a1"))

        dao.delete("a1")

        assertNull(dao.load("a1"))
        assertTrue(dao.list().isEmpty())
    }

    /** Builds a minimal article row with sensible defaults for the field under test. */
    private fun article(
        id: String,
        title: String = "t",
        markdown: String = "",
        createdAt: Long = 0,
        updatedAt: Long = 0,
        codeTheme: String = "GITHUB_LIGHT",
    ) = ArticleEntity(
        id = id,
        title = title,
        markdown = markdown,
        createdAt = createdAt,
        updatedAt = updatedAt,
        codeTheme = codeTheme,
    )
}
