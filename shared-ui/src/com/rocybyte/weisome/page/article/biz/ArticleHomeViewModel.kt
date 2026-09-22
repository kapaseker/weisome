package com.rocybyte.weisome.page.article.biz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rocybyte.weisome.article.Article
import com.rocybyte.weisome.repository.article.ArticleRepo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ArticleHomeUiState(
    val isLoaded: Boolean = false,
    val articles: List<Article> = emptyList(),
)

/** 文章首页的 ViewModel:加载文章列表并处理创建、删除。 */
class ArticleHomeViewModel(
    private val repository: ArticleRepo,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ArticleHomeUiState())
    val uiState: StateFlow<ArticleHomeUiState> = _uiState.asStateFlow()

    init {
        reload()
    }

    /** 以给定标题创建新文章并刷新列表,返回创建结果供调用方导航。 */
    suspend fun create(title: String): Article {
        val article = repository.create(title)
        reloadList()
        return article
    }

    /** 删除指定文章并刷新列表,失败时仅打印并保留原列表。 */
    fun delete(id: String) {
        viewModelScope.launch {
            try {
                repository.delete(id)
                reloadList()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                System.err.println("Unable to delete article: ${error.message}")
            }
        }
    }

    /** 在后台协程中重新加载文章列表。 */
    private fun reload() {
        viewModelScope.launch { reloadList() }
    }

    /** 读取全部文章并更新状态;失败时降级为空列表,不阻断首页展示。 */
    private suspend fun reloadList() {
        try {
            val articles = repository.list()
            _uiState.update { it.copy(isLoaded = true, articles = articles) }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            System.err.println("Unable to load articles: ${error.message}")
            _uiState.update { it.copy(isLoaded = true) }
        }
    }
}
