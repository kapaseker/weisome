package com.rocybyte.weisome.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.rocybyte.weisome.generated.resources.*
import org.jetbrains.compose.resources.stringResource

/** Renders the root content of the WeiSome desktop application. */
@Composable
fun WeiSomeApp() {
    WeiSomeTheme {
        var markdown by remember { mutableStateOf("") }
        var copySucceeded by remember { mutableStateOf<Boolean?>(null) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WeiSomeDimensions.PagePadding),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(WeiSomeDimensions.ContentSpacing),
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(Res.string.app_name), style = MaterialTheme.typography.headlineMedium)
                Button(enabled = markdown.isNotBlank(), onClick = { copySucceeded = runCatching { WechatHtmlClipboard.copy(MarkdownToWechatHtml.render(markdown)) }.isSuccess }) { Text(stringResource(Res.string.copy_button)) }
            }
            copySucceeded?.let { succeeded ->
                val message = if (succeeded) {
                    stringResource(Res.string.copy_success)
                } else {
                    stringResource(Res.string.copy_failure)
                }
                Text(message, style = MaterialTheme.typography.bodyMedium)
            }
            val hint = stringResource(Res.string.markdown_hint)
            val previewMarkdown = markdown.ifBlank { hint }
            Row(Modifier.fillMaxWidth().weight(1f)) {
                OutlinedTextField(value = markdown, onValueChange = { markdown = it; copySucceeded = null }, modifier = Modifier.weight(1f).fillMaxHeight(), label = { Text(stringResource(Res.string.markdown_label)) }, placeholder = { Text(hint) }, minLines = WeiSomeDimensions.EditorMinLines)
                Box(Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState()).padding(start = WeiSomeDimensions.ContentSpacing).alpha(if (markdown.isBlank()) 0.42f else 1f)) {
                    WechatArticlePreview(MarkdownDocumentParser.parse(previewMarkdown), Modifier.widthIn(max = WeiSomeDimensions.ContentMaxWidth))
                }
            }
        }
    }
}
