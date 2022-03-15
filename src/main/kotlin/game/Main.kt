package game

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
import initWindows
import kotlinx.coroutines.*
import nu.pattern.OpenCV
import org.opencv.core.Mat
import org.opencv.highgui.HighGui
import org.opencv.videoio.VideoCapture
import org.opencv.videoio.Videoio
import processImage
import java.awt.Toolkit
import kotlin.system.exitProcess

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun App(angle: Double) {
    val game = remember { Game(5, 5, difficulty = 0.10) }
    val density = LocalDensity.current

    LaunchedEffect(key1 = true) {
        game.startGame()
    }

    LaunchedEffect(key1 = game.elapsedTime) {
        delay(game.speed)
        if (game.isRunning()) game.elapsedTime += 1

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
            Column {
                GameMap(game.gameObjects, game.cols)
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (game.isRunning()) {
                        Text(
                            text = "Your score is ${game.elapsedTime}.",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        Text(
                            text = "Game Over!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Your best score is ${game.elapsedTime}.",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    OpenCV.loadLocally()

    val screenDimension = Toolkit.getDefaultToolkit().screenSize
    initWindows(screenDimension)

    var job: Job? = null
    val cap = VideoCapture(0, Videoio.CAP_DSHOW)
    val img = Mat()
    if (cap.isOpened) {
        var angle by mutableStateOf(0.0)

        job = GlobalScope.launch {
            while (true) {
                if (!cap.read(img)) continue
                angle = processImage(img, screenDimension)

                val keyPressed = HighGui.waitKey(16)
                if (keyPressed == 27) break
            }
        }

        application(
            exitProcessOnExit = false
        ) {
            Window(
                title = "Game (angle: ${String.format("%.2f", angle)}°)",
                onCloseRequest = ::exitApplication
            ) {
                App(angle)
            }
        }
    }

    job?.cancel()
    cap.release()
    HighGui.destroyAllWindows()

    exitProcess(0)
}
