package game

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import game.composables.App
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlin.system.exitProcess

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    application(
        exitProcessOnExit = false
    ) {
        Window(
            title = "Game",
            onCloseRequest = ::exitApplication
        ) {
            App()
        }
    }

    exitProcess(0)
}
