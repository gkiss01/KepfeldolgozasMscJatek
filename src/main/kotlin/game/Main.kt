package game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import game.composables.App
import initWindows
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import nu.pattern.OpenCV
import org.opencv.core.Mat
import org.opencv.highgui.HighGui
import org.opencv.videoio.VideoCapture
import org.opencv.videoio.Videoio
import processImage
import java.awt.Toolkit
import kotlin.system.exitProcess

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    OpenCV.loadLocally()

    val screenDimension = Toolkit.getDefaultToolkit().screenSize
    initWindows(screenDimension)

    var job: Job? = null
    val cap = VideoCapture(0, Videoio.CAP_DSHOW)
    if (cap.isOpened) {
        val img = Mat()
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
            val state = rememberWindowState(
                position = WindowPosition(Alignment.Center),
                width = (screenDimension.width / 2).dp,
                height = (screenDimension.height / 2).dp
            )

            Window(
                title = "Game (angle: ${String.format("%.2f", angle)}${if (angle.isNaN()) "" else "°"})",
                state = state,
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
