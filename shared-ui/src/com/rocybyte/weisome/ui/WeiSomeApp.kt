package com.rocybyte.weisome.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.rocybyte.weisome.navigation.WechatArticleRoute
import com.rocybyte.weisome.page.article.WechatArticlePage
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

private val navigationStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(WechatArticleRoute::class, WechatArticleRoute.serializer())
        }
    }
}

/** Renders the root content of the WeiSome desktop application. */
@Composable
fun WeiSomeApp() {
    WeiSomeTheme {
        val backStack = rememberNavBackStack(navigationStateConfiguration, WechatArticleRoute)
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            NavDisplay(
                backStack = backStack,
                onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
                entryProvider = entryProvider {
                    entry<WechatArticleRoute> { WechatArticlePage() }
                },
            )
        }
    }
}
