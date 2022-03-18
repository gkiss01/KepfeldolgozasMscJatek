package game.composables

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import game.Game
import game.GameState

@Composable
fun GameMessages(game: Game) {
    when (game.state) {
        GameState.STARTING -> {
//            Text(
//                text = "Good luck!",
//                fontSize = 20.sp,
//                fontWeight = FontWeight.SemiBold
//            )
        }
        GameState.RUNNING -> {
            Text(
                text = "Your score is ${game.score}.",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        GameState.STOPPED -> {
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