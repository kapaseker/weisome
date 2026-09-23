package com.rocybyte.weisome.page.article.widget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.rocybyte.weisome.article.CodeThemeId
import com.rocybyte.weisome.generated.resources.Res
import com.rocybyte.weisome.generated.resources.code_theme_atom_one
import com.rocybyte.weisome.generated.resources.code_theme_darcula
import com.rocybyte.weisome.generated.resources.code_theme_github_light
import com.rocybyte.weisome.generated.resources.code_theme_matrix
import com.rocybyte.weisome.generated.resources.code_theme_monokai
import com.rocybyte.weisome.generated.resources.code_theme_notepad
import com.rocybyte.weisome.generated.resources.code_theme_pastel
import com.rocybyte.weisome.generated.resources.code_theme_selector
import com.rocybyte.weisome.generated.resources.ic_code_pen
import com.rocybyte.weisome.ui.WeiSomeBorders
import com.rocybyte.weisome.ui.WeiSomeColors
import com.rocybyte.weisome.ui.WeiSomeShapes
import com.rocybyte.weisome.ui.WeiSomeSpacing
import com.rocybyte.weisome.ui.WeiSomeTypography
import com.rocybyte.weisome.widget.WeiSomeText
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** All selectable code themes in display order; GITHUB_LIGHT stays first as the default. */
private val CodeThemeEntries = listOf(
    CodeThemeId.GITHUB_LIGHT,
    CodeThemeId.DARCULA,
    CodeThemeId.MONOKAI,
    CodeThemeId.NOTEPAD,
    CodeThemeId.MATRIX,
    CodeThemeId.PASTEL,
    CodeThemeId.ATOM_ONE,
)

/** Returns the localized display name for a code theme id. */
@Composable
private fun CodeThemeId.displayName(): String = when (this) {
    CodeThemeId.GITHUB_LIGHT -> stringResource(Res.string.code_theme_github_light)
    CodeThemeId.DARCULA -> stringResource(Res.string.code_theme_darcula)
    CodeThemeId.MONOKAI -> stringResource(Res.string.code_theme_monokai)
    CodeThemeId.NOTEPAD -> stringResource(Res.string.code_theme_notepad)
    CodeThemeId.MATRIX -> stringResource(Res.string.code_theme_matrix)
    CodeThemeId.PASTEL -> stringResource(Res.string.code_theme_pastel)
    CodeThemeId.ATOM_ONE -> stringResource(Res.string.code_theme_atom_one)
}

/**
 * Toolbar control that switches the code-block highlight theme: a quiet selector labeled with
 * the active theme opens a single-choice popup list; the selected entry keeps a primary dot.
 */
@Composable
internal fun CodeThemeMenuButton(
    selectedTheme: CodeThemeId,
    onThemeSelected: (CodeThemeId) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier) {
        // 工具栏内控件不用按钮边框,靠 hover 浅底提示可点,箭头表达下拉语义。
        val interactionSource = remember { MutableInteractionSource() }
        val hovered by interactionSource.collectIsHoveredAsState()
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(WeiSomeShapes.default)
                .hoverable(interactionSource)
                .clickable(interactionSource = interactionSource, indication = null, onClick = { expanded = true })
                .background(if (hovered) WeiSomeColors.surfaceContainerLow else Color.Transparent)
                .padding(horizontal = WeiSomeSpacing.stackSm, vertical = WeiSomeSpacing.stackXs),
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_code_pen),
                contentDescription = stringResource(Res.string.code_theme_selector),
                modifier = Modifier.size(16.dp),
                colorFilter = ColorFilter.tint(WeiSomeColors.onSurfaceVariant),
            )
            Spacer(Modifier.width(WeiSomeSpacing.stackXs))
            WeiSomeText(
                text = selectedTheme.displayName(),
                style = WeiSomeTypography.labelSm,
                color = WeiSomeColors.onSurface,
            )
            Spacer(Modifier.width(WeiSomeSpacing.stackXs))
            ChevronDown()
        }
        if (expanded) {
            Popup(
                alignment = Alignment.TopEnd,
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = true),
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .shadow(8.dp, WeiSomeShapes.default, spotColor = WeiSomeColors.primary.copy(alpha = 0.08f))
                        .clip(WeiSomeShapes.default)
                        .background(WeiSomeColors.surfaceContainerLowest)
                        .border(WeiSomeBorders.thin, WeiSomeColors.outlineVariant, WeiSomeShapes.default)
                        .padding(vertical = WeiSomeSpacing.stackXs),
                ) {
                    CodeThemeEntries.forEach { theme ->
                        CodeThemeOption(
                            theme = theme,
                            selected = theme == selectedTheme,
                            onClick = {
                                expanded = false
                                onThemeSelected(theme)
                            },
                        )
                    }
                }
            }
        }
    }
}

/** Renders one theme entry with hover feedback and a primary selection dot. */
@Composable
private fun CodeThemeOption(
    theme: CodeThemeId,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val label = theme.displayName()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .heightIn(min = 40.dp)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .hoverable(interactionSource)
            .background(
                when {
                    hovered -> WeiSomeColors.surfaceContainerLow
                    else -> WeiSomeColors.surfaceContainerLowest
                },
            )
            .padding(horizontal = WeiSomeSpacing.stackSm),
    ) {
        WeiSomeText(
            text = label,
            style = WeiSomeTypography.labelSm,
            color = if (selected) WeiSomeColors.primary else WeiSomeColors.onSurface,
        )
        Spacer(Modifier.width(WeiSomeSpacing.stackSm))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(if (selected) 8.dp else 0.dp)
                    .clip(CircleShape)
                    .background(WeiSomeColors.primary),
            )
        }
    }
}

/** Renders a small downward chevron indicating the dropdown affordance. */
@Composable
private fun ChevronDown() {
    Canvas(Modifier.size(12.dp)) {
        val width = size.width
        val height = size.height
        drawPath(
            path = Path().apply {
                moveTo(width * 0.25f, height * 0.4f)
                lineTo(width * 0.5f, height * 0.65f)
                lineTo(width * 0.75f, height * 0.4f)
            },
            color = WeiSomeColors.onSurfaceVariant,
            style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}
