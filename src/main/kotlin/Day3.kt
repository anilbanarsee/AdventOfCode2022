class Day3 {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) = result(readAsStream(args[0])!!.toList()).run(::println)

        private fun result(input: List<String>): Results = Results().apply {
            part1 = input.map { it.half().toList() }
                .flatMap { it.map { str -> str.toCharArray().toSet() }.getMatchingElements() }
                .sumOf { it.asInt() }

           part2 = input.chunked(3)
                .flatMap { it.map { str -> str.toCharArray().toSet() }.getMatchingElements() }
                .sumOf { it.asInt() }
        }

        private fun <T> Iterable<Set<T>>.getMatchingElements(): Iterable<T> =
            this.reduce { item, next -> item.filter { next.contains(it) }.toSet() }

        private fun String.half(): Pair<String, String> =
            this.slice(0 until this.length / 2) to this.slice(this.length / 2 until this.length)

        private fun Char.asInt(): Int = ALPHABET.toCharArray().indexOf(this).plus(1)
    }
}