package game.loop

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
}


@Composable
fun GameObjectWithLoop(state: ObjectStateWithLoop) {
    val color = when (state) {
        ObjectStateWithLoop.Actual -> Color.DarkGray
        ObjectStateWithLoop.Blocked -> Color.Gray
        ObjectStateWithLoop.Empty -> Color.White
        ObjectStateWithLoop.End -> Color.Red
        is ObjectStateWithLoop.NextStep -> Color(0xFFFFC300).copy(alpha = state.percentage.toFloat())
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .shadow(20.dp)
            .background(color, RoundedCornerShape(5.dp))
    )
}

@Preview
@Composable
fun GameObjectWithLoopPreview() {
    GameObjectWithLoop(ObjectStateWithLoop.NextStep(0.5))
}