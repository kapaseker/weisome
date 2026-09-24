package com.rocybyte.weisome.page.article.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.rocybyte.weisome.article.MarkdownThemeId
import com.rocybyte.weisome.generated.resources.Res
import com.rocybyte.weisome.generated.resources.ic_palette
import com.rocybyte.weisome.generated.resources.markdown_theme_github
import com.rocybyte.weisome.generated.resources.markdown_theme_hydrogen
import com.rocybyte.weisome.generated.resources.markdown_theme_selector
import com.rocybyte.weisome.generated.resources.markdown_theme_smart_blue
import com.rocybyte.weisome.generated.resources.markdown_theme_typora_paper
import com.rocybyte.weisome.ui.WeiSomeBorders
import com.rocybyte.weisome.ui.WeiSomeColors
import com.rocybyte.weisome.ui.WeiSomeShapes
import com.rocybyte.weisome.ui.WeiSomeSpacing
import com.rocybyte.weisome.ui.WeiSomeTypography
import com.rocybyte.weisome.widget.WeiSomeText
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** All selectable Markdown themes in display order; GITHUB stays first as the default. */
private val MarkdownThemeEntries = listOf(
    MarkdownThemeId.GITHUB,
    MarkdownThemeId.HYDROGEN,
    MarkdownThemeId.SMART_BLUE,
    MarkdownThemeId.TYPORA_PAPER,
)

/** Returns the localized display name for a Markdown theme id. */
@Composable
private fun MarkdownThemeId.displayName(): String = when (this) {
    MarkdownThemeId.GITHUB -> stringResource(Res.string.markdown_theme_github)
    MarkdownThemeId.HYDROGEN -> stringResource(Res.string.markdown_theme_hydrogen)
    MarkdownThemeId.SMART_BLUE -> stringResource(Res.string.markdown_theme_smart_blue)
    MarkdownThemeId.TYPORA_PAPER -> stringResource(Res.string.markdown_theme_typora_paper)
}

/**
 * Toolbar control that switches the Markdown document theme: a quiet palette-labeled selector
 * opens a single-choice popup list; the selected entry keeps a primary dot.
 */
@Composable
internal fun MarkdownThemeMenuButton(
    selectedTheme: MarkdownThemeId,
    onThemeSelected: (MarkdownThemeId) -> Unit,
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
                painter = painterResource(Res.drawable.ic_palette),
                contentDescription = stringResource(Res.string.markdown_theme_selector),
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
            ThemeChevronDown()
        }
        if (expanded) {
            Popup(
                alignment = Alignment.TopEnd,
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = true),
            ) {
                Column(
                    // IntrinsicSize.Max 让菜单宽度由最宽的菜单项决定,item 再 fillMaxWidth 铺满菜单,
                    // 避免 Popup 无界约束下 fillMaxWidth 撑到屏幕宽。
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .shadow(8.dp, WeiSomeShapes.default, spotColor = WeiSomeColors.primary.copy(alpha = 0.08f))
                        .clip(WeiSomeShapes.default)
                        .background(WeiSomeColors.surfaceContainerLowest)
                        .border(WeiSomeBorders.thin, WeiSomeColors.outlineVariant, WeiSomeShapes.default)
                        .padding(vertical = WeiSomeSpacing.stackXs),
                ) {
                    MarkdownThemeEntries.forEach { theme ->
                        MarkdownThemeOption(
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
private fun MarkdownThemeOption(
    theme: MarkdownThemeId,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val label = theme.displayName()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
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
        Spacer(Modifier.weight(1f))
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
private fun ThemeChevronDown() {
    ChevronDown()
}
