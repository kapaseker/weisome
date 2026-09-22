package com.rocybyte.weisome.page.article.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.rocybyte.weisome.article.Article
import com.rocybyte.weisome.generated.resources.Res
import com.rocybyte.weisome.generated.resources.app_name
import com.rocybyte.weisome.generated.resources.article_create
import com.rocybyte.weisome.generated.resources.article_created_at
import com.rocybyte.weisome.generated.resources.article_delete
import com.rocybyte.weisome.generated.resources.article_empty_hint
import com.rocybyte.weisome.generated.resources.article_title_label
import com.rocybyte.weisome.generated.resources.article_title_placeholder
import com.rocybyte.weisome.generated.resources.article_updated_at
import com.rocybyte.weisome.generated.resources.ic_add
import com.rocybyte.weisome.generated.resources.ic_delete
import com.rocybyte.weisome.generated.resources.ic_settings
import com.rocybyte.weisome.generated.resources.settings
import com.rocybyte.weisome.page.article.biz.ArticleHomeUiState
import com.rocybyte.weisome.page.article.widget.ArticleDeleteDialog
import com.rocybyte.weisome.page.article.widget.ArticleTitleDialog
import com.rocybyte.weisome.ui.WeiSomeBorders
import com.rocybyte.weisome.ui.WeiSomeColors
import com.rocybyte.weisome.ui.WeiSomeShapes
import com.rocybyte.weisome.ui.WeiSomeSpacing
import com.rocybyte.weisome.ui.WeiSomeTypography
import com.rocybyte.weisome.ui.weiSomeRipple
import com.rocybyte.weisome.widget.MediumIconButton
import com.rocybyte.weisome.widget.WeiSomeCircularProgressIndicator
import com.rocybyte.weisome.widget.WeiSomeText
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** Renders the article home screen: loading, empty, and list states with create/delete dialogs. */
@Composable
internal fun ArticleHomeScreen(
    state: ArticleHomeUiState,
    onCreateConfirm: (String) -> Unit,
    onDeleteArticle: (String) -> Unit,
    onOpenArticle: (String) -> Unit,
    onOpenSettings: () -> Unit,
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var pendingDelete by remember { mutableStateOf<Article?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(WeiSomeSpacing.margin),
            verticalArrangement = Arrangement.spacedBy(WeiSomeSpacing.stackSm),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                WeiSomeText(
                    text = stringResource(Res.string.app_name),
                    style = WeiSomeTypography.h2,
                    modifier = Modifier.weight(1f),
                )
                MediumIconButton(
                    onClick = onOpenSettings,
                    painter = painterResource(Res.drawable.ic_settings),
                    contentDescription = stringResource(Res.string.settings),
                )
            }
            when {
                !state.isLoaded -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    WeiSomeCircularProgressIndicator()
                }

                state.articles.isEmpty() -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    WeiSomeText(
                        text = stringResource(Res.string.article_empty_hint),
                        style = WeiSomeTypography.bodyLg,
                        color = WeiSomeColors.onSurfaceVariant,
                    )
                }

                else -> LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(WeiSomeSpacing.stackSm),
                ) {
                    items(state.articles, key = Article::id) { article ->
                        ArticleCard(
                            article = article,
                            onOpenArticle = onOpenArticle,
                            onDeleteRequest = { pendingDelete = article },
                        )
                    }
                }
            }
        }
        if (state.isLoaded) {
            ArticleCreateFab(
                onClick = { showCreateDialog = true },
                modifier = Modifier.align(Alignment.BottomEnd).padding(WeiSomeSpacing.margin),
            )
        }
    }

    if (showCreateDialog) {
        ArticleTitleDialog(
            title = stringResource(Res.string.article_title_label),
            initialValue = "",
            placeholder = stringResource(Res.string.article_title_placeholder),
            onConfirm = { title ->
                showCreateDialog = false
                onCreateConfirm(title)
            },
            onDismiss = { showCreateDialog = false },
        )
    }
    pendingDelete?.let { article ->
        ArticleDeleteDialog(
            title = article.title,
            onConfirm = {
                pendingDelete = null
                onDeleteArticle(article.id)
            },
            onDismiss = { pendingDelete = null },
        )
    }
}

/**
 * Floating action button for creating an article: Electric gradient fill with a white
 * icon per DESIGN.md, and a faint tint-matched glow while hovered.
 */
@Composable
private fun ArticleCreateFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val shape = WeiSomeShapes.full

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(52.dp)
            .then(
                if (hovered) {
                    Modifier.shadow(8.dp, shape, spotColor = WeiSomeColors.primary.copy(alpha = 0.2f))
                } else {
                    Modifier
                },
            )
            .clip(shape)
            .background(Brush.linearGradient(listOf(WeiSomeColors.primary, WeiSomeColors.primaryContainer)))
            .hoverable(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                // 深色渐变底：ripple 用内容色（白）而非全局 onSurface。
                indication = weiSomeRipple(WeiSomeColors.onPrimary),
                onClick = onClick,
            ),
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_add),
            contentDescription = stringResource(Res.string.article_create),
            modifier = Modifier.size(20.dp),
            colorFilter = ColorFilter.tint(Color.White),
        )
    }
}

/**
 * Article card per DESIGN.md: white surface, 1px border, airy hover shadow, md radius.
 * The whole card opens the article; a delete button appears on hover.
 */
@Composable
private fun ArticleCard(
    article: Article,
    onOpenArticle: (String) -> Unit,
    onDeleteRequest: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val shape = WeiSomeShapes.md

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (hovered) {
                    Modifier.shadow(
                        24.dp,
                        shape,
                        spotColor = WeiSomeColors.primary.copy(alpha = 0.05f),
                    )
                } else {
                    Modifier
                },
            )
            .clip(shape)
            .background(WeiSomeColors.surfaceContainerLowest, shape)
            .border(WeiSomeBorders.thin, WeiSomeColors.outlineVariant, shape)
            .hoverable(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onOpenArticle(article.id) },
            )
            .padding(horizontal = WeiSomeSpacing.stackMd, vertical = WeiSomeSpacing.stackSm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(WeiSomeSpacing.stackXs),
        ) {
            WeiSomeText(text = article.title, style = WeiSomeTypography.h3)
            Row(horizontalArrangement = Arrangement.spacedBy(WeiSomeSpacing.stackMd)) {
                WeiSomeText(
                    text = stringResource(Res.string.article_created_at, formatTimestamp(article.createdAt)),
                    style = WeiSomeTypography.labelSm,
                    color = WeiSomeColors.onSurfaceVariant,
                )
                WeiSomeText(
                    text = stringResource(Res.string.article_updated_at, formatTimestamp(article.updatedAt)),
                    style = WeiSomeTypography.labelSm,
                    color = WeiSomeColors.onSurfaceVariant,
                )
            }
        }
        if (hovered) {
            MediumIconButton(
                onClick = onDeleteRequest,
                painter = painterResource(Res.drawable.ic_delete),
                contentDescription = stringResource(Res.string.article_delete),
            )
        } else {
            // 占位保持标题与删除按钮区域等宽,避免 hover 时布局跳动。
            Spacer(Modifier.size(52.dp))
        }
    }
}

/** Formats epoch millis in the system zone as `yyyy-MM-dd HH:mm`. */
private fun formatTimestamp(epochMillis: Long): String =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        .withZone(ZoneId.systemDefault())
        .format(Instant.ofEpochMilli(epochMillis))
