import java.util.function.Predicate

class Day1 {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

            val lines = readAsStream(args[0])!!.toList()

            val part1 = lines.splitBy { it.equals("") }.maxOfOrNull { list -> list.sumOf { it.toInt() } }

            val part2 = lines.splitBy { it.equals("") }
                .map { list -> list.sumOf(String::toInt) }.sortedDescending()
                .slice(0..2)
                .sum()

            Results(part1, part2).also { println(it) }
        }

        /*
        Splits an Iterable<T> into an Iterable<Iterable<T>> based on a given predicate applied to
        each item T. Items that match the predicate are not added to any iterable.
         */
        private fun <T> Iterable<T>.splitBy(predicate: Predicate<T>): Iterable<Iterable<T>> {
            val subList = ArrayList<T>()
            val list = ArrayList<Iterable<T>>()

            this.onEach {
                if (predicate.test(it) || !iterator().hasNext()) {
                    list.add(subList.copyOf())
                    subList.clear()
                } else {
                    subList.add(it)
                }
            }

            return list
        }

        private fun <T> List<T>.copyOf(): List<T> {
            return mutableListOf<T>().also { it.addAll(this) }
        }
    }
}