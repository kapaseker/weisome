package com.rocybyte.weisome.page.article.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.rocybyte.weisome.generated.resources.Res
import com.rocybyte.weisome.generated.resources.article_delete
import com.rocybyte.weisome.generated.resources.article_delete_hint
import com.rocybyte.weisome.generated.resources.dialog_cancel
import com.rocybyte.weisome.generated.resources.dialog_confirm
import com.rocybyte.weisome.ui.WeiSomeBorders
import com.rocybyte.weisome.ui.WeiSomeColors
import com.rocybyte.weisome.ui.WeiSomeShapes
import com.rocybyte.weisome.ui.WeiSomeSpacing
import com.rocybyte.weisome.ui.WeiSomeTypography
import com.rocybyte.weisome.widget.WeiSomeSecondaryButton
import com.rocybyte.weisome.widget.WeiSomeText
import org.jetbrains.compose.resources.stringResource

/**
 * 删除文章的二次确认弹窗,外壳与 [ArticleTitleDialog] 一致(DESIGN.md Modals 规格)。
 *
 * @param title 待删除文章的标题,用于提示文案。
 * @param onConfirm 用户确认删除时回调。
 * @param onDismiss 取消或点击弹窗外关闭时回调。
 */
@Composable
internal fun ArticleDeleteDialog(
    title: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(WeiSomeShapes.lg)
                .background(WeiSomeColors.surfaceContainerLowest)
                .border(WeiSomeBorders.thin, WeiSomeColors.outlineVariant, WeiSomeShapes.lg)
                .padding(WeiSomeSpacing.stackMd),
            verticalArrangement = Arrangement.spacedBy(WeiSomeSpacing.stackSm),
        ) {
            WeiSomeText(text = stringResource(Res.string.article_delete), style = WeiSomeTypography.h3)
            WeiSomeText(
                text = stringResource(Res.string.article_delete_hint, title),
                style = WeiSomeTypography.bodyMd,
                color = WeiSomeColors.onSurfaceVariant,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(WeiSomeSpacing.stackSm)) {
                Spacer(Modifier.weight(1f))
                WeiSomeSecondaryButton(text = stringResource(Res.string.dialog_cancel), onClick = onDismiss)
                ErrorConfirmButton(text = stringResource(Res.string.dialog_confirm), onClick = onConfirm)
            }
        }
    }
}

/** 破坏性操作的确认按钮:error 底色 + 白字,样式尺寸与 WeiSomePrimaryButton 对齐。 */
@Composable
private fun ErrorConfirmButton(
    text: String,
    onClick: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .heightIn(min = 40.dp)
            .clip(WeiSomeShapes.default)
            .background(WeiSomeColors.error)
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
    ) {
        WeiSomeText(text = text, style = WeiSomeTypography.labelSm, color = WeiSomeColors.onError)
    }
}
