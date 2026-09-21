package com.rocybyte.weisome.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.rocybyte.weisome.navigation.SettingsRoute
import com.rocybyte.weisome.navigation.WechatArticleRoute
import com.rocybyte.weisome.page.article.WechatArticlePage
import com.rocybyte.weisome.page.settings.SettingsPage
import com.rocybyte.weisome.page.settings.biz.SettingsViewModel
import com.rocybyte.weisome.page.settings.biz.selectedTextScale
import com.rocybyte.weisome.page.settings.biz.selectedUiScale
import com.rocybyte.weisome.widget.WeiSomeCircularProgressIndicator
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.koin.compose.viewmodel.koinViewModel

private val navigationStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(WechatArticleRoute::class, WechatArticleRoute.serializer())
            subclass(SettingsRoute::class, SettingsRoute.serializer())
        }
    }
}

/** Renders the root content of the WeiSome desktop application. */
@Composable
fun WeiSomeApp() {
    val settingsViewModel = koinViewModel<SettingsViewModel>()
    val textScaleState by settingsViewModel.textScaleState.collectAsState()
    val uiScaleState by settingsViewModel.uiScaleState.collectAsState()
    val systemDensity = LocalDensity.current
    val backStack = rememberNavBackStack(navigationStateConfiguration, WechatArticleRoute)

    if (!textScaleState.isLoaded || !uiScaleState.isLoaded) {
        Box(
            modifier = Modifier.fillMaxSize().background(WeiSomeColors.background),
            contentAlignment = Alignment.Center,
        ) {
            WeiSomeCircularProgressIndicator()
        }
        return
    }

    val textScale = selectedTextScale(systemDensity.density, textScaleState.userScale)
    val uiScale = selectedUiScale(uiScaleState.userScale)

    WeiSomeTheme(textScale = textScale, uiScale = uiScale) {
        Box(modifier = Modifier.fillMaxSize().background(WeiSomeColors.background)) {
            NavDisplay(
                backStack = backStack,
                onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
                entryProvider = entryProvider {
                    entry<WechatArticleRoute> {
                        WechatArticlePage(
                            onOpenSettings = {
                                if (backStack.lastOrNull() != SettingsRoute) {
                                    backStack.add(SettingsRoute)
                                }
                            },
                        )
                    }
                    entry<SettingsRoute> {
                        SettingsPage(
                            textState = textScaleState,
                            uiState = uiScaleState,
                            selectedTextScale = textScale,
                            systemDensity = systemDensity,
                            onTextScaleChanged = settingsViewModel::previewTextScale,
                            onTextScaleChangeFinished = settingsViewModel::savePreviewedTextScale,
                            onResetTextScale = settingsViewModel::resetTextScale,
                            onUiScaleChanged = settingsViewModel::previewUiScale,
                            onUiScaleChangeFinished = settingsViewModel::applyPreviewedUiScale,
                            onResetUiScale = settingsViewModel::resetUiScale,
                            onBack = { backStack.removeLastOrNull() },
                        )
                    }
                },
            )
        }
    }
}
