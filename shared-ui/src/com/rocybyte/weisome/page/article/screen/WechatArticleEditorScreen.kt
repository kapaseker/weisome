package com.rocybyte.weisome.page.article.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.rocybyte.weisome.article.ArticleLayoutMode
import com.rocybyte.weisome.generated.resources.Res
import com.rocybyte.weisome.generated.resources.app_name
import com.rocybyte.weisome.generated.resources.article_layout_editor_only
import com.rocybyte.weisome.generated.resources.article_layout_preview_only
import com.rocybyte.weisome.generated.resources.article_layout_split
import com.rocybyte.weisome.generated.resources.copy_button
import com.rocybyte.weisome.generated.resources.copy_failure
import com.rocybyte.weisome.generated.resources.copy_juejin_button
import com.rocybyte.weisome.generated.resources.copy_juejin_success
import com.rocybyte.weisome.generated.resources.copy_success
import com.rocybyte.weisome.generated.resources.ic_all_expand
import com.rocybyte.weisome.generated.resources.ic_left_expand
import com.rocybyte.weisome.generated.resources.ic_right_expand
import com.rocybyte.weisome.generated.resources.markdown_hint
import com.rocybyte.weisome.generated.resources.markdown_label
import com.rocybyte.weisome.page.article.biz.ArticleCopyTarget
import com.rocybyte.weisome.page.article.biz.ArticleLayoutUiState
import com.rocybyte.weisome.page.article.biz.WechatArticleUiState
import com.rocybyte.weisome.page.article.widget.WechatArticlePreview
import com.rocybyte.weisome.ui.WeiSomeDimensions
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** Renders the editor, preview, and copy controls for the article workflow. */
@Composable
internal fun WechatArticleEditorScreen(
    state: WechatArticleUiState,
    layoutState: ArticleLayoutUiState,
    onMarkdownChanged: (String) -> Unit,
    onCopyAsHtml: () -> Unit,
    onCopyForJuejin: () -> Unit,
    onLayoutModeSelected: (ArticleLayoutMode) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(WeiSomeDimensions.PagePadding),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(WeiSomeDimensions.ContentSpacing),
    ) {
        Box(Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(Res.string.app_name), style = MaterialTheme.typography.headlineMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(WeiSomeDimensions.ContentSpacing)) {
                    Button(enabled = state.markdown.isNotBlank(), onClick = onCopyForJuejin) {
                        Text(stringResource(Res.string.copy_juejin_button))
                    }
                    Button(enabled = state.markdown.isNotBlank(), onClick = onCopyAsHtml) {
                        Text(stringResource(Res.string.copy_button))
                    }
                }
            }
            if (layoutState.isLoaded) {
                ArticleLayoutSelector(
                    selectedMode = layoutState.mode,
                    onModeSelected = onLayoutModeSelected,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
        state.copySucceeded?.let { succeeded ->
            val message = when {
                !succeeded -> stringResource(Res.string.copy_failure)
                state.copyTarget == ArticleCopyTarget.Juejin -> stringResource(Res.string.copy_juejin_success)
                else -> stringResource(Res.string.copy_success)
            }
            Text(message, style = MaterialTheme.typography.bodyMedium)
        }
        val hint = stringResource(Res.string.markdown_hint)
        if (layoutState.isLoaded) {
            ArticleWorkspace(
                state = state,
                mode = layoutState.mode,
                hint = hint,
                onMarkdownChanged = onMarkdownChanged,
                modifier = Modifier.fillMaxWidth().weight(1f),
            )
        } else {
            Spacer(Modifier.weight(1f))
        }
    }
}

/** Renders the centered icon-only single-choice control for article layout modes. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArticleLayoutSelector(
    selectedMode: ArticleLayoutMode,
    onModeSelected: (ArticleLayoutMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val modes = listOf(
        ArticleLayoutMode.EDITOR_ONLY,
        ArticleLayoutMode.SPLIT,
        ArticleLayoutMode.PREVIEW_ONLY,
    )
    val editorOnlyDescription = stringResource(Res.string.article_layout_editor_only)
    val splitDescription = stringResource(Res.string.article_layout_split)
    val previewOnlyDescription = stringResource(Res.string.article_layout_preview_only)

    SingleChoiceSegmentedButtonRow(modifier) {
        modes.forEachIndexed { index, mode ->
            val description = when (mode) {
                ArticleLayoutMode.EDITOR_ONLY -> editorOnlyDescription
                ArticleLayoutMode.SPLIT -> splitDescription
                ArticleLayoutMode.PREVIEW_ONLY -> previewOnlyDescription
            }
            val icon = when (mode) {
                ArticleLayoutMode.EDITOR_ONLY -> Res.drawable.ic_left_expand
                ArticleLayoutMode.SPLIT -> Res.drawable.ic_all_expand
                ArticleLayoutMode.PREVIEW_ONLY -> Res.drawable.ic_right_expand
            }
            SegmentedButton(
                selected = selectedMode == mode,
                onClick = { onModeSelected(mode) },
                shape = SegmentedButtonDefaults.itemShape(index, modes.size),
                icon = {},
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = description,
                )
            }
        }
    }
}

/** Displays the editor, preview, or equal split according to the selected layout mode. */
@Composable
private fun ArticleWorkspace(
    state: WechatArticleUiState,
    mode: ArticleLayoutMode,
    hint: String,
    onMarkdownChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier) {
        when (mode) {
            ArticleLayoutMode.EDITOR_ONLY -> ArticleEditor(
                state = state,
                hint = hint,
                onMarkdownChanged = onMarkdownChanged,
                modifier = Modifier.fillMaxSize(),
            )

            ArticleLayoutMode.SPLIT -> {
                ArticleEditor(
                    state = state,
                    hint = hint,
                    onMarkdownChanged = onMarkdownChanged,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
                ArticlePreviewPane(
                    state = state,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
            }

            ArticleLayoutMode.PREVIEW_ONLY -> ArticlePreviewPane(
                state = state,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

/** Renders the Markdown source editor within the supplied layout bounds. */
@Composable
private fun ArticleEditor(
    state: WechatArticleUiState,
    hint: String,
    onMarkdownChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = state.markdown,
        onValueChange = onMarkdownChanged,
        modifier = modifier,
        label = { Text(stringResource(Res.string.markdown_label)) },
        placeholder = { Text(hint) },
        minLines = WeiSomeDimensions.EditorMinLines,
    )
}

/** Renders the scrolling preview at the full width offered by its current layout region. */
@Composable
private fun ArticlePreviewPane(
    state: WechatArticleUiState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier.verticalScroll(rememberScrollState())
            .padding(start = WeiSomeDimensions.ContentSpacing)
            .alpha(if (state.markdown.isBlank()) 0.42f else 1f),
    ) {
        WechatArticlePreview(
            document = state.preview,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
