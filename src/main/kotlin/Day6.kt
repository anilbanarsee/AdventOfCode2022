class Day6 {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) = result(readAsStream(args[0])!!.toList()).run(::println)

        private fun result(input: List<String>): Results = Results().apply {
            part1 = 4.let { window -> input[0].windowed(window).indexOfFirst { it.toSet().size == window } + window }
            part2 = 14.let { window -> input[0].windowed(window).indexOfFirst { it.toSet().size == window } + window }
        }
    }
}
