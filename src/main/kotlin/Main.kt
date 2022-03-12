import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Button(onClick = {
            text = "Hello, Desktop!"
        }) {
            Text(text)
        }
    }
}

fun main() = application {
    var pieces = remember { mutableStateListOf<PieceState>() }
    pieces = (0..24).map { PieceState.getRandom() }.toMutableStateList()

    Window(onCloseRequest = ::exitApplication) {
        LaunchedEffect(key1 = true) {
            //pieces.shuffle()
        }
        Map(pieces)
    }

//    val pieces = remember { mutableStateOf<MutableList<PieceState>>(mutableListOf()) }
//
//    Window(onCloseRequest = ::exitApplication) {
//        LaunchedEffect(key1 = true) {
//
//            pieces.value = (0..24).map { PieceState.getRandom() }.toMutableList()
//            pieces.value.shuffle()
//        }
//        kotlin.collections.Map(pieces.value)
//    }
}
