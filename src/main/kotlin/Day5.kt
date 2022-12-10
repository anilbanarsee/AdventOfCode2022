import java.util.regex.Pattern

typealias Table<T> = List<List<T>>
typealias MutableTable<T> = MutableList<MutableList<T>>

class Day5 {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) = result(readAsStream(args[0])!!.toList()).run(::println)

        private fun result(input: List<String>): Results = Results().apply {
            val startCrates = input.splitOn { it == "" }.first.toList().dropLast(1)// drop 1-9 column axis
                .map { it.toCharArray().takeEvery(4, 1) } // grab chars
                .run{ fill(this.size, this.last().size) }.transpose() // transpose into columns
                .map { char -> char.filter { it != ' ' } }.clean() //remove nulls

            val commands = input.splitOn { it == "" }.second.map { CraneCommand.parse(it) }

            part1 = commands.fold(startCrates.toMutableTable()) { crates, command -> command.actPart1(crates) }.map { it[0] }.joinToString("")
            part2 = commands.fold(startCrates.toMutableTable()) { crates, command -> command.actPart2(crates) }.map { it[0] }.joinToString("")
        }
    }
}

data class CraneCommand(var move: Int = 1, val from: Int, val to: Int) {
    companion object {
        private val PATTERN: Pattern = Pattern.compile("move (([^ ]*) *)from ([^ ]+) to ([^ \\n]+)")

        fun parse(string: String): CraneCommand = PATTERN.matcher(string).apply { find() }
            .run { CraneCommand( from = group(3).toInt()-1, to = group(4).toInt()-1 )
                .apply{ group(2)?.let { move = it.toInt() } } }
    }

    fun <T> actPart1(crates: MutableTable<T>) = crates.apply {
        this[to] = this[from].take(move).reversed().toMutableList().also { it.addAll(this[to]) }
        this[from] = this[from].drop(move).toMutableList()
    }

    fun <T> actPart2(crates: MutableTable<T>) = crates.apply {
        this[to] = this[from].take(move).toMutableList().also { it.addAll(this[to]) }
        this[from] = this[from].drop(move).toMutableList()
    }
}

private fun CharArray.takeEvery(step: Int, start: Int) =
    this.drop(start).filterIndexed { index, _ -> index % step == 0 }

private fun <T> Iterable<T>.splitOn(predicate: (T) -> Boolean): Pair<Iterable<T>, Iterable<T>> =
    this.indexOfFirst(predicate).let { this.take(it) to this.drop(it+1) }

fun <T> Table<T>.transpose(): Table<T> = List(this[0].size) { i -> List(this.size) { j -> this[j][i] } }

// fills out a table into a nullable table of size x/y, it is important for the contract of transpose that the table is filled
private fun <T> Table<T>.fill(x: Int, y: Int): Table<T?> = List(x) { i -> List(y) { j -> this.getOrNull(i)?.getOrNull(j)} }

private fun <T> Table<T?>.clean(): Table<T> = List(this.size) { i -> this[i].filterNotNull() }

private fun <T> Table<T>.toMutableTable(): MutableTable<T> = this.map { it.toMutableList() }.toMutableList()