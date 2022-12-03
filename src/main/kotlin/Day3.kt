class Day3 {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) = result(readAsStream(args[0])!!.toList()).run(::println)

        private fun result(input: List<String>): Results = Results().apply {
            part1 = input.map { it.half().toList() }
                .flatMap { it.map { str -> str.toCharArray().toSet() }.intersect() }
                .sumOf { it.asInt() }

           part2 = input.chunked(3)
                .flatMap { it.map { str -> str.toCharArray().toSet() }.intersect() }
                .sumOf { it.asInt() }
        }

        private fun <T> Iterable<Set<T>>.intersect(): Iterable<T> = this.reduce { item, next -> item.intersect(next) }

        private fun String.half(): Pair<String, String> = this.take(this.length / 2) to this.drop(this.length / 2)

        private fun Char.asInt(): Int = ALPHABET.toCharArray().indexOf(this).plus(1)
    }
}