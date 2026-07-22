package com.rocybyte.weisome.di

import com.rocybyte.weisome.repository.article.DesktopWechatArticleRepository
import com.rocybyte.weisome.repository.article.WechatArticleRepository
import org.koin.dsl.module

actual val platformDataModule = module {
    single<WechatArticleRepository> { DesktopWechatArticleRepository() }
}
