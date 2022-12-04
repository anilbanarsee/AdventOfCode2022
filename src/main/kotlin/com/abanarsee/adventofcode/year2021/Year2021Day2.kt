package com.abanarsee.adventofcode.year2021

import Results
import readAsStream

class Year2021Day2 {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) = result(readAsStream(args[0])!!.toList()).let(::println)

        private fun result(input: Iterable<String>): Results = Results().apply {
            val commands = input.map { line -> line.split(" ").let { it[0].toDirection() to it[1].toInt() } }

            part1 = commands
                .fold(Submarine()) { submarine, (direction, distance) -> submarine.move(direction, distance) }
                .let { it.x * it.y }

            part2 = commands.map { (direction, distance) -> direction.toAim() to distance }
                .fold(Submarine()) { submarine, (direction, distance) -> submarine.move(direction, distance) }
                .let { it.x * it.y }
        }

        data class Submarine(var x: Int = 0, var y: Int = 0, var aim: Int = 0) {
            fun move(direction: Direction, distance: Int): Submarine = direction.movement.invoke(this, distance)
        }

        enum class Direction(val movement: (Submarine, Int) -> Submarine) {
            UP({ sub, d -> sub.apply { y -= d } }),
            AIM_UP({ sub, d -> sub.apply { aim -= d } }),
            DOWN({ sub, d -> sub.apply { y += d } }),
            AIM_DOWN({ sub, d -> sub.apply { aim += d } }),
            FORWARD({ sub, d -> sub.apply { x += d; y += aim * d } });

            fun toAim(): Direction = when(this) {
                UP -> AIM_UP
                DOWN -> AIM_DOWN
                else -> this
            }
        }

        private fun String.toDirection(): Direction = Direction.valueOf(this.uppercase())
    }
}