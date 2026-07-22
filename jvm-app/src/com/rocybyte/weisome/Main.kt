package com.rocybyte.weisome

import androidx.compose.ui.window.application
import com.rocybyte.weisome.di.platformDataModule
import com.rocybyte.weisome.di.uiModule
import com.rocybyte.weisome.settings.SavedWindowState
import com.rocybyte.weisome.settings.WindowStateStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

fun main() {
    startKoin { modules(uiModule, platformDataModule) }
    val windowStateStore = GlobalContext.get().get<WindowStateStore>()
    val initialWindowState = loadInitialWindowState(windowStateStore)

    application {
        WeiSomeWindow(initialWindowState, windowStateStore)
    }
}

private fun loadInitialWindowState(store: WindowStateStore): SavedWindowState? {
    val savedState = runBlocking(Dispatchers.IO) {
        try {
            store.load()
        } catch (error: Exception) {
            System.err.println("Unable to load window state: ${error.message}")
            null
        }
    } ?: return null
    val (workAreas, defaultWorkArea) = currentScreenWorkAreas()

    return savedState.clampToVisibleScreen(workAreas, defaultWorkArea)
}
