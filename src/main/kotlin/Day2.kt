import java.util.function.Function
import java.util.function.Supplier

class Day2 {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val lines = readAsStream(args[0])!!.toList()

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

            println(totalScore)
            println(scoreP2)
        }

        enum class RPS(private val baseScore: Int, private val beats: Supplier<RPS>) {
            ROCK(1, { SCISSORS }),
            PAPER(2, { ROCK }),
            SCISSORS(3, { PAPER });

            fun fight(rps: RPS): Int = let {
                when (rps) {
                    this -> 3//draw
                    this.beats() -> 6//win
                    else -> 0//loss
                }
            }.plus(this.baseScore)

            fun beats(): RPS = beats.get()
        }

        enum class Condition(private val calculate: Function<RPS, RPS>) {
            WIN({ it.beats().beats() }),
            LOSE({ it.beats() }),
            DRAW({ it });

            fun calculate(them: RPS): RPS = calculate.apply(them)
        }
    }
}