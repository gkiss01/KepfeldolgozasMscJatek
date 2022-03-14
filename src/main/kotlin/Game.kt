import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class Game {
    private val gameObjectsInternal = mutableListOf<MutableList<PieceState>>()
    val gameObjects = mutableStateListOf<PieceState>()
    var gameState by mutableStateOf(GameState.RUNNING)

    private fun render() {
        gameObjects.swapList(gameObjectsInternal.flatten())
    }

    fun startGame() {
        gameState = GameState.RUNNING
        gameObjectsInternal.addAll(generateFirstMap(GAME_TABLE_ROWS, GAME_TABLE_COLS))
        render()
    }

    fun updateGame() {
        if (gameState == GameState.STOPPED) return

        // lépés megtétele az alsó sorban
        gameObjectsInternal[gameObjectsInternal.lastIndex] =
            makeMove(gameObjectsInternal[gameObjectsInternal.lastIndex], MoveDirection.Left)

        // az elem léptetése egy sorral feljebb
        val pos = getPos(gameObjectsInternal[gameObjectsInternal.lastIndex])
        // a felső sor ellenőrzése, hogy van-e ott fal
        if (gameObjectsInternal[gameObjectsInternal.lastIndex - 1][pos] != PieceState.EMPTY) {
            gameObjectsInternal[gameObjectsInternal.lastIndex - 1][pos] = PieceState.END
            endGame()
        } else
            gameObjectsInternal[gameObjectsInternal.lastIndex - 1][pos] = PieceState.ACTUAL

        // új sor beszúrása felülre
        gameObjectsInternal.add(0, generateRow(GAME_TABLE_COLS))

        // utolsó sor törlése
        gameObjectsInternal.removeLast()

        render()
    }

    fun endGame() {
        gameState = GameState.STOPPED
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
        fun canMove(row: List<PieceState>, direction: MoveDirection): Boolean {
            if (!row.contains(PieceState.ACTUAL)) throw GameError.ActualFieldNotFound
            val pos = row.indexOf(PieceState.ACTUAL)

            if (!row.indices.contains(pos + direction.step)) return false // throw GameError.NextPosOutOfBounds

            for (i in direction.toRange()) {
                if (row[pos + i] != PieceState.EMPTY) return false
            }

            return true
        }

        fun markMove(row: List<PieceState>, direction: MoveDirection): MutableList<PieceState> {
            if (!canMove(row, direction)) return row.toMutableList() // throw GameError.CannotMakeMove
            if (direction.step == 0) return row.toMutableList()
            val pos = row.indexOf(PieceState.ACTUAL)
            val rowCopy = row.toMutableList()
            rowCopy[pos + direction.step] = PieceState.NEXTSTEP
            return rowCopy
        }

        fun makeMove(row: List<PieceState>, direction: MoveDirection): MutableList<PieceState> {
            if (!canMove(row, direction)) return row.toMutableList() // throw GameError.CannotMakeMove
            val pos = row.indexOf(PieceState.ACTUAL)
            val rowCopy = row.toMutableList()
            rowCopy[pos] = PieceState.EMPTY
            rowCopy[pos + direction.step] = PieceState.ACTUAL
            return rowCopy
        }

        fun getPos(row: List<PieceState>): Int {
            if (!row.contains(PieceState.ACTUAL)) throw GameError.ActualFieldNotFound
            return row.indexOf(PieceState.ACTUAL)
        }

        private fun generateRow(cols: Int, probability: Double = 0.2): MutableList<PieceState> {
            return (0 until cols).map { PieceState.getRandom(probability) }.toMutableList()
        }

        private fun generateStartRow(cols: Int): MutableList<PieceState> {
            return generateRow(cols, 0.0).also {
                val centralIndex = it.size / 2
                it[centralIndex] = PieceState.ACTUAL
            }
        }

        fun generateFirstMap(rows: Int, cols: Int): MutableList<MutableList<PieceState>> {
            val pieces = mutableListOf<MutableList<PieceState>>()

            for (i in 1 until rows)
                pieces += generateRow(cols, 0.0)
            pieces += generateStartRow(cols)

            return pieces
        }
    }
}