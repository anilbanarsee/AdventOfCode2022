typealias Matrix<T> = Array<out Array<T>>
typealias Traversal = (Coord) -> Coord
typealias Context<T> = Pair<Matrix<T>, Coord>

fun main(args: Array<String>) = result(readAsStream(args[0])!!.toList()).run(::println)

private fun result(input: List<String>): Results = Results().apply {
    val grid = Array(input.size) { i -> Array(input[0].length) { j -> input[i][j].digitToInt() } }

    part1 = grid.matrixMapIndexed { x, y, tree -> Context(grid, Coord(x, y)).run {
                lookCardinal().any { view -> view.all { it < tree } } //look in all directions and check if we're blocked
            } }.flatten().count { it } //count where we have at least one unblocked

    part2 = grid.matrixMapIndexed { x, y, tree -> Context(grid, Coord(x, y)).run {
                lookCardinal().map { view -> view.lookUntil { it >= tree } } //look in all directions until we're blocked
                    .fold(1) { a, b -> a * b.count() } //take product of count
            } }.flatten().max()
}

fun <T> Context<T>.get(): T? = this.let { (m, p) -> m.getOrNull(p.x)?.getOrNull(p.y) }

fun <T> Context<T>.step(traversal: Traversal): Context<T> = this.let { (m, p) -> Context(m, traversal.invoke(p)) }

fun <T> Context<T>.traverse(traversal: Traversal, identity: List<T> = listOf()): List<T> = when (get()) {
    null -> identity
    else -> step(traversal).traverse(traversal, identity.toMutableList().apply { add(get()!!) })
}

fun <T> Context<T>.lookCardinal(): List<List<T>> =
    CardinalTraversal.values().map { this.traverse(it.traversal).drop(1) }

fun <T> Iterable<T>.lookUntil(predicate: (T) -> Boolean): Iterable<T> = this.indexOfFirst(predicate).let {
    when (it) {
        -1 -> this
        else -> this.take(it + 1)
    }
}

fun <T, U> Matrix<T>.matrixMapIndexed(function: (Int, Int, T) -> U): List<List<U>> =
    this.mapIndexed { x, row -> row.mapIndexed { y, item -> function.invoke(x, y, item) } }

data class Coord(val x: Int = 0, val y: Int = 0)

enum class CardinalTraversal(val traversal: Traversal, private val reverse: () -> CardinalTraversal) {
    UP({ it.copy(x = it.x, y = it.y + 1) }, {DOWN}),
    DOWN({ it.copy(x = it.x, y = it.y - 1) }, {UP}),
    LEFT({ it.copy(x = it.x + 1, y = it.y) }, {RIGHT}),
    RIGHT({ it.copy(x = it.x - 1, y = it.y) }, {LEFT});
}
