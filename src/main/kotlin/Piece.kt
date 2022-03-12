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

enum class PieceState {
    FREE,
    BLOCKED,
    END;

    companion object {
        fun getRandom(): PieceState {
            return values()[Random.Default.nextInt(0, values().size)]
        }
    }
}

@Composable
fun Piece(state: PieceState, boxSize: Dp = 40.dp) {
    val color = when (state) {
        PieceState.FREE -> Color.LightGray
        PieceState.BLOCKED -> Color.Gray
        PieceState.END -> Color.DarkGray
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
    Piece(PieceState.FREE)
}