import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

enum class ObjectState {
    EMPTY,
    BLOCKED,
    END,
    ACTUAL,
    NEXTSTEP;

    companion object {
        fun getRandom(blockedChance: Double): ObjectState {
            val rand = Random.Default.nextDouble()
            return if (rand <= blockedChance) BLOCKED else EMPTY
        }
    }
}

@Composable
fun GameObject(state: ObjectState, boxSize: Dp = 40.dp) {
    val color = when (state) {
        ObjectState.EMPTY -> Color.LightGray
        ObjectState.BLOCKED -> Color.Gray
        ObjectState.END -> Color.DarkGray
        ObjectState.ACTUAL -> Color.Red
        ObjectState.NEXTSTEP -> Color.Magenta
    }

    Box(
        modifier = Modifier
            .size(boxSize, boxSize)
            .shadow(20.dp)
            .background(color, RoundedCornerShape(5.dp))
    )
}

@Preview
@Composable
fun PiecePreview() {
    GameObject(ObjectState.EMPTY)
}