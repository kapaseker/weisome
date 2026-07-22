package com.rocybyte.weisome

import androidx.compose.ui.window.application
import com.rocybyte.weisome.di.platformDataModule
import com.rocybyte.weisome.di.uiModule
import org.koin.core.context.startKoin

/** Starts dependency injection and the Compose Desktop application. */
fun main() {
    startKoin { modules(uiModule, platformDataModule) }
    application {
        WeiSomeApplication()
    }
}
