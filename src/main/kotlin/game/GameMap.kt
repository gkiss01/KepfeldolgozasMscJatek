package game

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
fun GameMap(objects: List<ObjectState>, cols: Int) {
    LazyVerticalGrid(
        cells = GridCells.Fixed(cols),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(objects.size) {
            GameObject(objects[it])
        }
    }
}

@Preview
@Composable
fun MapPreview() {
    val objects = (0..24).map { ObjectState.getRandom(0.2) }
    GameMap(objects, 5)
}