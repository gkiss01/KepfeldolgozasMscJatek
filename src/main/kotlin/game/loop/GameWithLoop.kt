package game.loop

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import game.Game
import game.swapList
import kotlinx.coroutines.*

class GameWithLoop(
    private val rows: Int,
    val cols: Int,
    private val iterationDelay: Long = 1000L,
    private val fillUpDelay: Long = (iterationDelay / 2.5).toLong(),
    private val difficulty: Double = 0.2
) {
    val objectsToRender = mutableStateListOf<ObjectStateWithLoop>()
    var state by mutableStateOf(GameState.STARTING)
        private set
    var score by mutableStateOf(0L)
        private set

    private var coroutineScope = CoroutineScope(Dispatchers.Main)

    private var timeMillis = 0L
    private var lastTimestamp = 0L
    private var lastIterationTimestamp = 0L

    private val objects = mutableListOf<MutableList<ObjectStateWithLoop>>()
    private var nextDirection: Game.MoveDirection = Game.MoveDirection.Stay

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
            objects.swapList(GameCore.generateFirstMap(rows, cols))

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
        // lépés megtétele az alsó sorban
//        objects[objects.lastIndex] =
//            GameCore.makeMove(objects[objects.lastIndex], nextDirection)
//        nextDirection = Game.MoveDirection.Stay

        // az elem léptetése egy sorral feljebb
        val pos = GameCore.getPos(objects[objects.lastIndex])
        if (objects[objects.lastIndex - 1][pos] !is ObjectStateWithLoop.Empty) { // a felső sor ellenőrzése, hogy van-e ott fal
            objects[objects.lastIndex - 1][pos] = ObjectStateWithLoop.End
            stop()
        } else {
            objects[objects.lastIndex - 1][pos] = ObjectStateWithLoop.Actual

            // töltés megjegyzése
            val nextPos = objects[objects.lastIndex].indexOfFirst { it is ObjectStateWithLoop.NextStep }
            if (nextPos != -1 && objects[objects.lastIndex - 1][nextPos] is ObjectStateWithLoop.Empty) {
                val nextStepObject = objects[objects.lastIndex].elementAt(nextPos) as ObjectStateWithLoop.NextStep
                objects[objects.lastIndex - 1][nextPos] = ObjectStateWithLoop.NextStep(nextStepObject.percentage)
            }
        }

        // új sor beszúrása felülre
        objects.add(0, GameCore.generateRow(cols, difficulty))

        // utolsó sor törlése
        objects.removeLast()
    }

    private fun updateGameObjects() {
        // irány lekérése a szögből
        val direction = Game.MoveDirection.fromAngle(angle)

        // szög elfelejtése => így ha nincs bemenet, megáll a lépkedés
        angle = Double.NaN

        // mezők frissítése => haladásjelző frissítése, átlépés
        objects[objects.lastIndex] =
            GameCore.markMove(objects[objects.lastIndex], direction, fillUpDelay)
    }

    enum class GameState {
        STARTING,
        RUNNING,
        STOPPED
    }

    fun updatePointerLocation(offset: DpOffset) {
        angle = (1 - (offset.x / width)) * 180.0
    }
}