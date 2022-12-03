package com.abanarsee.adventofcode.year2021

import Results
import readAsStream

class Year2021Day1 {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) = result(readAsStream(args[0])!!.toList()).let(::println)

        private fun result(input: Iterable<String>): Results = Results().apply {
            val depths = input.toList().map { it.toInt() }

            part1 = depths.filterByNext { item, next -> item > next }.size

            part2 = depths.windowed(3).map { it.sum() }.filterByNext{ item, next -> item > next }.size
        }

        private fun <T> List<T>.filterByNext( predicate : (T, T) -> Boolean) : List<T> =
            this.drop(1).filterIndexed{ index, item -> predicate.invoke(item, this[index]) }

    }
}