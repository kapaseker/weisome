package com.rocybyte.weisome.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.rocybyte.weisome.repository.article.DesktopWechatArticleRepository
import com.rocybyte.weisome.repository.article.WechatArticleRepository
import com.rocybyte.weisome.repository.code.CodeHighlightRepo
import com.rocybyte.weisome.repository.code.CodeHighlightRepository
import com.rocybyte.weisome.repository.window.WindowStateRepo
import com.rocybyte.weisome.repository.window.WindowStateRepository
import com.rocybyte.weisome.storage.window.WindowStateStorage
import com.rocybyte.weisome.storage.window.WindowStateStore
import com.rocybyte.weisome.storage.window.settingsDataStore
import org.koin.dsl.module

actual val platformDataModule = module {
    single<DataStore<Preferences>> { settingsDataStore }
    single<WindowStateStore> { WindowStateStorage(get()) }
    single<WindowStateRepo> { WindowStateRepository(get()) }
    single<CodeHighlightRepo> { CodeHighlightRepository() }
    single<WechatArticleRepository> { DesktopWechatArticleRepository(get()) }
}
