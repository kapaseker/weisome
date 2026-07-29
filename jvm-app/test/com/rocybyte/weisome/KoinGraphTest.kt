package com.rocybyte.weisome

import com.rocybyte.weisome.di.platformDataModule
import com.rocybyte.weisome.di.uiModule
import com.rocybyte.weisome.repository.article.ArticleLayoutRepo
import com.rocybyte.weisome.repository.article.WechatArticleRepository
import com.rocybyte.weisome.repository.code.CodeHighlightRepo
import com.rocybyte.weisome.repository.settings.DisplaySettingsRepo
import com.rocybyte.weisome.repository.window.WindowStateRepo
import com.rocybyte.weisome.storage.article.ArticleLayoutStore
import com.rocybyte.weisome.storage.settings.DisplaySettingsStore
import com.rocybyte.weisome.storage.window.WindowStateStore
import com.rocybyte.weisome.window.biz.WindowStateViewModel
import com.rocybyte.weisome.page.settings.biz.SettingsViewModel
import org.koin.core.context.startKoin
import kotlin.test.Test
import kotlin.test.assertNotNull

class KoinGraphTest {
    @Test
    /** Verifies production repository, store, and ViewModel bindings resolve together. */
    fun `production repository binding resolves from the application graph`() {
        val application = startKoin { modules(uiModule, platformDataModule) }

        try {
            assertNotNull(application.koin.get<WechatArticleRepository>())
            assertNotNull(application.koin.get<ArticleLayoutStore>())
            assertNotNull(application.koin.get<ArticleLayoutRepo>())
            assertNotNull(application.koin.get<DisplaySettingsStore>())
            assertNotNull(application.koin.get<DisplaySettingsRepo>())
            assertNotNull(application.koin.get<CodeHighlightRepo>())
            assertNotNull(application.koin.get<WindowStateStore>())
            assertNotNull(application.koin.get<WindowStateRepo>())
            assertNotNull(application.koin.get<WindowStateViewModel>())
            assertNotNull(application.koin.get<SettingsViewModel>())
        } finally {
            application.close()
        }
    }
}
