package game

fun <T> MutableList<T>.swapList(newList: List<T>) {
    clear()
    addAll(newList)
}