package game.loop

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import game.Game
import game.ObjectState
import game.swapList
import kotlinx.coroutines.*

class GameWithLoop(
    private val rows: Int,
    val cols: Int,
    private val iterationDelay: Long = 1000L,
    private val difficulty: Double = 0.2
) {
    val objectsToRender = mutableStateListOf<ObjectState>()
    var state by mutableStateOf(GameState.STARTING)
        private set
    var score by mutableStateOf(0L)
        private set

    private var coroutineScope = CoroutineScope(Dispatchers.Main)

    private var timeMillis = 0L
    private var lastTimestamp = 0L
    private var lastIterationTimestamp = 0L

    private val objects = mutableListOf<MutableList<ObjectState>>()
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
            objects.swapList(Game.generateFirstMap(rows, cols))

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
        objects[objects.lastIndex] =
            Game.makeMove(objects[objects.lastIndex], nextDirection)
        nextDirection = Game.MoveDirection.Stay

        // az elem léptetése egy sorral feljebb
        val pos = Game.getPos(objects[objects.lastIndex])
        if (objects[objects.lastIndex - 1][pos] != ObjectState.EMPTY) { // a felső sor ellenőrzése, hogy van-e ott fal
            objects[objects.lastIndex - 1][pos] = ObjectState.END
            stop()
        } else {
            objects[objects.lastIndex - 1][pos] = ObjectState.ACTUAL
        }

        // új sor beszúrása felülre
        objects.add(0, Game.generateRow(cols, difficulty))

        // utolsó sor törlése
        objects.removeLast()
    }

    private fun updateGameObjects() {
        // irány lekérése a szögből
        val direction = Game.MoveDirection.fromAngle(angle)

        // léphetünk-e a kijelölt irányban
        if (Game.canMove(objects[objects.lastIndex], direction))
            nextDirection = direction

        // következő mező kijelölése
        objects[objects.lastIndex] =
            Game.markMove(objects[objects.lastIndex], nextDirection)
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