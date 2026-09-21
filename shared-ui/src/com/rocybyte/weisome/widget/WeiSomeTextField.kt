package com.rocybyte.weisome.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.rocybyte.weisome.ui.WeiSomeBorders
import com.rocybyte.weisome.ui.WeiSomeColors
import com.rocybyte.weisome.ui.WeiSomeShapes
import com.rocybyte.weisome.ui.WeiSomeTypography

/**
 * Text field per the DESIGN.md input spec: light gray resting surface that turns white
 * on focus with a 2px primary border.
 */
@Composable
internal fun WeiSomeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    minLines: Int = 1,
) {
    var focused by remember { mutableStateOf(false) }
    val shape = WeiSomeShapes.default

    Column(modifier = modifier) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .onFocusChanged { focused = it.isFocused }
                .then(
                    if (focused) {
                        Modifier.border(2.dp, WeiSomeColors.primary, shape)
                    } else {
                        Modifier.border(WeiSomeBorders.thin, WeiSomeColors.outlineVariant, shape)
                    },
                )
                .background(
                    if (focused) WeiSomeColors.surfaceContainerLowest else WeiSomeColors.surfaceContainerLow,
                    shape,
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            textStyle = WeiSomeTypography.bodyMd.copy(color = WeiSomeColors.onSurface),
            cursorBrush = SolidColor(WeiSomeColors.primary),
            minLines = minLines,
            decorationBox = { innerTextField ->
                Box(Modifier.fillMaxSize()) {
                    if (value.isEmpty() && placeholder != null) {
                        WeiSomeText(
                            text = placeholder,
                            style = WeiSomeTypography.bodyMd,
                            color = WeiSomeColors.onSurfaceVariant.copy(alpha = 0.6f),
                        )
                    }
                    Box(Modifier.fillMaxSize()) { innerTextField() }
                }
            },
        )
    }
}
