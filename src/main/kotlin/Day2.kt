import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.function.BiFunction
import java.util.function.Function

class Day2 {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream(args[0]) ?: return
            val lines = BufferedReader(InputStreamReader(inputStream)).lines().toList()

            val rpsMap = mapOf(
                Pair('A', RPS.ROCK), Pair('X', RPS.ROCK),
                Pair('B', RPS.PAPER), Pair('Y', RPS.PAPER),
                Pair('C', RPS.SCISSORS), Pair('Z', RPS.SCISSORS)
            )

            val conditionMap = mapOf(Pair('X', Condition.LOSE), Pair('Y', Condition.DRAW), Pair('Z', Condition.WIN))

            val totalScore = lines.map { line -> Pair(rpsMap[line[0]]!!, rpsMap[line[2]]!!) }
                .sumOf { (them, us) -> us.fight(them) }

            val scoreP2 = lines.map { line -> Pair(rpsMap[line[0]]!!, conditionMap[line[2]]!!) }
                .sumOf { (them, condition) -> condition.calculate(them).fight(them) }

            println(totalScore);

            println(scoreP2);

        }

        enum class RPS(private val baseScore: Int) {
            ROCK(1) { override fun beats(): RPS = SCISSORS },
            PAPER(2) { override fun beats(): RPS = ROCK },
            SCISSORS(3) { override fun beats(): RPS = PAPER };

            abstract fun beats(): RPS

            fun fight(rps: RPS): Int = let {
                when (rps) {
                    this -> 3//draw
                    this.beats() -> 6//win
                    else -> 0//loss
                }
            }.plus(this.baseScore)
        }

        enum class Condition {
            WIN { override fun calculate(them: RPS): RPS = them.beats().beats() },
            LOSE { override fun calculate(them: RPS): RPS = them.beats()},
            DRAW { override fun calculate(them: RPS): RPS = them};

            abstract fun calculate(them: RPS): RPS
        }
    }
}