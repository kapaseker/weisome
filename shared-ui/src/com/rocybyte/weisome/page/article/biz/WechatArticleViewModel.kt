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
)

class WechatArticleViewModel(
    private val repository: WechatArticleRepository,
    previewMarkdown: String,
) : ViewModel() {
    private val _uiState = MutableStateFlow(WechatArticleUiState(preview = repository.preview(previewMarkdown)))
    val uiState: StateFlow<WechatArticleUiState> = _uiState.asStateFlow()

    fun onMarkdownChanged(markdown: String) {
        _uiState.update {
            it.copy(markdown = markdown, preview = repository.preview(markdown), copySucceeded = null)
        }
    }

    fun copyAsHtml() {
        val markdown = _uiState.value.markdown
        if (markdown.isBlank()) return
        _uiState.update { it.copy(copySucceeded = repository.copyAsHtml(markdown)) }
    }
}
