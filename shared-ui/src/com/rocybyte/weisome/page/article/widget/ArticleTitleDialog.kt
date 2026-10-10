package com.rocybyte.weisome.page.article.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.unit.dp
import com.rocybyte.weisome.generated.resources.Res
import com.rocybyte.weisome.generated.resources.dialog_cancel
import com.rocybyte.weisome.generated.resources.dialog_confirm
import com.rocybyte.weisome.ui.WeiSomeBorders
import com.rocybyte.weisome.ui.WeiSomeColors
import com.rocybyte.weisome.ui.WeiSomeShapes
import com.rocybyte.weisome.ui.WeiSomeSpacing
import com.rocybyte.weisome.ui.WeiSomeTypography
import com.rocybyte.weisome.widget.WeiSomePrimaryButton
import com.rocybyte.weisome.widget.WeiSomeSecondaryButton
import com.rocybyte.weisome.widget.WeiSomeText
import org.jetbrains.compose.resources.stringResource

/**
 * 文章标题输入弹窗,新建与改名共用。按 DESIGN.md Modals 规格绘制:
 * 白色表面、thin 边框、lg 圆角;输入框自动获得焦点且光标定位在已有文字末尾,
 * 标题为空白时确认按钮禁用、按 Enter 也不生效。
 *
 * @param title 弹窗的标题文案。
 * @param initialValue 输入框的初始值,新建时为空串。
 * @param placeholder 输入框占位文案。
 * @param onConfirm 校验通过后回调,参数为去除首尾空白的标题。
 * @param onDismiss 取消或点击弹窗外关闭时回调。
 */
@Composable
internal fun ArticleTitleDialog(
    title: String,
    initialValue: String,
    placeholder: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    // selection 初始定位到末尾,聚焦后光标落在已有文字后面而非开头。
    var value by remember {
        mutableStateOf(TextFieldValue(initialValue, selection = TextRange(initialValue.length)))
    }
    val focusRequester = remember { FocusRequester() }
    // 确认动作:空白标题不生效,与确认按钮的禁用态共用同一校验。
    val confirm: () -> Unit = { if (value.text.isNotBlank()) onConfirm(value.text.trim()) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(WeiSomeShapes.large)
                .background(WeiSomeColors.surfaceContainerLowest)
                .border(WeiSomeBorders.thin, WeiSomeColors.outlineVariant, WeiSomeShapes.large)
                .padding(WeiSomeSpacing.stackMedium),
            verticalArrangement = Arrangement.spacedBy(WeiSomeSpacing.stackSmall),
        ) {
            WeiSomeText(text = title, style = WeiSomeTypography.h3)
            TitleTextField(
                value = value,
                onValueChange = { value = it },
                placeholder = placeholder,
                onDone = confirm,
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(WeiSomeSpacing.stackSmall)) {
                Spacer(Modifier.weight(1f))
                WeiSomeSecondaryButton(text = stringResource(Res.string.dialog_cancel), onClick = onDismiss)
                WeiSomePrimaryButton(
                    text = stringResource(Res.string.dialog_confirm),
                    onClick = confirm,
                    enabled = value.text.isNotBlank(),
                )
            }
        }
    }

    // 弹窗打开后立即聚焦输入框,方便直接开始输入。
    LaunchedEffect(Unit) { focusRequester.requestFocus() }
}

/**
 * 弹窗专用的单行标题输入框,按 DESIGN.md Input Fields 规格:浅灰底(#F9F9FB)聚焦变白,
 * 聚焦时 2px 主色描边,默认 8px 圆角。按下 Enter 触发 [onDone],与点击确认按钮等效。
 * 无滚动、无外部滚动接管等编辑器专属能力。
 *
 * @param value 输入内容,含光标选区状态。
 * @param onValueChange 内容变化回调。
 * @param placeholder 内容为空时的占位文案。
 * @param onDone 按下 Enter(IME Done)时回调。
 * @param modifier 应用于输入框的修饰符。
 */
@Composable
private fun TitleTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var focused by remember { mutableStateOf(false) }
    val shape = WeiSomeShapes.default

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        textStyle = WeiSomeTypography.bodyMd.copy(color = WeiSomeColors.onSurface),
        cursorBrush = SolidColor(WeiSomeColors.primary),
        modifier = modifier
            .onFocusChanged { focused = it.isFocused }
            .border(
                if (focused) WeiSomeBorders.focusBorder else WeiSomeBorders.thin,
                if (focused) WeiSomeColors.primary else WeiSomeColors.outlineVariant,
                shape,
            )
            .background(
                if (focused) WeiSomeColors.surfaceContainerLowest else WeiSomeColors.inputBg,
                shape,
            )
            .padding(horizontal = WeiSomeSpacing.controlPadding, vertical = WeiSomeSpacing.stackSmall),
        decorationBox = { innerTextField ->
            Box {
                if (value.text.isEmpty()) {
                    WeiSomeText(
                        text = placeholder,
                        style = WeiSomeTypography.bodyMd,
                        color = WeiSomeColors.onSurfaceVariant.copy(alpha = 0.6f),
                    )
                }
                innerTextField()
            }
        },
    )
}
