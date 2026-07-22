package com.rocybyte.weisome

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.rocybyte.weisome.di.platformDataModule
import com.rocybyte.weisome.di.uiModule
import com.rocybyte.weisome.ui.WeiSomeApp
import org.koin.core.context.startKoin

fun main() {
    startKoin { modules(uiModule, platformDataModule) }
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "WeiSome",
        ) {
            WeiSomeApp()
        }
    }
}
