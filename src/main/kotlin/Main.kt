import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.delay

@Composable
@Preview
fun App() {
    val pieces = remember { mutableStateListOf<PieceState>() }
    var elapsedTime by remember { mutableStateOf(0) }

    LaunchedEffect(key1 = true) {
        pieces.swapList((0..24).map { PieceState.getRandom() }.toMutableStateList())
    }

    LaunchedEffect(key1 = elapsedTime) {
        delay(1000)
        elapsedTime += 1

        // Any update goes here
        pieces.swapList((0..24).map { PieceState.getRandom() }.toMutableStateList())
    }

    MaterialTheme {
        Map(pieces)
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
