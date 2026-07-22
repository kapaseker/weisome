package com.rocybyte.weisome.di

import com.rocybyte.weisome.page.article.biz.WechatArticleViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel { parameters -> WechatArticleViewModel(get(), parameters.get<String>()) }
}
