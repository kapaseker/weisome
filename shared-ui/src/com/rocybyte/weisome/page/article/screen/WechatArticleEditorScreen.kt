package com.rocybyte.weisome.page.article.screen

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import com.rocybyte.weisome.article.ArticleLayoutMode
import com.rocybyte.weisome.generated.resources.Res
import com.rocybyte.weisome.generated.resources.article_layout_editor_only
import com.rocybyte.weisome.generated.resources.article_layout_preview_only
import com.rocybyte.weisome.generated.resources.article_layout_split
import com.rocybyte.weisome.generated.resources.article_title_label
import com.rocybyte.weisome.generated.resources.article_title_placeholder
import com.rocybyte.weisome.generated.resources.back
import com.rocybyte.weisome.generated.resources.copy_button
import com.rocybyte.weisome.generated.resources.copy_failure
import com.rocybyte.weisome.generated.resources.copy_success
import com.rocybyte.weisome.generated.resources.ic_all_expand
import com.rocybyte.weisome.generated.resources.ic_copy
import com.rocybyte.weisome.generated.resources.ic_left
import com.rocybyte.weisome.generated.resources.ic_left_expand
import com.rocybyte.weisome.generated.resources.ic_right_expand
import com.rocybyte.weisome.generated.resources.ic_settings
import com.rocybyte.weisome.generated.resources.markdown_hint
import com.rocybyte.weisome.generated.resources.settings
import com.rocybyte.weisome.page.article.biz.ArticleLayoutUiState
import com.rocybyte.weisome.page.article.biz.WechatArticleUiState
import com.rocybyte.weisome.page.article.widget.ArticleTitleDialog
import com.rocybyte.weisome.page.article.widget.WechatArticlePreview
import com.rocybyte.weisome.ui.WeiSomeColors
import com.rocybyte.weisome.ui.WeiSomeSpacing
import com.rocybyte.weisome.ui.WeiSomeTypography
import com.rocybyte.weisome.widget.LocalWeiSomeSnackbar
import com.rocybyte.weisome.widget.MediumIconButton
import com.rocybyte.weisome.widget.WeiSomeSecondaryButton
import com.rocybyte.weisome.widget.WeiSomeSegmentedControl
import com.rocybyte.weisome.widget.WeiSomeSegmentedOption
import com.rocybyte.weisome.widget.WeiSomeText
import com.rocybyte.weisome.widget.WeiSomeTextField
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** Minimum editor height in text lines before the field starts scrolling. */
private const val EDITOR_MIN_LINES = 12

/** Renders the editor, preview, and copy controls for the article workflow. */
@Composable
internal fun WechatArticleEditorScreen(
    state: WechatArticleUiState,
    layoutState: ArticleLayoutUiState,
    onBack: () -> Unit,
    onMarkdownChanged: (String) -> Unit,
    onTitleChanged: (String) -> Unit,
    onCopyAsHtml: () -> Unit,
    onDismissCopyStatus: () -> Unit,
    onLayoutModeSelected: (ArticleLayoutMode) -> Unit,
    onOpenSettings: () -> Unit,
) {
    var showRenameDialog by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(WeiSomeSpacing.margin),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(WeiSomeSpacing.stackSm),
        ) {
            Box(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    MediumIconButton(
                        onClick = onBack,
                        painter = painterResource(Res.drawable.ic_left),
                        contentDescription = stringResource(Res.string.back),
                    )
                    val titleInteractionSource = remember { MutableInteractionSource() }
                    val titleHovered by titleInteractionSource.collectIsHoveredAsState()
                    // 标题占据返回按钮与右侧操作区之间的剩余宽度,hover 变 primary 色提示可点击改名。
                    WeiSomeText(
                        text = state.title,
                        style = WeiSomeTypography.h2,
                        color = if (titleHovered) WeiSomeColors.primary else WeiSomeColors.onSurface,
                        maxLines = 1,
                        modifier = Modifier
                            .weight(1f)
                            .hoverable(titleInteractionSource)
                            .clickable(
                                interactionSource = titleInteractionSource,
                                indication = null,
                                onClick = { showRenameDialog = true },
                            ),
                    )
                    WeiSomeSecondaryButton(
                        text = stringResource(Res.string.copy_button),
                        onClick = onCopyAsHtml,
                        enabled = state.markdown.isNotBlank(),
                        icon = painterResource(Res.drawable.ic_copy),
                    )
                }
                if (layoutState.isLoaded) {
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Spacer(Modifier.size(52.dp))
                        Spacer(Modifier.size(width = WeiSomeSpacing.stackSm, height = 1.dp))
                        ArticleLayoutSelector(
                            selectedMode = layoutState.mode,
                            onModeSelected = onLayoutModeSelected,
                        )
                        Spacer(Modifier.size(width = WeiSomeSpacing.stackSm, height = 1.dp))
                        MediumIconButton(
                            onClick = onOpenSettings,
                            painter = painterResource(Res.drawable.ic_settings),
                            contentDescription = stringResource(Res.string.settings),
                        )
                    }
                }
            }
            val hint = stringResource(Res.string.markdown_hint)
            if (layoutState.isLoaded && state.isArticleLoaded) {
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
        CopyStatusSnackbar(
            status = state.copySucceeded,
            onDismiss = onDismissCopyStatus,
        )
    }
    if (showRenameDialog) {
        ArticleTitleDialog(
            title = stringResource(Res.string.article_title_label),
            initialValue = state.title,
            placeholder = stringResource(Res.string.article_title_placeholder),
            onConfirm = { title ->
                onTitleChanged(title)
                showRenameDialog = false
            },
            onDismiss = { showRenameDialog = false },
        )
    }
}

/**
 * Bridges the one-shot copy result into the global snackbar: shows the feedback once the
 * state emits a result, then clears the source state so the same result cannot re-show.
 */
@Composable
private fun CopyStatusSnackbar(
    status: Boolean?,
    onDismiss: () -> Unit,
) {
    val snackbar = LocalWeiSomeSnackbar.current
    val successMessage = stringResource(Res.string.copy_success)
    val failureMessage = stringResource(Res.string.copy_failure)

    LaunchedEffect(status) {
        if (status != null) {
            snackbar.show(
                message = if (status) successMessage else failureMessage,
                isError = !status,
            )
            onDismiss()
        }
    }
}

/** Renders the centered icon-only single-choice control for article layout modes. */
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
    val options = modes.map { mode ->
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
        WeiSomeSegmentedOption(painter = painterResource(icon), contentDescription = description)
    }

    WeiSomeSegmentedControl(
        options = options,
        selectedIndex = modes.indexOf(selectedMode),
        onSelected = { index -> onModeSelected(modes[index]) },
        modifier = modifier,
    )
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
    val editorScroll = rememberScrollState()
    val previewScroll = rememberScrollState()
    Row(modifier) {
        when (mode) {
            ArticleLayoutMode.EDITOR_ONLY -> ArticleEditor(
                state = state,
                hint = hint,
                onMarkdownChanged = onMarkdownChanged,
                modifier = Modifier.fillMaxSize(),
                scrollState = editorScroll,
            )

            ArticleLayoutMode.SPLIT -> {
                val textLayout = remember { mutableStateOf<TextLayoutResult?>(null) }
                // Re-created whenever the document changes so stale block positions never survive a re-parse.
                val blockTops = remember(state.preview) { mutableStateMapOf<Int, Float>() }
                SplitScrollSync(
                    editorScroll = editorScroll,
                    previewScroll = previewScroll,
                    textLayout = textLayout,
                    blockRanges = state.preview.blockRanges,
                    blockTops = blockTops,
                )
                ArticleEditor(
                    state = state,
                    hint = hint,
                    onMarkdownChanged = onMarkdownChanged,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    scrollState = editorScroll,
                    onTextLayout = { textLayout.value = it },
                )
                ArticlePreviewPane(
                    state = state,
                    scrollState = previewScroll,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    onBlockPositioned = { index, top -> blockTops[index] = top },
                )
            }

            ArticleLayoutMode.PREVIEW_ONLY -> ArticlePreviewPane(
                state = state,
                scrollState = previewScroll,
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
    scrollState: ScrollState? = null,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
) {
    WeiSomeTextField(
        value = state.markdown,
        onValueChange = onMarkdownChanged,
        modifier = modifier,
        placeholder = hint,
        minLines = EDITOR_MIN_LINES,
        scrollState = scrollState,
        onTextLayout = onTextLayout,
    )
}

/** Renders the scrolling preview at the full width offered by its current layout region. */
@Composable
private fun ArticlePreviewPane(
    state: WechatArticleUiState,
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    onBlockPositioned: ((blockIndex: Int, topPx: Float) -> Unit)? = null,
) {
    Box(
        modifier.verticalScroll(scrollState)
            .padding(start = WeiSomeSpacing.stackSm)
            .alpha(if (state.markdown.isBlank()) 0.42f else 1f),
    ) {
        WechatArticlePreview(
            document = state.preview,
            modifier = Modifier.fillMaxWidth(),
            onBlockPositioned = onBlockPositioned,
        )
    }
}
