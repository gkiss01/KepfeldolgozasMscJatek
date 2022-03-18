package game.loop

import game.Game

class GameCore {
    companion object {
        private fun canMove(row: List<ObjectStateWithLoop>, direction: Game.MoveDirection): Boolean {
            if (!row.contains(ObjectStateWithLoop.Actual)) throw Game.GameError.ActualFieldNotFound
            val pos = row.indexOf(ObjectStateWithLoop.Actual)

            if (!row.indices.contains(pos + direction.step)) return false // throw GameError.NextPosOutOfBounds

            for (i in direction.toRange()) {
                if (row[pos + i] !is ObjectStateWithLoop.Empty &&
                    row[pos + i] !is ObjectStateWithLoop.NextStep
                ) return false
            }

            return true
        }

        fun markMove(
            row: List<ObjectStateWithLoop>,
            direction: Game.MoveDirection,
            fillUpDelay: Long = 300L
        ): MutableList<ObjectStateWithLoop> {
            val rowCopy = row.toMutableList()
            val nextPos = rowCopy.getHighlightIndex()
            val nextStep = rowCopy.getHighlight()
            rowCopy.removeHighlights()

            if (!canMove(row, direction) || direction is Game.MoveDirection.Stay) return rowCopy

            val actualPos = rowCopy.getActualIndex()
            if (nextPos != -1 && actualPos + direction.step == nextPos) {
                val fraction = 0.9 / (fillUpDelay / 16L)
                val newPercentage = nextStep!!.percentage + fraction
                if (newPercentage >= 1.0) {
                    rowCopy[actualPos] = ObjectStateWithLoop.Empty
                    rowCopy[nextPos] = ObjectStateWithLoop.Actual
                } else {
                    rowCopy[nextPos] = ObjectStateWithLoop.NextStep(newPercentage)
                }
            } else {
                rowCopy[actualPos + direction.step] = ObjectStateWithLoop.NextStep(0.1)
            }
            return rowCopy
        }

        fun generateFirstMap(rows: Int, cols: Int): MutableList<MutableList<ObjectStateWithLoop>> {
            val objects = mutableListOf<MutableList<ObjectStateWithLoop>>()

            for (i in 1 until rows)
                objects += generateRow(cols, 0.0)
            objects += generateStartRow(cols)

            return objects
        }

        fun generateRow(cols: Int, probability: Double = 0.2): MutableList<ObjectStateWithLoop> {
            return (0 until cols).map { ObjectStateWithLoop.getRandom(probability) }.toMutableList()
        }

        private fun generateStartRow(cols: Int): MutableList<ObjectStateWithLoop> {
            return generateRow(cols, 0.0).also {
                val centralIndex = it.size / 2
                it[centralIndex] = ObjectStateWithLoop.Actual
            }
        }
    }
}

typealias GameRow = MutableList<ObjectStateWithLoop>

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

fun GameRow.removeHighlights() {
    replaceAll {
        when (it) {
            is ObjectStateWithLoop.NextStep -> ObjectStateWithLoop.Empty
            else -> it
        }
    }
}