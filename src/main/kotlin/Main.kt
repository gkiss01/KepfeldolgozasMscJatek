import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.delay

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun App() {
    val game = remember { Game(5, 5, 2000) }
    val density = LocalDensity.current

    LaunchedEffect(key1 = true) {
        game.startGame()
    }

    LaunchedEffect(key1 = game.elapsedTime) {
        delay(game.speed)
        game.elapsedTime += 1

        // Any update goes here
        game.updateGame()
    }

    MaterialTheme {
        Box(modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .pointerMoveFilter(onMove = {
                with(density) {
                    game.updatePointerLocation(DpOffset(it.x.toDp(), it.y.toDp()))
                }
                false
            })
            .onSizeChanged {
                with(density) {
                    game.width = it.width.toDp()
                    game.height = it.height.toDp()
                }
            })
        {
            GameMap(game.gameObjects, game.cols)
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
