class Day3 {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val lines = readAsStream(args[0])!!.toList()

            val part1 = lines.map { it.half().toList() }
                .flatMap { it.map { str -> str.toCharArray().toSet() }.getMatchingElements() }
                .sumOf { it.asInt() }

            val part2 = lines.chunked(3)
                .flatMap { it.map { str -> str.toCharArray().toSet() }.getMatchingElements() }
                .sumOf { it.asInt() }

            Results(part1, part2).also { println(it) }
        }

        private fun <T> Iterable<Iterable<T>>.getMatchingElements(): Iterable<T> =
            this.reduce { item, next -> item.filter { next.contains(it) }.toSet() }

        private fun String.half(): Pair<String, String> =
            this.slice(0 until this.length / 2) to this.slice(this.length / 2 until this.length)

        private fun Char.asInt(): Int = ALPHABET.toCharArray().indexOf(this).plus(1)
    }
}