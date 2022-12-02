import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Predicate
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.streams.toList

class Main {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

            val inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream(args[0]) ?: return
            val lines = BufferedReader(InputStreamReader(inputStream)).lines().toList()

            val max = lines.splitBy { it.equals("") }.maxOfOrNull { list -> list.sumOf { it.toInt() } }

            val top3 = lines.splitBy { it.equals("") }
                .map { list -> list.sumOf(String::toInt) }.sortedDescending()
                .slice(0..2)
                .sum()

            val max2 = lines.stream()
                .map { if(it == "") "#" else it }
                .collect(Collectors.joining("|"))
                .split("|#|")
                .maxOfOrNull { s -> s.split("|").sumOf(String::toInt)}

            println(max)
            println(top3)
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