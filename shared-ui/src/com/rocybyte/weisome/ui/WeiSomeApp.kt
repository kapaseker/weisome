package com.rocybyte.weisome.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.rocybyte.weisome.navigation.WechatArticleRoute
import com.rocybyte.weisome.navigation.SettingsRoute
import com.rocybyte.weisome.page.article.WechatArticlePage
import com.rocybyte.weisome.page.settings.SettingsPage
import com.rocybyte.weisome.page.settings.biz.SettingsViewModel
import com.rocybyte.weisome.page.settings.biz.selectedTextScale
import com.rocybyte.weisome.page.settings.biz.selectedUiScale
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
        MaterialTheme { LoadingScreen() }
        return
    }

    val textScale = selectedTextScale(systemDensity.density, textScaleState.userScale)
    val uiScale = selectedUiScale(uiScaleState.userScale)

    WeiSomeTheme(textScale = textScale, uiScale = uiScale) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
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

/** Renders the initial display-settings loading state. */
@Composable
private fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
