import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Map(pieces: List<PieceState>, cols: Int) {
    LazyVerticalGrid(
        cells = GridCells.Fixed(cols),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(pieces.size) {
            Piece(pieces[it])
        }
    }
}

@Preview
@Composable
fun MapPreview() {
    val pieces = (0..24).map { PieceState.getRandom(0.2) }
    Map(pieces, 5)
}