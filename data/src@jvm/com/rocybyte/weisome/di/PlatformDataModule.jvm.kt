package com.rocybyte.weisome.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.rocybyte.weisome.repository.article.DesktopWechatArticleRepository
import com.rocybyte.weisome.repository.article.WechatArticleRepository
import com.rocybyte.weisome.settings.WindowStateStorage
import com.rocybyte.weisome.settings.WindowStateStore
import com.rocybyte.weisome.settings.settingsDataStore
import org.koin.dsl.module

actual val platformDataModule = module {
    single<DataStore<Preferences>> { settingsDataStore }
    single<WindowStateStore> { WindowStateStorage(get()) }
    single<WechatArticleRepository> { DesktopWechatArticleRepository() }
}
