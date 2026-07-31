package ygmd.kmpquiz.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ygmd.kmpquiz.di.initKoin
import ygmd.kmpquiz.presentation.App

fun main() {
    initKoin()
    application {
        Window(onCloseRequest = ::exitApplication, title = "KMPQuiz") {
            App()
        }
    }
}
