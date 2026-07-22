package com.rocybyte.weisome

import com.rocybyte.weisome.di.platformDataModule
import com.rocybyte.weisome.di.uiModule
import com.rocybyte.weisome.repository.article.WechatArticleRepository
import com.rocybyte.weisome.repository.window.WindowStateRepo
import com.rocybyte.weisome.storage.window.WindowStateStore
import com.rocybyte.weisome.window.biz.WindowStateViewModel
import org.koin.core.context.startKoin
import kotlin.test.Test
import kotlin.test.assertNotNull

class KoinGraphTest {
    @Test
    fun `production repository binding resolves from the application graph`() {
        val application = startKoin { modules(uiModule, platformDataModule) }

        try {
            assertNotNull(application.koin.get<WechatArticleRepository>())
            assertNotNull(application.koin.get<WindowStateStore>())
            assertNotNull(application.koin.get<WindowStateRepo>())
            assertNotNull(application.koin.get<WindowStateViewModel>())
        } finally {
            application.close()
        }
    }
}
