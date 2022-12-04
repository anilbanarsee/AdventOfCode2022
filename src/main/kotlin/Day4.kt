import kotlin.math.max

class Day4 {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) = result(readAsStream(args[0])!!.toList()).run(::println)

        private fun result(input: List<String>): Results = Results().apply {
            val sections = input.map { line -> line.pairOf(",") { it.pairOf("-", String::toInt).asRange() } }
            // an implementation of range.superset could be: (a.first > b.first != a.last > b.last) || a.first == b.first || a.last == b.last
            part1 = sections.count { (a, b) -> a.union(b).count() == max(a.count(), b.count()) }
            part2 = sections.count { (a, b) -> a.intersect(b).isNotEmpty() }
        }

        private fun <T> String.pairOf(delimiter: String, function: (String) -> T): Pair<T, T> =
            split(delimiter).map(function).let { it[0] to it[1] }

        private fun Pair<Int, Int>.asRange(): IntRange = first..second
    }
}