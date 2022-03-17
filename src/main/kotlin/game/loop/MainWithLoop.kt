package game.loop

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlin.system.exitProcess

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun AppLoop() {
    val game = remember { GameWithLoop(5, 5) }
    val density = LocalDensity.current

    LaunchedEffect(key1 = true) {
        game.start()
    }

//    LaunchedEffect(key1 = angle) {
//        game.angle = angle
//    }

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
            }
        ) {
            Column {
                GameMapWithLoop(game.objectsToRender, game.cols)
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GameMessages(game)
                }
            }
        }
    }
}

@Composable
private fun GameMessages(game: GameWithLoop) {
    when (game.state) {
        GameWithLoop.GameState.STARTING -> {
//            Text(
//                text = "Good luck!",
//                fontSize = 20.sp,
//                fontWeight = FontWeight.SemiBold
//            )
        }
        GameWithLoop.GameState.RUNNING -> {
            Text(
                text = "Your score is ${game.score}.",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        GameWithLoop.GameState.STOPPED -> {
            Text(
                text = "Game Over!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Your best score is ${game.score}.",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    application(
        exitProcessOnExit = false
    ) {
        Window(
            onCloseRequest = ::exitApplication
        ) {
            AppLoop()
        }
    }

    exitProcess(0)
}
