import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.delay

const val GAME_TABLE_ROWS = 5
const val GAME_TABLE_COLS = 5

@Composable
@Preview
fun App() {
    val game = remember { Game() }
    var elapsedTime by remember { mutableStateOf(0) }

    LaunchedEffect(key1 = true) {
        game.startGame()
    }

    LaunchedEffect(key1 = elapsedTime) {
        delay(1000)
        elapsedTime += 1

        // Any update goes here
        game.updateGame()
    }

    MaterialTheme {
        Map(game.gameObjects)
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
