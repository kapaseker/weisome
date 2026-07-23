package com.rocybyte.weisome.page.article.biz

import androidx.lifecycle.ViewModel
import com.rocybyte.weisome.article.MarkdownDocument
import com.rocybyte.weisome.repository.article.WechatArticleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class WechatArticleUiState(
    val markdown: String = "",
    val preview: MarkdownDocument = MarkdownDocument(emptyList()),
    val copySucceeded: Boolean? = null,
    val copyTarget: ArticleCopyTarget? = null,
)

/** Identifies the destination used by the latest article copy operation. */
enum class ArticleCopyTarget {
    Wechat,
    Juejin,
}

class WechatArticleViewModel(
    private val repository: WechatArticleRepository,
    previewMarkdown: String,
) : ViewModel() {
    private val _uiState = MutableStateFlow(WechatArticleUiState(preview = repository.preview(previewMarkdown)))
    val uiState: StateFlow<WechatArticleUiState> = _uiState.asStateFlow()

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
}
