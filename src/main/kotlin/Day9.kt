import kotlin.math.absoluteValue

fun main(args: Array<String>) = result(readAsStream(args[0])!!.toList()).run(::println)

private fun result(input: List<String>): Results = Results().apply {
    val moves = input.map { line -> line.split(" ").pairOf({ it.toTraversal() }, { it.toInt() }) }

    part1 = moves.flatLastFold(Rope.ofLength(2)) { rope, (traversal, steps) -> rope.doSteps(traversal, steps) }
        .setOf { it.end().pos }.size

    part2 = moves.flatLastFold(Rope.ofLength(10)) { rope, (traversal, steps) -> rope.doSteps(traversal, steps) }
        .setOf { it.end().pos }.size
}


data class Rope(val pos: Coord = Coord(), val tail: Rope? = null) {
    companion object {
        fun ofLength(len: Int): Rope = (0 until len - 1).fold(Rope()) { rope, _ -> Rope(tail = rope) }
    }

    fun doSteps(traversal: Traversal, steps: Int) = (0 until steps)
        .fold(listOf(this)) { hist, _ ->
            hist.toMutableList().apply {
                add(hist.last().step(traversal))
            }
        }

    private fun step(traversal: Traversal): Rope = traversal.invoke(pos).let { newPos ->
        Rope(pos = newPos,
            tail = tail?.let {
                when (it.pos.touching(newPos)) {
                    true -> it
                    else -> it.pos.lookTo(newPos).run { it.step { Coord(x, y) } } } }
        )
    }

    fun end(): Rope = when (tail) {
        null -> this; else -> tail.end(); }
}

fun Coord.subtract(c: Coord): Coord = Coord(x - c.x, y - c.y)
fun Coord.add(c: Coord): Coord = Coord(x + c.x, y + c.y)
fun Coord.touching(c: Coord): Boolean = this.subtract(c).run { x.absoluteValue < 2 && y.absoluteValue < 2 }
fun Coord.lookTo(c: Coord): Coord = Coord(c.x.compareTo(x), c.y.compareTo(y)).let { this.add(it) }

fun <T, U> Iterable<T>.setOf(function: (T) -> U) = this.map { function.invoke(it) }.toSet()

fun <T, U> Iterable<T>.flatLastFold(start: U, function: (U, T) -> List<U>): List<U> =
    this.fold(mutableListOf(start)) { list, item -> list.apply { addAll(function.invoke(list.last(), item)) } }

fun <T, U, V> List<T>.pairOf(f1: (T) -> U, f2: (T) -> V): Pair<U, V> = f1.invoke(this[0]) to f2.invoke(this[1])

fun String.toTraversal(): Traversal = when (this) {
    "U" -> CardinalTraversal.UP.traversal
    "D" -> CardinalTraversal.DOWN.traversal
    "L" -> CardinalTraversal.LEFT.traversal
    "R" -> CardinalTraversal.RIGHT.traversal
    else -> error("Invalid direction")
}