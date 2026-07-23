package com.rocybyte.weisome.page.article.biz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rocybyte.weisome.article.ArticleLayoutMode
import com.rocybyte.weisome.article.MarkdownDocument
import com.rocybyte.weisome.repository.article.ArticleLayoutRepo
import com.rocybyte.weisome.repository.article.WechatArticleRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WechatArticleUiState(
    val markdown: String = "",
    val preview: MarkdownDocument = MarkdownDocument(emptyList()),
    val copySucceeded: Boolean? = null,
    val copyTarget: ArticleCopyTarget? = null,
)

data class ArticleLayoutUiState(
    val isLoaded: Boolean = false,
    val mode: ArticleLayoutMode = ArticleLayoutMode.SPLIT,
)

/** Identifies the destination used by the latest article copy operation. */
enum class ArticleCopyTarget {
    Wechat,
    Juejin,
}

class WechatArticleViewModel(
    private val repository: WechatArticleRepository,
    private val layoutRepository: ArticleLayoutRepo,
    previewMarkdown: String,
) : ViewModel() {
    private val _uiState = MutableStateFlow(WechatArticleUiState(preview = repository.preview(previewMarkdown)))
    val uiState: StateFlow<WechatArticleUiState> = _uiState.asStateFlow()
    private val _layoutState = MutableStateFlow(ArticleLayoutUiState())
    val layoutState: StateFlow<ArticleLayoutUiState> = _layoutState.asStateFlow()
    private var layoutSaveJob: Job? = null

    init {
        viewModelScope.launch {
            val restoredMode = try {
                layoutRepository.load()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                System.err.println("Unable to load article layout: ${error.message}")
                null
            }
            _layoutState.value = ArticleLayoutUiState(
                isLoaded = true,
                mode = restoredMode ?: ArticleLayoutMode.SPLIT,
            )
        }
    }

    /** Updates the source and preview while clearing the previous copy result. */
    fun onMarkdownChanged(markdown: String) {
        _uiState.update {
            it.copy(
                markdown = markdown,
                preview = repository.preview(markdown),
                copySucceeded = null,
                copyTarget = null,
            )
        }
    }

    /** Copies the current non-blank article and records whether the operation succeeded. */
    fun copyAsHtml() {
        val markdown = _uiState.value.markdown
        if (markdown.isBlank()) return
        _uiState.update {
            it.copy(
                copySucceeded = repository.copyAsHtml(markdown),
                copyTarget = ArticleCopyTarget.Wechat,
            )
        }
    }

    /** Copies the original Markdown for Juejin and records the result. */
    fun copyForJuejin() {
        val markdown = _uiState.value.markdown
        if (markdown.isBlank()) return
        _uiState.update {
            it.copy(
                copySucceeded = repository.copyForJuejin(markdown),
                copyTarget = ArticleCopyTarget.Juejin,
            )
        }
    }

    /** Applies a layout mode immediately and persists the latest selection in the background. */
    fun onLayoutModeSelected(mode: ArticleLayoutMode) {
        if (!_layoutState.value.isLoaded || _layoutState.value.mode == mode) return

        _layoutState.value = ArticleLayoutUiState(isLoaded = true, mode = mode)
        layoutSaveJob?.cancel()
        layoutSaveJob = viewModelScope.launch {
            try {
                layoutRepository.save(mode)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                System.err.println("Unable to save article layout: ${error.message}")
            }
        }
    }
}
