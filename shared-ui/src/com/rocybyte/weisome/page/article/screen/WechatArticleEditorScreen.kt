package com.rocybyte.weisome.page.article.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.rocybyte.weisome.generated.resources.Res
import com.rocybyte.weisome.generated.resources.app_name
import com.rocybyte.weisome.generated.resources.copy_button
import com.rocybyte.weisome.generated.resources.copy_failure
import com.rocybyte.weisome.generated.resources.copy_success
import com.rocybyte.weisome.generated.resources.markdown_hint
import com.rocybyte.weisome.generated.resources.markdown_label
import com.rocybyte.weisome.page.article.biz.WechatArticleUiState
import com.rocybyte.weisome.page.article.widget.WechatArticlePreview
import com.rocybyte.weisome.ui.WeiSomeDimensions
import org.jetbrains.compose.resources.stringResource

/** Renders the editor, preview, and copy controls for the article workflow. */
@Composable
internal fun WechatArticleEditorScreen(
    state: WechatArticleUiState,
    onMarkdownChanged: (String) -> Unit,
    onCopyAsHtml: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(WeiSomeDimensions.PagePadding),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(WeiSomeDimensions.ContentSpacing),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(stringResource(Res.string.app_name), style = MaterialTheme.typography.headlineMedium)
            Button(enabled = state.markdown.isNotBlank(), onClick = onCopyAsHtml) {
                Text(stringResource(Res.string.copy_button))
            }
        }
        state.copySucceeded?.let { succeeded ->
            val message = if (succeeded) stringResource(Res.string.copy_success) else stringResource(Res.string.copy_failure)
            Text(message, style = MaterialTheme.typography.bodyMedium)
        }
        val hint = stringResource(Res.string.markdown_hint)
        Row(Modifier.fillMaxWidth().weight(1f)) {
            OutlinedTextField(
                value = state.markdown,
                onValueChange = onMarkdownChanged,
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = { Text(stringResource(Res.string.markdown_label)) },
                placeholder = { Text(hint) },
                minLines = WeiSomeDimensions.EditorMinLines,
            )
            Box(
                Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState())
                    .padding(start = WeiSomeDimensions.ContentSpacing).alpha(if (state.markdown.isBlank()) 0.42f else 1f),
            ) {
                WechatArticlePreview(
                    document = state.preview,
                    modifier = Modifier.widthIn(max = WeiSomeDimensions.ContentMaxWidth),
                )
            }
        }
    }
}
