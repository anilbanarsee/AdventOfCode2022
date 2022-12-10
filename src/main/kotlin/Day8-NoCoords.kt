fun main(args: Array<String>) = result(readAsStream(args[0])!!.toList()).run(::println)

private fun result(input: List<String>): Results = Results().apply {
    val grid = List(input.size) { i -> List(input[0].length) { j -> input[i][j].digitToInt() } }

    part1 = grid.tableWedgeReduce({ tree, view -> view.all { it < tree} }, { a, b, c, d -> a || b || c || d })
        .flatten().count { it }

    part2 = grid.tableWedgeReduce({tree, view -> view.lookUntil { it >= tree }.count()}, {a, b, c, d -> a * b * c * d })
        .flatten().max()
}
// Merges like for like items with a given merge function
fun <T, U> Table<T>.mergeTo(table: Table<T>, function: (T, T) -> U): Table<U> =
    List(size) { i -> List(this[0].size) { j -> function.invoke(this[i][j], table[i][j])} }
// Performs a 2 way fold where the item is mapped with forward and backwards list items. These mapped values are then merged.
// the backwards list items are viewed as FIFO.
fun <T, U, V> List<T>.biWedgeReduce(function: (T, List<T>) -> U, acc: (U, U) -> V): List<V> = mutableListOf<V>()
    .also { total -> this.foldIndexed(listOf<T>()) { i, list, item ->
        total.add(acc.invoke(function.invoke(item, list.reversed()), function.invoke(item, this.drop(i+1))))
        list.toMutableList().apply { add(item)}
    }
}
// Performs a 4 directional fold using the provided function. These mapped values are then merged.
fun <T, U, V> Table<T>.tableWedgeReduce(function: (T, List<T>) -> U, acc: (U, U, U, U) -> V): Table<V> =
    this.map { it.biWedgeReduce(function) { a, b -> a to b } }
        .mergeTo(
            this.transpose().map { it.biWedgeReduce(function) { a, b -> a to b } }.transpose() //transpose, then return to time and space
        ) { (a, b), (c, d) -> acc.invoke(a, b, c, d) }