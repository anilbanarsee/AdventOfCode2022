class Day6 {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) = result(readAsStream(args[0])!!.toList()).run(::println)

        private fun result(input: List<String>): Results = Results().apply {
            part1 = input[0].windowed(4).indexOfFirst { it.toSet().size == 4 } + 4
            part2 = input[0].windowed(14).indexOfFirst { it.toSet().size == 14 } + 14
        }
    }
}
