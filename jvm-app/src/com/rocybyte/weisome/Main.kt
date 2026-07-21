package com.rocybyte.weisome

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.rocybyte.weisome.ui.WeiSomeApp

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "WeiSome",
    ) {
        WeiSomeApp()
    }
}

