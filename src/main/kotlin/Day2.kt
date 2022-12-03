import java.lang.IllegalArgumentException

class Day2 {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) = result(readAsStream(args[0])!!.toList()).run(::println)

        private fun result(input: List<String>): Results = Results().apply {
            part1 = input.map { line -> Pair(line[0].toRPS(), line[2].toRPS()) }
                .sumOf { (them, us) -> calculateScore(us, them) }

            part2 = input.map { line -> Pair(line[0].toRPS(), line[2].toCondition()) }
                .sumOf { (them, condition) -> calculateScore(condition.calculate(them), them) }
        }

        private fun calculateScore(us: RPS, them: RPS): Int = us.run { fight(them).score + baseScore }

        enum class RPS(val baseScore: Int, private val beats: () -> RPS) {
            ROCK(1, { SCISSORS }),
            PAPER(2, { ROCK }),
            SCISSORS(3, { PAPER });

            fun fight(rps: RPS): Condition = let {
                when (rps) {
                    this -> Condition.DRAW
                    this.beats() -> Condition.WIN
                    else -> Condition.LOSE
                }
            }

            fun beats(): RPS = beats.invoke()
        }

        enum class Condition(val score: Int, private val calculate: (RPS) -> RPS) {
            WIN(6, { it.beats().beats() }),
            LOSE(0, { it.beats() }),
            DRAW(3, { it });

            fun calculate(them: RPS): RPS = calculate.invoke(them)
        }

        private fun Char.toRPS(): RPS = when (this) {
            'X', 'A' -> RPS.ROCK
            'B', 'Y' -> RPS.PAPER
            'C', 'Z' -> RPS.SCISSORS
            else -> throw IllegalArgumentException()
        }

        private fun Char.toCondition(): Condition = when (this) {
            'X' -> Condition.LOSE
            'Y' -> Condition.DRAW
            'Z' -> Condition.WIN
            else -> throw IllegalArgumentException()
        }
    }
}