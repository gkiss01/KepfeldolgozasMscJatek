package game.loop

import game.Game

class GameCore {
    companion object {
        fun canMove(row: List<ObjectStateWithLoop>, direction: Game.MoveDirection): Boolean {
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

        fun markMove(row: List<ObjectStateWithLoop>, direction: Game.MoveDirection): MutableList<ObjectStateWithLoop> {
            if (!canMove(row, direction)) return row.toMutableList() // throw GameError.CannotMakeMove

            val pos = row.indexOf(ObjectStateWithLoop.Actual)
            val rowCopy = row.toMutableList()
            rowCopy.forEachIndexed { index, state ->
                if (state is ObjectStateWithLoop.NextStep)
                    rowCopy[index] = ObjectStateWithLoop.Empty
            }

            if (direction.step == 0) return rowCopy
            rowCopy[pos + direction.step] = ObjectStateWithLoop.NextStep(0.5)
            return rowCopy
        }

        fun makeMove(row: List<ObjectStateWithLoop>, direction: Game.MoveDirection): MutableList<ObjectStateWithLoop> {
            if (!canMove(row, direction)) return row.toMutableList() // throw GameError.CannotMakeMove
            val pos = row.indexOf(ObjectStateWithLoop.Actual)
            val rowCopy = row.toMutableList()
            rowCopy[pos] = ObjectStateWithLoop.Empty
            rowCopy[pos + direction.step] = ObjectStateWithLoop.Actual
            return rowCopy
        }

        fun getPos(row: List<ObjectStateWithLoop>): Int {
            if (!row.contains(ObjectStateWithLoop.Actual)) throw Game.GameError.ActualFieldNotFound
            return row.indexOf(ObjectStateWithLoop.Actual)
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