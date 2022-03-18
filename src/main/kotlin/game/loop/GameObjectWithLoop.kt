package game.loop

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.random.Random

sealed class GameObjectState {
    object Empty : GameObjectState()
    object Blocked : GameObjectState()
    object End : GameObjectState()
    object Actual : GameObjectState()
    data class NextStep(val percentage: Double = 0.0) : GameObjectState()

    companion object {
        fun getRandom(blockedChance: Double): GameObjectState {
            val rand = Random.Default.nextDouble()
            return if (rand <= blockedChance) Blocked else Empty
        }
    }

    override fun toString(): String {
        return when (this) {
            Actual -> "Actual"
            Blocked -> "Blocked"
            Empty -> "Empty"
            End -> "End"
            is NextStep -> "NextStep($percentage)"
        }
    }
}

@Composable
fun GameObjectWithLoop(state: GameObjectState) {
    val color = when (state) {
        GameObjectState.Actual -> Color.DarkGray
        GameObjectState.Blocked -> Color.Gray
        GameObjectState.Empty -> Color.White
        GameObjectState.End -> Color.Red
        is GameObjectState.NextStep -> Color.White
    }

    Card(
        modifier = Modifier.size(40.dp),
        shape = RoundedCornerShape(5.dp),
        backgroundColor = color,
        elevation = 20.dp
    ) {
        if (state is GameObjectState.NextStep) {
            LinearProgressIndicator(
                progress = state.percentage.toFloat(),
                modifier = Modifier.fillMaxSize(),
                backgroundColor = Color.White,
                color = Color(0xFFFFC300)
            )
        }
    }
}

@Preview
@Composable
fun GameObjectWithLoopPreview() {
    GameObjectWithLoop(GameObjectState.NextStep(0.5))
}