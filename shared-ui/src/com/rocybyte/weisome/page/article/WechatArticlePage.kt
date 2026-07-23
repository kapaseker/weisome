package com.rocybyte.weisome.page.article

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.rocybyte.weisome.generated.resources.Res
import com.rocybyte.weisome.generated.resources.markdown_hint
import com.rocybyte.weisome.page.article.biz.WechatArticleViewModel
import com.rocybyte.weisome.page.article.screen.WechatArticleEditorScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.jetbrains.compose.resources.stringResource

/** Navigation 3 destination for the WeChat article editor. */
@Composable
internal fun WechatArticlePage() {
    val hint = stringResource(Res.string.markdown_hint)
    val viewModel = koinViewModel<WechatArticleViewModel> { parametersOf(hint) }
    val state by viewModel.uiState.collectAsState()
    val layoutState by viewModel.layoutState.collectAsState()

    WechatArticleEditorScreen(
        state = state,
        layoutState = layoutState,
        onMarkdownChanged = viewModel::onMarkdownChanged,
        onCopyAsHtml = viewModel::copyAsHtml,
        onCopyForJuejin = viewModel::copyForJuejin,
        onLayoutModeSelected = viewModel::onLayoutModeSelected,
    )
}
