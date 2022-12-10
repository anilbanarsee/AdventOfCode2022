import kotlin.math.absoluteValue

fun main(args: Array<String>) = result(readAsStream(args[0])!!.toList()).run(::println)

private fun result(input: List<String>): Results = Results().apply {
    val cycles = input.map { parseOperator(it) }.fold(listOf(1)) { list, cmd -> cmd.invoke(list) }

    part1 = cycles.drop(1).takeEvery(20, 40).mapIndexed { i, n -> (((i * 40) + 20) * n) }.take(6).sum()

    val chunked = cycles.chunked(40)
    part2 = (0 until 6).fold("") { line, y ->
        (0 until 40).fold("") { r, x ->
            when ((chunked[y][x] - x).absoluteValue < 2) {
                true -> "$r#"
                false -> "$r."
            }
        }.let { "$line\n$it" }
    }
}

fun <T> List<T>.stack(): List<T> = this.toMutableList().apply { add(this.last()) }
fun <T> List<T>.stack(n: Int): List<T> = (0 until n).fold(this) { list, _ -> list.stack() }
fun <T> List<T>.stackMerge(value: T, n: Int = 1, function: (T, T) -> T): List<T> =
    this.stack(n).toMutableList().apply {
        this[lastIndex] = function.invoke(value, last())
    }

fun <T> List<T>.takeEvery(start: Int, n: Int) = this.filterIndexed { i, _ -> (i + start + 2) % (n) == 0 }

fun parseOperator(str: String): (List<Int>) -> List<Int> = str.split(" ").let { cmd ->
    when (cmd[0]) {
        "noop" -> { list -> list.stack() }
        "addx" -> cmd[1].toInt().let {
            { list -> list.stackMerge(it, 2, Int::plus) }
        }
        else -> error("Invalid operator")
    }
}