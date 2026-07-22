package com.rocybyte.weisome

import com.rocybyte.weisome.di.platformDataModule
import com.rocybyte.weisome.di.uiModule
import com.rocybyte.weisome.repository.article.WechatArticleRepository
import org.koin.core.context.startKoin
import kotlin.test.Test
import kotlin.test.assertNotNull

class KoinGraphTest {
    @Test
    fun `production repository binding resolves from the application graph`() {
        val application = startKoin { modules(uiModule, platformDataModule) }

        try {
            assertNotNull(application.koin.get<WechatArticleRepository>())
        } finally {
            application.close()
        }
    }
}
