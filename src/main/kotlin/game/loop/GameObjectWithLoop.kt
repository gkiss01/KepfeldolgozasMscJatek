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

sealed class ObjectStateWithLoop {
    object Empty : ObjectStateWithLoop()
    object Blocked : ObjectStateWithLoop()
    object End : ObjectStateWithLoop()
    object Actual : ObjectStateWithLoop()
    data class NextStep(val percentage: Double = 0.0) : ObjectStateWithLoop()

    companion object {
        fun getRandom(blockedChance: Double): ObjectStateWithLoop {
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
fun GameObjectWithLoop(state: ObjectStateWithLoop) {
    val color = when (state) {
        ObjectStateWithLoop.Actual -> Color.DarkGray
        ObjectStateWithLoop.Blocked -> Color.Gray
        ObjectStateWithLoop.Empty -> Color.White
        ObjectStateWithLoop.End -> Color.Red
        is ObjectStateWithLoop.NextStep -> Color.White
    }

    Card(
        modifier = Modifier.size(40.dp),
        shape = RoundedCornerShape(5.dp),
        backgroundColor = color,
        elevation = 20.dp
    ) {
        if (state is ObjectStateWithLoop.NextStep) {
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
    GameObjectWithLoop(ObjectStateWithLoop.NextStep(0.5))
}