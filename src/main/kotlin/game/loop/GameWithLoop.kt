package game.loop

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import game.swapList
import kotlinx.coroutines.*

class GameWithLoop(
    private val rows: Int,
    val cols: Int,
    private val iterationDelay: Long = 1000L,
    private val fillUpDelay: Long = (iterationDelay / 2.5).toLong(),
    private val difficulty: Double = 0.2
) {
    val objectsToRender = mutableStateListOf<GameObjectState>()
    var state by mutableStateOf(GameState.STARTING)
        private set
    var score by mutableStateOf(0L)
        private set

    private var coroutineScope = CoroutineScope(Dispatchers.Main)

    private var timeMillis = 0L
    private var lastTimestamp = 0L
    private var lastIterationTimestamp = 0L

    private val objects = mutableListOf<GameRowMutable>()

    private var angle: Double = Double.NaN
        set(value) {
            field = value.coerceIn(0.0, 180.0)
        }

    // később vedd ki
    var width by mutableStateOf(0.dp)
    var height by mutableStateOf(0.dp)

    fun start() {
        if (state == GameState.RUNNING) return

        if (state == GameState.STARTING)
            objects.swapList(GameCore.generateMap(rows, cols))

        coroutineScope.launch {
            lastTimestamp = System.currentTimeMillis()
            lastIterationTimestamp = lastTimestamp
            state = GameState.RUNNING

            while (state == GameState.RUNNING) {
                delay(16L)
                timeMillis += System.currentTimeMillis() - lastTimestamp
                lastTimestamp = System.currentTimeMillis()

                updateGameObjects()

                if (lastTimestamp - lastIterationTimestamp >= iterationDelay) {
                    lastIterationTimestamp = lastTimestamp

                    updateGameMap()
                    score++
                }

                // render
                objectsToRender.swapList(objects.flatten())
            }
        }
    }

    fun stop() {
        state = GameState.STOPPED
    }

    fun reset() {
        coroutineScope.cancel()
        coroutineScope = CoroutineScope(Dispatchers.Main)
        timeMillis = 0L
        lastTimestamp = 0L
        lastIterationTimestamp = 0L
        state = GameState.STARTING
        score = 0L
    }

    private fun updateGameMap() {
        // aktuális pozíció lekérése
        val pos = objects[objects.lastIndex].getActualIndex()

        // a játéktábla léptetése egy sorral lejjebb
        if (objects[objects.lastIndex - 1][pos] !is GameObjectState.Empty) { // ha falba ütközünk, a játéknak vége
            objects[objects.lastIndex - 1][pos] = GameObjectState.End
            stop()
        } else {
            // aktuális mező átléptetése
            objects[objects.lastIndex - 1][pos] = GameObjectState.Actual

            // következő mező irányának megjegyzése és betöltése az új sorba
            val nextPos = objects[objects.lastIndex].getHighlightIndex()
            if (nextPos != -1 && objects[objects.lastIndex - 1][nextPos] is GameObjectState.Empty) { // csak ha ott üres mező áll
                val nextStep = objects[objects.lastIndex].getHighlight()
                objects[objects.lastIndex - 1][nextPos] = GameObjectState.NextStep(nextStep!!.percentage)
            }
        }

        // új sor beszúrása a játéktábla tetejére
        objects.add(0, GameCore.generateRow(cols, difficulty))

        // utolsó sor törlése a játéktábla aljáról
        objects.removeLast()
    }

    private fun updateGameObjects() {
        // irány lekérése a szögből
        val direction = GameMove.fromAngle(angle)

        // szög elfelejtése => így ha nincs bemenet, megáll a lépkedés
        angle = Double.NaN

        // mezők frissítése => haladásjelző frissítése, átlépés
        objects[objects.lastIndex].markMove(direction, fillUpDelay)
    }

    fun updatePointerLocation(offset: DpOffset) {
        angle = (1 - (offset.x / width)) * 180.0
    }
}