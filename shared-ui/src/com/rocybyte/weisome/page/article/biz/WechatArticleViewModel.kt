package com.rocybyte.weisome.page.article.biz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rocybyte.weisome.article.Article
import com.rocybyte.weisome.article.ArticleLayoutMode
import com.rocybyte.weisome.article.MarkdownDocument
import com.rocybyte.weisome.repository.article.ArticleLayoutRepo
import com.rocybyte.weisome.repository.article.ArticleRepo
import com.rocybyte.weisome.repository.article.WechatArticleRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class WechatArticleUiState(
    val title: String = "",
    val markdown: String = "",
    val preview: MarkdownDocument = MarkdownDocument(emptyList()),
    val copySucceeded: Boolean? = null,
    val isArticleLoaded: Boolean = false,
)

data class ArticleLayoutUiState(
    val isLoaded: Boolean = false,
    val mode: ArticleLayoutMode = ArticleLayoutMode.SPLIT,
)

/** 文章编辑页的 ViewModel:加载草稿、维护标题/正文/预览,并按固定周期自动保存。 */
class WechatArticleViewModel(
    private val repository: WechatArticleRepository,
    private val layoutRepository: ArticleLayoutRepo,
    private val articleRepository: ArticleRepo,
    previewMarkdown: String,
    private val articleId: String,
    private val autoSaveIntervalMillis: Long = 5_000,
) : ViewModel() {
    private val _uiState = MutableStateFlow(WechatArticleUiState(preview = repository.preview(previewMarkdown)))
    val uiState: StateFlow<WechatArticleUiState> = _uiState.asStateFlow()
    private val _layoutState = MutableStateFlow(ArticleLayoutUiState())
    val layoutState: StateFlow<ArticleLayoutUiState> = _layoutState.asStateFlow()
    private var layoutSaveJob: Job? = null

    // ponytail: onCleared 里的最终保存是 fire-and-forget,进程若在写库完成前退出,
    // 最多丢最后一个保存周期内的编辑;桌面常驻进程场景可接受。
    private val finalSaveScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var lastSavedTitle: String = ""
    private var lastSavedMarkdown: String = ""
    private var createdAt: Long = 0

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
        viewModelScope.launch { loadArticle() }
        viewModelScope.launch {
            while (isActive) {
                delay(autoSaveIntervalMillis)
                saveIfDirty()
            }
        }
    }

    /** Updates the source and preview while clearing the previous copy result. */
    fun onMarkdownChanged(markdown: String) {
        _uiState.update {
            it.copy(
                markdown = markdown,
                preview = repository.preview(markdown),
                copySucceeded = null,
            )
        }
    }

    /** Updates the article title; the periodic draft save persists it together with the source. */
    fun onTitleChanged(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    /** Copies the current non-blank article and records whether the operation succeeded. */
    fun copyAsHtml() {
        val markdown = _uiState.value.markdown
        if (markdown.isBlank()) return
        _uiState.update {
            it.copy(copySucceeded = repository.copyAsHtml(markdown))
        }
    }

    /** Clears the transient copy feedback once it has been shown. */
    fun dismissCopyStatus() {
        _uiState.update { it.copy(copySucceeded = null) }
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

    override fun onCleared() {
        finalSaveScope.launch { saveIfDirty() }
    }

    /** 读取指定文章并填充标题、正文与预览;文章缺失时按空文章继续,后续自动保存会重建记录。 */
    private suspend fun loadArticle() {
        val article = try {
            articleRepository.load(articleId)
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            System.err.println("Unable to load article: ${error.message}")
            null
        }
        if (article == null) {
            createdAt = System.currentTimeMillis()
            _uiState.update { it.copy(isArticleLoaded = true) }
            return
        }
        createdAt = article.createdAt
        lastSavedTitle = article.title
        lastSavedMarkdown = article.markdown
        _uiState.update {
            it.copy(
                title = article.title,
                markdown = article.markdown,
                preview = if (article.markdown.isNotBlank()) repository.preview(article.markdown) else it.preview,
                isArticleLoaded = true,
            )
        }
    }

    /** 标题或正文相对上次保存有变化时写入数据库,并刷新已保存基线;失败仅打印,等待下个周期重试。 */
    private suspend fun saveIfDirty() {
        val state = _uiState.value
        if (state.title == lastSavedTitle && state.markdown == lastSavedMarkdown) return
        try {
            articleRepository.save(
                Article(
                    id = articleId,
                    title = state.title,
                    markdown = state.markdown,
                    createdAt = createdAt,
                    updatedAt = System.currentTimeMillis(),
                ),
            )
            lastSavedTitle = state.title
            lastSavedMarkdown = state.markdown
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            System.err.println("Unable to save article draft: ${error.message}")
        }
    }
}
