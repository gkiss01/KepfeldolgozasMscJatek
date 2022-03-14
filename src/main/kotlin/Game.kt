import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

class Game(private val rows: Int, val cols: Int) {
    private val gameObjectsInternal = mutableListOf<MutableList<ObjectState>>()
    val gameObjects = mutableStateListOf<ObjectState>()

    private var gameState: GameState = GameState.RUNNING
    private var nextDirection: MoveDirection = MoveDirection.Stay

    var width by mutableStateOf(0.dp)
    var height by mutableStateOf(0.dp)

    private fun render() {
        gameObjects.swapList(gameObjectsInternal.flatten())
    }

    fun startGame() {
        gameState = GameState.RUNNING
        gameObjectsInternal.addAll(generateFirstMap(rows, cols))
        render()
    }

    fun updateGame() {
        if (gameState == GameState.STOPPED) return

        // lépés megtétele az alsó sorban
        gameObjectsInternal[gameObjectsInternal.lastIndex] =
            makeMove(gameObjectsInternal[gameObjectsInternal.lastIndex], nextDirection)

        // az elem léptetése egy sorral feljebb
        val pos = getPos(gameObjectsInternal[gameObjectsInternal.lastIndex])
        // a felső sor ellenőrzése, hogy van-e ott fal
        if (gameObjectsInternal[gameObjectsInternal.lastIndex - 1][pos] != ObjectState.EMPTY) {
            gameObjectsInternal[gameObjectsInternal.lastIndex - 1][pos] = ObjectState.END
            endGame()
        } else
            gameObjectsInternal[gameObjectsInternal.lastIndex - 1][pos] = ObjectState.ACTUAL

        // új sor beszúrása felülre
        gameObjectsInternal.add(0, generateRow(cols))

        // utolsó sor törlése
        gameObjectsInternal.removeLast()

        render()
    }

    fun endGame() {
        gameState = GameState.STOPPED
    }

    fun updatePointerLocation(offset: DpOffset) {
        if (gameState == GameState.STOPPED) return

        val angle = (1 - (offset.x / width)) * 180.0
        val direction = MoveDirection.fromAngle(angle)

        if (canMove(gameObjectsInternal[gameObjectsInternal.lastIndex], direction))
            nextDirection = direction

        gameObjectsInternal[gameObjectsInternal.lastIndex] =
            markMove(gameObjectsInternal[gameObjectsInternal.lastIndex], direction)

        render()
    }

    sealed class MoveDirection(val step: Int) {
        object Stay : MoveDirection(0)
        object Left : MoveDirection(-1)
        object LeftBig : MoveDirection(-2)
        object Right : MoveDirection(1)
        object RightBig : MoveDirection(2)

        fun toRange(): IntProgression {
            return when {
                step == 0 -> IntRange.EMPTY
                step > 0 -> 1..step
                else -> -1 downTo step
            }
        }

        companion object {
            fun fromAngle(angDeg: Double): MoveDirection {
                return when {
                    0.0 <= angDeg && angDeg < 30.0 -> RightBig
                    30.0 <= angDeg && angDeg < 70.0 -> Right
                    angDeg in 70.0..110.0 -> Stay
                    110.0 < angDeg && angDeg <= 150.0 -> Left
                    150.0 < angDeg && angDeg <= 180.0 -> LeftBig
                    else -> Stay
                }
            }
        }
    }

    sealed class GameError : Exception() {
        object ActualFieldNotFound : GameError()
        object ActualPosOutOfBounds : GameError()
        object NextPosOutOfBounds : GameError()
        object CannotMakeMove : GameError()
    }

    enum class GameState {
        STOPPED, RUNNING
    }

    companion object {
        fun canMove(row: List<ObjectState>, direction: MoveDirection): Boolean {
            if (!row.contains(ObjectState.ACTUAL)) throw GameError.ActualFieldNotFound
            val pos = row.indexOf(ObjectState.ACTUAL)

            if (!row.indices.contains(pos + direction.step)) return false // throw GameError.NextPosOutOfBounds

            for (i in direction.toRange()) {
                if (row[pos + i] != ObjectState.EMPTY &&
                    row[pos + i] != ObjectState.NEXTSTEP
                ) return false
            }

            return true
        }

        fun markMove(row: List<ObjectState>, direction: MoveDirection): MutableList<ObjectState> {
            if (!canMove(row, direction)) return row.toMutableList() // throw GameError.CannotMakeMove

            val pos = row.indexOf(ObjectState.ACTUAL)
            val rowCopy = row.toMutableList()
            rowCopy.forEachIndexed { index, state ->
                if (state == ObjectState.NEXTSTEP)
                    rowCopy[index] = ObjectState.EMPTY
            }

            if (direction.step == 0) return rowCopy
            rowCopy[pos + direction.step] = ObjectState.NEXTSTEP
            return rowCopy
        }

        fun makeMove(row: List<ObjectState>, direction: MoveDirection): MutableList<ObjectState> {
            if (!canMove(row, direction)) return row.toMutableList() // throw GameError.CannotMakeMove
            val pos = row.indexOf(ObjectState.ACTUAL)
            val rowCopy = row.toMutableList()
            rowCopy[pos] = ObjectState.EMPTY
            rowCopy[pos + direction.step] = ObjectState.ACTUAL
            return rowCopy
        }

        fun getPos(row: List<ObjectState>): Int {
            if (!row.contains(ObjectState.ACTUAL)) throw GameError.ActualFieldNotFound
            return row.indexOf(ObjectState.ACTUAL)
        }

        private fun generateRow(cols: Int, probability: Double = 0.2): MutableList<ObjectState> {
            return (0 until cols).map { ObjectState.getRandom(probability) }.toMutableList()
        }

        private fun generateStartRow(cols: Int): MutableList<ObjectState> {
            return generateRow(cols, 0.0).also {
                val centralIndex = it.size / 2
                it[centralIndex] = ObjectState.ACTUAL
            }
        }

        fun generateFirstMap(rows: Int, cols: Int): MutableList<MutableList<ObjectState>> {
            val objects = mutableListOf<MutableList<ObjectState>>()

            for (i in 1 until rows)
                objects += generateRow(cols, 0.0)
            objects += generateStartRow(cols)

            return objects
        }
    }
}