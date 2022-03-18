package game.loop

import game.Game

class GameCore {
    companion object {
        fun generateMap(rows: Int, cols: Int): MutableList<GameRowMutable> {
            val objects = mutableListOf<GameRowMutable>()

            for (i in 1 until rows)
                objects += generateRow(cols, 0.0)
            objects += generateStartRow(cols)

            return objects
        }

        fun generateRow(cols: Int, probability: Double = 0.2): GameRowMutable {
            return (0 until cols).map { ObjectStateWithLoop.getRandom(probability) }.toMutableList()
        }

        private fun generateStartRow(cols: Int): GameRowMutable {
            return generateRow(cols, 0.0).also {
                val centralIndex = it.size / 2
                it[centralIndex] = ObjectStateWithLoop.Actual
            }
        }
    }
}

typealias GameRow = List<ObjectStateWithLoop>
typealias GameRowMutable = MutableList<ObjectStateWithLoop>

fun GameRow.getActualIndex(): Int {
    return indexOfFirst { it is ObjectStateWithLoop.Actual }.also {
        if (it == -1) throw Game.GameError.ActualFieldNotFound
    }
}

fun GameRow.getHighlightIndex(): Int {
    return indexOfFirst { it is ObjectStateWithLoop.NextStep }
}

fun GameRow.getHighlight(): ObjectStateWithLoop.NextStep? {
    return find { it is ObjectStateWithLoop.NextStep } as? ObjectStateWithLoop.NextStep
}

fun GameRow.canMove(direction: Game.MoveDirection): Boolean {
    val pos = this.getActualIndex()
    if (!this.indices.contains(pos + direction.step)) return false // throw GameError.NextPosOutOfBounds

    for (i in direction.toRange()) {
        if (this[pos + i] !is ObjectStateWithLoop.Empty &&
            this[pos + i] !is ObjectStateWithLoop.NextStep
        ) return false
    }

    return true
}

fun GameRowMutable.markMove(
    direction: Game.MoveDirection,
    fillUpDelay: Long = 300L
) {
    val nextPos = this.getHighlightIndex()
    val nextStep = this.getHighlight()
    this.removeHighlights()

    if (!this.canMove(direction) || direction is Game.MoveDirection.Stay) return

    val actualPos = this.getActualIndex()
    if (nextPos != -1 && actualPos + direction.step == nextPos) {
        val fraction = 0.9 / (fillUpDelay / 16L)
        val newPercentage = nextStep!!.percentage + fraction
        if (newPercentage >= 1.0) {
            this[actualPos] = ObjectStateWithLoop.Empty
            this[nextPos] = ObjectStateWithLoop.Actual
        } else {
            this[nextPos] = ObjectStateWithLoop.NextStep(newPercentage)
        }
    } else {
        this[actualPos + direction.step] = ObjectStateWithLoop.NextStep(0.1)
    }
}

fun GameRowMutable.removeHighlights() {
    replaceAll {
        when (it) {
            is ObjectStateWithLoop.NextStep -> ObjectStateWithLoop.Empty
            else -> it
        }
    }
}
