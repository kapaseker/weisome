package com.rocybyte.weisome.di

import com.rocybyte.weisome.page.article.biz.WechatArticleViewModel
import com.rocybyte.weisome.window.biz.WindowStateViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel { WindowStateViewModel(get()) }
    viewModel { parameters -> WechatArticleViewModel(get(), get(), parameters.get<String>()) }
}
