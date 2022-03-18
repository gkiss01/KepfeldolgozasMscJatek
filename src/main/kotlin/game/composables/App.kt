package game.composables

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalDensity
import game.Game

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun App(angle: Double) {
    val game = remember { Game(5, 5) }
    val density = LocalDensity.current

    LaunchedEffect(key1 = true) {
        game.start()
    }

    LaunchedEffect(key1 = angle) {
        game.angle = angle
    }

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
        ) {
            Column {
                GameMap(game.objectsToRender, game.cols)
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