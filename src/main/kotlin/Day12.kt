import kotlin.math.abs

fun main(args: Array<String>) = result(readAsStream(args[0])!!.toList()).run(::println)

private fun result(input: List<String>): Results = Results().apply {
    val grid = Array(input.size) { i -> Array(input[0].length) { j -> input[i][j] } }
    val start = grid.coordOf { it == 'S' }.first()
    val end = grid.coordOf { it == 'E' }.first()

    part1 = grid.matrixMap { it.toHeight() }
        .pathToWithStepRule(start, end,
            { from, to -> to <= from + 1 }) !!.size

    val startCandidates = grid.coordOf { it == 'S' || it == 'a' }.sortedBy { it.manhattanTo(end) }
    part2 = startCandidates.fold(Int.MAX_VALUE) { size, s ->
        val path = grid.matrixMap { it.toHeight() }
            .pathToWithStepRule(s, end,
                { from, to -> to <= from + 1 }, { it.getPath().size > size })?.size ?: Int.MAX_VALUE
        when(path < size) {
            true -> path
            else -> size
        }
    }
}

fun <T> List<T>.mutableAdd(item: T) = this.toMutableList().apply { add(item) }

fun <T> List<T>.mutableAddAll(items: Iterable<T>) = this.toMutableList().apply { addAll(items) }

fun <T> Coord.outOfBoundsOf(matrix: Matrix<T>) =
    this.x < 0 || this.y < 0 || this.x >= matrix.size || this.y >= matrix[0].size

fun Coord.manhattanTo(other: Coord) = abs(this.x - other.x) + abs(this.y - other.y)

fun <T> Matrix<T>.getCardinals(coord: Coord) = CardinalTraversal.values()
    .map { c -> c.traversal.invoke(coord) }
    .filter { !it.outOfBoundsOf(this) }

fun <T> Matrix<T>.at(coord: Coord) = this[coord.x][coord.y]
fun <T> Matrix<T>.coordOf(predicate: (T) -> Boolean): List<Coord> =
    this.foldIndexed(mutableListOf()) { x, list, row ->
        row.foldIndexed<T, MutableList<Coord>>(mutableListOf()) { y, r, t ->
            when (predicate.invoke(t)) {
                true ->
                    r.mutableAdd(Coord(x, y))
                false -> r
            }
        }.let {
            list.mutableAddAll(it)
        }
    }

inline fun <T, reified U> Matrix<T>.matrixMap(function: (T) -> U): Matrix<U> =
    this.map { row -> row.map { item -> function.invoke(item) }.toTypedArray() }.toTypedArray()

data class PathNode<T>(val value: T, val coord: Coord, val parent: PathNode<T>?, val f: Int) {

    override fun toString(): String =
        "{ $value, $coord, $f }"
}

fun <T> PathNode<T>.getPath(list: List<PathNode<T>> = listOf()): List<PathNode<T>> =
    when (parent) {
        null -> list
        else -> parent.getPath(list.mutableAdd(this))
    }

fun <T> Matrix<T>.pathToWithStepRule(
    start: Coord,
    end: Coord,
    stepRule: (T, T) -> Boolean,
    abortCondition: (PathNode<T>) -> Boolean = { _ -> false}
): List<PathNode<T>>? {
    val open = mutableListOf(PathNode(this.at(start), start, null, 0))
    val closed = mutableListOf<PathNode<T>>()

    while (open.isNotEmpty()) {
        val q = open.apply { sortBy { it.f } }.removeAt(0)

        if(abortCondition(q)){
            return null
        }

        val cardinals = this.getCardinals(q.coord).asSequence()
            .filter { stepRule.invoke(this.at(q.coord), this.at(it)) } // apply step rule
            .map { PathNode(this.at(it), it, q, q.getPath().size + q.coord.manhattanTo(it) + it.manhattanTo(end)) }

        cardinals.find { it.coord == end }?.let { return it.getPath() }

        cardinals
            .filter { s -> open.filter { it.coord == s.coord }.let { it.isEmpty() || !it.any { t -> t.f <= s.f } } }
            .filter { s -> closed.filter { it.coord == s.coord }.let { it.isEmpty() || !it.any { t -> t.f <= s.f } } }
            .forEach { open.add(it) }
        closed.add(0, q)
    }
    return null
}

fun Char.toHeight(): Int = when (this) {
    'S' -> 0
    'E' -> 25
    else -> (this.code - 97)
}