package game.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import game.GameObjectState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameMap(objects: List<GameObjectState>, cols: Int) {
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
