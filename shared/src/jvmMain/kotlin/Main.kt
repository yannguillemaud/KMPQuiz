import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ygmd.kmpquiz.di.initKoin
import ygmd.kmpquiz.ui.App

fun main() = application {
    initKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "KMPQuiz",
        alwaysOnTop = true
    ) {
        App()
    }
}
