package com.rocybyte.weisome.page.article.biz

import androidx.lifecycle.ViewModel
import com.rocybyte.weisome.article.Article
import com.rocybyte.weisome.article.ArticleLayoutMode
import com.rocybyte.weisome.article.CodeThemeId
import com.rocybyte.weisome.article.MarkdownDocument
import com.rocybyte.weisome.article.MarkdownThemeId
import com.rocybyte.weisome.repository.article.ArticleLayoutRepo
import com.rocybyte.weisome.repository.article.ArticleRepo
import com.rocybyte.weisome.repository.article.WechatArticleRepository
import java.util.concurrent.CopyOnWriteArrayList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.yield
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class WechatArticleViewModelTest {
    @Test
    /** Verifies source edits refresh preview state and clear the previous copy result. */
    fun `updates the preview and clears copy status when markdown changes`() {
        val repository = FakeWechatArticleRepository()
        val viewModel = editorViewModel(repository)

        viewModel.copyAsHtml()
        viewModel.onMarkdownChanged("# Updated")

        assertEquals("# Updated", viewModel.uiState.value.markdown)
        assertEquals("# Updated", repository.lastPreviewMarkdown)
        assertNull(viewModel.uiState.value.copySucceeded)
    }

    @Test
    /** Verifies blank content is ignored and non-blank content reaches the repository. */
    fun `copies only non-blank markdown through the repository`() {
        val repository = FakeWechatArticleRepository(copyResult = false)
        val viewModel = editorViewModel(repository)

        viewModel.copyAsHtml()
        assertFalse(repository.copyCalled)

        viewModel.onMarkdownChanged("Article")
        viewModel.copyAsHtml()

        assertTrue(repository.copyCalled)
        assertFalse(viewModel.uiState.value.copySucceeded!!)
    }

    @Test
    /** Verifies a persisted layout is restored before the layout controls become ready. */
    fun `restores the persisted article layout`() = runBlocking {
        val viewModel = loadedViewModel(FakeArticleLayoutRepo(restoredMode = ArticleLayoutMode.PREVIEW_ONLY))

        assertEquals(
            ArticleLayoutUiState(isLoaded = true, mode = ArticleLayoutMode.PREVIEW_ONLY),
            viewModel.layoutState.value,
        )
    }

    @Test
    /** Verifies a missing or failed layout read becomes a ready split layout. */
    fun `layout load failure falls back to split mode`() = runBlocking {
        val viewModel = loadedViewModel(FakeArticleLayoutRepo(loadFailure = true))

        assertEquals(
            ArticleLayoutUiState(isLoaded = true, mode = ArticleLayoutMode.SPLIT),
            viewModel.layoutState.value,
        )
    }

    @Test
    /** Verifies selection is immediate and remains active even when preference persistence fails. */
    fun `layout selection remains active when saving fails`() = runBlocking {
        val layoutRepository = FakeArticleLayoutRepo(saveFailure = true)
        val viewModel = loadedViewModel(layoutRepository)

        viewModel.onLayoutModeSelected(ArticleLayoutMode.EDITOR_ONLY)
        withTimeout(1_000) {
            layoutRepository.saveAttempts.first { attempts -> attempts > 0 }
        }

        assertEquals(ArticleLayoutMode.EDITOR_ONLY, viewModel.layoutState.value.mode)
        assertTrue(layoutRepository.savedModes.isEmpty())
    }

    @Test
    /** Verifies a successful layout selection persists the exact newly selected mode. */
    fun `layout selection persists the selected mode`() = runBlocking {
        val layoutRepository = FakeArticleLayoutRepo()
        val viewModel = loadedViewModel(layoutRepository)

        viewModel.onLayoutModeSelected(ArticleLayoutMode.PREVIEW_ONLY)
        withTimeout(1_000) {
            while (layoutRepository.savedModes.isEmpty()) yield()
        }

        assertEquals(listOf(ArticleLayoutMode.PREVIEW_ONLY), layoutRepository.savedModes)
    }

    @Test
    /** Verifies rapid layout changes cancel an older pending write and persist only the final mode. */
    fun `rapid layout selections persist only the latest mode`() = runBlocking {
        val layoutRepository = FakeArticleLayoutRepo(saveDelayMillis = 100)
        val viewModel = loadedViewModel(layoutRepository)

        viewModel.onLayoutModeSelected(ArticleLayoutMode.EDITOR_ONLY)
        withTimeout(1_000) {
            layoutRepository.saveAttempts.first { attempts -> attempts > 0 }
        }
        viewModel.onLayoutModeSelected(ArticleLayoutMode.PREVIEW_ONLY)
        withTimeout(1_000) {
            while (layoutRepository.savedModes.isEmpty()) yield()
        }

        assertEquals(listOf(ArticleLayoutMode.PREVIEW_ONLY), layoutRepository.savedModes)
    }

    @Test
    /** Verifies the periodic saver persists markdown edits with the current article id. */
    fun `auto save persists markdown changes`() = runBlocking {
        val articleRepo = FakeEditorArticleRepo()
        val viewModel = WechatArticleViewModel(
            FakeWechatArticleRepository(), FakeArticleLayoutRepo(), articleRepo,
            "Welcome", "a1", autoSaveIntervalMillis = 10,
        )
        withTimeout(1_000) { viewModel.uiState.first { it.isArticleLoaded } }

        viewModel.onMarkdownChanged("# hi")
        withTimeout(1_000) {
            while (articleRepo.saved.isEmpty()) yield()
        }

        assertEquals("# hi", articleRepo.saved.first().markdown)
        assertEquals("a1", articleRepo.saved.first().id)
    }

    @Test
    /** Verifies title edits ride along with the periodic draft save. */
    fun `auto save persists title changes`() = runBlocking {
        val articleRepo = FakeEditorArticleRepo()
        val viewModel = WechatArticleViewModel(
            FakeWechatArticleRepository(), FakeArticleLayoutRepo(), articleRepo,
            "Welcome", "a1", autoSaveIntervalMillis = 10,
        )
        withTimeout(1_000) { viewModel.uiState.first { it.isArticleLoaded } }

        viewModel.onTitleChanged("新标题")
        withTimeout(1_000) {
            while (articleRepo.saved.none { it.title == "新标题" }) yield()
        }
    }

    @Test
    /** Verifies untouched drafts never trigger a repository write. */
    fun `auto save skips writes when nothing changed`() = runBlocking {
        val articleRepo = FakeEditorArticleRepo(
            stored = Article(id = "a1", title = "t", markdown = "# base", createdAt = 0, updatedAt = 0, codeTheme = CodeThemeId.GITHUB_LIGHT, markdownTheme = MarkdownThemeId.GITHUB),
        )
        val viewModel = WechatArticleViewModel(
            FakeWechatArticleRepository(), FakeArticleLayoutRepo(), articleRepo,
            "Welcome", "a1", autoSaveIntervalMillis = 10,
        )
        withTimeout(1_000) { viewModel.uiState.first { it.isArticleLoaded } }

        delay(50)

        assertTrue(articleRepo.saved.isEmpty())
    }

    @Test
    /** Verifies onCleared performs one final fire-and-forget save for unsaved edits. */
    fun `onCleared flushes unsaved changes`() = runBlocking {
        val articleRepo = FakeEditorArticleRepo()
        val viewModel = WechatArticleViewModel(
            FakeWechatArticleRepository(), FakeArticleLayoutRepo(), articleRepo,
            "Welcome", "a1", autoSaveIntervalMillis = 60_000,
        )
        withTimeout(1_000) { viewModel.uiState.first { it.isArticleLoaded } }

        viewModel.onMarkdownChanged("# before exit")
        // onCleared 是 protected,测试通过反射触发,避免为测试放宽生产代码可见性。
        ViewModel::class.java.getDeclaredMethod("onCleared").apply { isAccessible = true }
            .invoke(viewModel)
        withTimeout(1_000) {
            while (articleRepo.saved.isEmpty()) yield()
        }

        assertEquals("# before exit", articleRepo.saved.first().markdown)
    }

    @Test
    /** Verifies a stored draft's code theme is restored and used to re-render its preview. */
    fun `restores the persisted code theme with the preview`() = runBlocking {
        val repository = FakeWechatArticleRepository()
        val articleRepo = FakeEditorArticleRepo(
            stored = Article(id = "a1", title = "t", markdown = "# base", createdAt = 0, updatedAt = 0, codeTheme = CodeThemeId.DARCULA, markdownTheme = MarkdownThemeId.HYDROGEN),
        )
        val viewModel = WechatArticleViewModel(
            repository, FakeArticleLayoutRepo(), articleRepo,
            "Welcome", "a1", autoSaveIntervalMillis = 60_000,
        )
        withTimeout(1_000) { viewModel.uiState.first { it.isArticleLoaded } }

        assertEquals(CodeThemeId.DARCULA, viewModel.uiState.value.codeTheme)
        assertEquals(MarkdownThemeId.HYDROGEN, viewModel.uiState.value.markdownTheme)
        assertEquals(CodeThemeId.DARCULA, repository.lastPreviewTheme)
    }

    @Test
    /** Verifies theme selection re-renders the preview and rides along with the periodic save. */
    fun `code theme selection updates preview and persists`() = runBlocking {
        val articleRepo = FakeEditorArticleRepo()
        val viewModel = WechatArticleViewModel(
            FakeWechatArticleRepository(), FakeArticleLayoutRepo(), articleRepo,
            "Welcome", "a1", autoSaveIntervalMillis = 10,
        )
        withTimeout(1_000) { viewModel.uiState.first { it.isArticleLoaded } }

        viewModel.onMarkdownChanged("# hi")
        viewModel.onCodeThemeSelected(CodeThemeId.MONOKAI)
        withTimeout(1_000) {
            while (articleRepo.saved.none { it.codeTheme == CodeThemeId.MONOKAI }) yield()
        }

        assertEquals(CodeThemeId.MONOKAI, viewModel.uiState.value.codeTheme)
        assertEquals(CodeThemeId.MONOKAI, articleRepo.saved.last().codeTheme)
    }

    @Test
    /** Verifies Markdown theme selection updates state and rides along with the periodic save. */
    fun `markdown theme selection updates state and persists`() = runBlocking {
        val articleRepo = FakeEditorArticleRepo()
        val viewModel = WechatArticleViewModel(
            FakeWechatArticleRepository(), FakeArticleLayoutRepo(), articleRepo,
            "Welcome", "a1", autoSaveIntervalMillis = 10,
        )
        withTimeout(1_000) { viewModel.uiState.first { it.isArticleLoaded } }

        viewModel.onMarkdownThemeSelected(MarkdownThemeId.HYDROGEN)
        withTimeout(1_000) {
            while (articleRepo.saved.none { it.markdownTheme == MarkdownThemeId.HYDROGEN }) yield()
        }

        assertEquals(MarkdownThemeId.HYDROGEN, viewModel.uiState.value.markdownTheme)
        assertEquals(MarkdownThemeId.HYDROGEN, articleRepo.saved.last().markdownTheme)
    }

    /** Creates an editor ViewModel with the default save interval for copy-related tests. */
    private fun editorViewModel(repository: WechatArticleRepository): WechatArticleViewModel =
        WechatArticleViewModel(repository, FakeArticleLayoutRepo(), FakeEditorArticleRepo(), "Welcome", "a1")

    /** Creates an article ViewModel and waits for its persisted layout read to finish. */
    private suspend fun loadedViewModel(layoutRepository: ArticleLayoutRepo): WechatArticleViewModel {
        val viewModel = WechatArticleViewModel(
            FakeWechatArticleRepository(), layoutRepository, FakeEditorArticleRepo(), "Welcome", "a1",
        )
        withTimeout(1_000) {
            viewModel.layoutState.first { state -> state.isLoaded }
        }
        return viewModel
    }
}

private class FakeArticleLayoutRepo(
    private val restoredMode: ArticleLayoutMode? = null,
    private val loadFailure: Boolean = false,
    private val saveFailure: Boolean = false,
    private val saveDelayMillis: Long = 0,
) : ArticleLayoutRepo {
    val savedModes = CopyOnWriteArrayList<ArticleLayoutMode>()
    val saveAttempts = kotlinx.coroutines.flow.MutableStateFlow(0)

    /** Returns the configured layout or raises the configured read failure. */
    override suspend fun load(): ArticleLayoutMode? {
        if (loadFailure) error("load failed")
        return restoredMode
    }

    /** Records a layout unless the fake is configured to fail the write. */
    override suspend fun save(mode: ArticleLayoutMode) {
        saveAttempts.value += 1
        delay(saveDelayMillis)
        if (saveFailure) error("save failed")
        savedModes += mode
    }
}

private class FakeWechatArticleRepository(
    private val copyResult: Boolean = true,
) : WechatArticleRepository {
    var lastPreviewMarkdown = ""
    var lastPreviewTheme: CodeThemeId? = null
    var copyCalled = false

    /** Records preview input and returns a minimal document fixture. */
    override fun preview(markdown: String, codeTheme: CodeThemeId): MarkdownDocument {
        lastPreviewMarkdown = markdown
        lastPreviewTheme = codeTheme
        return MarkdownDocument(emptyList())
    }

    /** Records copied Markdown and returns the configured result. */
    override fun copyAsHtml(markdown: String, codeTheme: CodeThemeId, markdownTheme: MarkdownThemeId): Boolean {
        copyCalled = true
        return copyResult
    }
}

private class FakeEditorArticleRepo(
    stored: Article? = null,
) : ArticleRepo {
    val saved = CopyOnWriteArrayList<Article>()
    private val storedMap = stored?.let { mapOf(it.id to it) } ?: emptyMap()

    /** Returns the stored articles; unused by the editor ViewModel. */
    override suspend fun list(): List<Article> = storedMap.values.toList()

    /** Returns the preconfigured article or null when none was stored. */
    override suspend fun load(id: String): Article? = storedMap[id]

    /** Creates a throwaway article; unused by the editor ViewModel. */
    override suspend fun create(title: String): Article =
        Article(id = "generated", title = title, markdown = "", createdAt = 0, updatedAt = 0, codeTheme = CodeThemeId.GITHUB_LIGHT, markdownTheme = MarkdownThemeId.GITHUB)

    /** Records every saved draft. */
    override suspend fun save(article: Article) {
        saved += article
    }

    /** Records the deletion; unused by the editor ViewModel. */
    override suspend fun delete(id: String) = Unit
}
