package com.rocybyte.weisome.page.article

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.rocybyte.weisome.page.article.biz.ArticleHomeViewModel
import com.rocybyte.weisome.page.article.screen.ArticleHomeScreen
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

/** Navigation 3 destination for the article home (list) screen. */
@Composable
internal fun ArticleHomePage(
    onOpenArticle: (String) -> Unit,
    onOpenSettings: () -> Unit,
) {
    val viewModel = koinViewModel<ArticleHomeViewModel>()
    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    ArticleHomeScreen(
        state = state,
        onCreateConfirm = { title ->
            scope.launch {
                val article = viewModel.create(title)
                onOpenArticle(article.id)
            }
        },
        onDeleteArticle = viewModel::delete,
        onOpenArticle = onOpenArticle,
        onOpenSettings = onOpenSettings,
    )
}
