package game.loop

enum class GameState {
    STARTING,
    RUNNING,
    STOPPED
}

sealed class GameError : Exception() {
    object ActualFieldNotFound : GameError()
}

sealed class GameMove(val step: Int) {
    object Stay : GameMove(0)
    object Left : GameMove(-1)
    object LeftBig : GameMove(-2)
    object Right : GameMove(1)
    object RightBig : GameMove(2)

    fun toRange(): IntProgression {
        return when {
            step == 0 -> IntRange.EMPTY
            step > 0 -> 1..step
            else -> -1 downTo step
        }
    }

    companion object {
        fun fromAngle(angDeg: Double): GameMove {
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

class GameCore {
    companion object {
        fun generateMap(rows: Int, cols: Int): GameMapMutable {
            val objects = mutableListOf<GameRowMutable>()

            for (i in 1 until rows)
                objects += generateRow(cols, 0.0)
            objects += generateStartRow(cols)

            return objects
        }

        fun generateRow(cols: Int, probability: Double = 0.2): GameRowMutable {
            return (0 until cols).map { GameObjectState.getRandom(probability) }.toMutableList()
        }

        private fun generateStartRow(cols: Int): GameRowMutable {
            return generateRow(cols, 0.0).also {
                val centralIndex = it.size / 2
                it[centralIndex] = GameObjectState.Actual
            }
        }
    }
}

typealias GameRow = List<GameObjectState>
typealias GameRowMutable = MutableList<GameObjectState>
typealias GameMapMutable = MutableList<GameRowMutable>

fun GameRow.getActualIndex(): Int {
    return indexOfFirst { it is GameObjectState.Actual }.also {
        if (it == -1) throw GameError.ActualFieldNotFound
    }
}

fun GameRow.getHighlightIndex(): Int {
    return indexOfFirst { it is GameObjectState.NextStep }
}

fun GameRow.getHighlight(): GameObjectState.NextStep? {
    return find { it is GameObjectState.NextStep } as? GameObjectState.NextStep
}

fun GameRow.canMove(direction: GameMove): Boolean {
    val pos = this.getActualIndex()
    if (!this.indices.contains(pos + direction.step)) return false // throw GameError.NextPosOutOfBounds

    for (i in direction.toRange()) {
        if (this[pos + i] !is GameObjectState.Empty &&
            this[pos + i] !is GameObjectState.NextStep
        ) return false
    }

    return true
}

fun GameRowMutable.markMove(
    direction: GameMove,
    fillUpDelay: Long = 300L
) {
    val nextPos = this.getHighlightIndex()
    val nextStep = this.getHighlight()
    this.removeHighlights()

    if (!this.canMove(direction) || direction is GameMove.Stay) return

    val actualPos = this.getActualIndex()
    if (nextPos != -1 && actualPos + direction.step == nextPos) {
        val fraction = 0.9 / (fillUpDelay / 16L)
        val newPercentage = nextStep!!.percentage + fraction
        if (newPercentage >= 1.0) {
            this[actualPos] = GameObjectState.Empty
            this[nextPos] = GameObjectState.Actual
        } else {
            this[nextPos] = GameObjectState.NextStep(newPercentage)
        }
    } else {
        this[actualPos + direction.step] = GameObjectState.NextStep(0.1)
    }
}

fun GameRowMutable.removeHighlights() {
    replaceAll {
        when (it) {
            is GameObjectState.NextStep -> GameObjectState.Empty
            else -> it
        }
    }
}