package com.rocybyte.weisome.page.article.biz

import com.rocybyte.weisome.article.ArticleLayoutMode
import com.rocybyte.weisome.article.MarkdownDocument
import com.rocybyte.weisome.repository.article.ArticleLayoutRepo
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
        val viewModel = WechatArticleViewModel(repository, FakeArticleLayoutRepo(), "Welcome")

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
        val viewModel = WechatArticleViewModel(repository, FakeArticleLayoutRepo(), "Welcome")

        viewModel.copyAsHtml()
        assertFalse(repository.copyCalled)

        viewModel.onMarkdownChanged("Article")
        viewModel.copyAsHtml()

        assertTrue(repository.copyCalled)
        assertFalse(viewModel.uiState.value.copySucceeded!!)
        assertEquals(ArticleCopyTarget.Wechat, viewModel.uiState.value.copyTarget)
    }

    @Test
    /** Verifies Juejin copies use the dedicated repository path and destination state. */
    fun `copies Markdown through the Juejin repository path`() {
        val repository = FakeWechatArticleRepository()
        val viewModel = WechatArticleViewModel(repository, FakeArticleLayoutRepo(), "Welcome")

        viewModel.onMarkdownChanged("Article")
        viewModel.copyForJuejin()

        assertTrue(repository.juejinCopyCalled)
        assertEquals(ArticleCopyTarget.Juejin, viewModel.uiState.value.copyTarget)
        assertTrue(viewModel.uiState.value.copySucceeded!!)
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

    /** Creates an article ViewModel and waits for its persisted layout read to finish. */
    private suspend fun loadedViewModel(layoutRepository: ArticleLayoutRepo): WechatArticleViewModel {
        val viewModel = WechatArticleViewModel(FakeWechatArticleRepository(), layoutRepository, "Welcome")
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
    var copyCalled = false
    var juejinCopyCalled = false

    /** Records preview input and returns a minimal document fixture. */
    override fun preview(markdown: String): MarkdownDocument {
        lastPreviewMarkdown = markdown
        return MarkdownDocument(emptyList())
    }

    /** Records copied Markdown and returns the configured result. */
    override fun copyAsHtml(markdown: String): Boolean {
        copyCalled = true
        return copyResult
    }

    /** Records Juejin copy requests and returns the configured result. */
    override fun copyForJuejin(markdown: String): Boolean {
        juejinCopyCalled = true
        return copyResult
    }
}
