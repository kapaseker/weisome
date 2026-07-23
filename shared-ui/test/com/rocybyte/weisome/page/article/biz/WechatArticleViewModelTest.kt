package com.rocybyte.weisome.page.article.biz

import com.rocybyte.weisome.article.MarkdownDocument
import com.rocybyte.weisome.repository.article.WechatArticleRepository
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
        val viewModel = WechatArticleViewModel(repository, "Welcome")

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
        val viewModel = WechatArticleViewModel(repository, "Welcome")

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
        val viewModel = WechatArticleViewModel(repository, "Welcome")

        viewModel.onMarkdownChanged("Article")
        viewModel.copyForJuejin()

        assertTrue(repository.juejinCopyCalled)
        assertEquals(ArticleCopyTarget.Juejin, viewModel.uiState.value.copyTarget)
        assertTrue(viewModel.uiState.value.copySucceeded!!)
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
