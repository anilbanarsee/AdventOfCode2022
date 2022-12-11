import Monkey.Companion.DIV
import Monkey.Companion.PATTERN
import java.math.BigInteger
import java.util.regex.Pattern

fun main(args: Array<String>) = result(readAsStream(args[0])!!.toList()).run(::println)


private fun result(input: List<String>): Results = Results().apply {
    val chunked = input.chunkBy { it == "" }.map { it.joinToString("\n") }
    val mod = chunked.map { DIV.matcher(it).apply { find() }.group(1).toInt() }.product()

    val intMonkeys = mutableListOf<Monkey<Int>>().let { list ->
        chunked.fold(list){ l, str -> l.apply { add(str.toIntMonkey(list)) } }
    }
    val modMonkeys = mutableListOf<Monkey<ModAwareInt>>().let { list ->
        chunked.fold(list){ l, str -> l.apply { add(str.toModMonkey(list, mod)) } }
    }

    (1 .. 20).fold(mutableListOf(intMonkeys.map { it.items })) { table, _ ->
            table.apply { add(intMonkeys.onEach { it.act() }.map { it.items }) }
        }

    (1..10000).fold(mutableListOf(modMonkeys.map { it.items })) { table, _ ->
            table.apply { add(modMonkeys.onEach { it.act() }.map { it.items }) }
        }

    part1 = intMonkeys.map { it.inspected }.sortedDescending().take(2).product()
    part2 = modMonkeys.map { it.inspected }.sortedDescending().take(2).product()
}

data class Monkey<T>(
    var items: List<T> = emptyList(),
    var inspector: (T) -> T = { it },
    var thrower: (Monkey<T>) -> Unit = { },
    var inspected: BigInteger = BigInteger.ZERO,
    val monkeys: List<Monkey<T>>
) {
    companion object {
        val DIV: Pattern = Pattern.compile("Test: divisible by (.*)")
        val PATTERN: Pattern =
            Pattern.compile(
                " *Starting items: (.*)\\n *" +
                        "Operation: new = (.*)\\n *" +
                        "Test: divisible by (.*)\\n *" +
                        "If true: throw to monkey (.*)\\n *If false: throw to monkey (.*)"
            )
    }

    private fun inspectHeld() = this.apply {
        items = items.toMutableList().apply {
            this[0] = inspector.invoke(items[0])
        }
    }

    private fun throwHeld() = this.apply { thrower(this) }

    fun act() {
        items.onEach {
            inspectHeld().throwHeld()
        }
    }

    override fun toString(): String = items.toString()
}

private fun String.toModMonkey(monkeys: List<Monkey<ModAwareInt>>, mod: Int) =
    this.toGroups(PATTERN).let { groups ->
        Monkey(
            groups[0].split(", ").map { item -> ModAwareInt(item.toBigInteger(), mod.toBigInteger()) },
            groups[1].toModAwareOperator(mod),
            groups[2].toInt().let { div ->
                monkeyThrower({ it.rem(div.toBigInteger()) == BigInteger.ZERO }, groups[3].toInt(), groups[4].toInt())
            },
            monkeys = monkeys
        )
    }

private fun String.toIntMonkey(monkeys: List<Monkey<Int>>) =
    this.toGroups(PATTERN).let { groups ->
        Monkey(
            groups[0].split(", ").map { item -> item.toInt() },
            groups[1].toIntOperator(),
            groups[2].toInt().let { div ->
                monkeyThrower({ (it % div) == 0 }, groups[3].toInt(), groups[4].toInt())
            },
            monkeys = monkeys
        )
    }

fun List<BigInteger>.product(): BigInteger = this.fold(BigInteger.ONE) { a, b -> a * b }
fun List<Int>.product(): Int = this.fold(1) { a, b -> a * b }

fun String.toGroups(p: Pattern) =
    p.matcher(this).let { m -> m.find(); (1..m.groupCount()).map { m.group(it) } }

private fun <T> emptyMutableTable() = mutableListOf(mutableListOf<T>())

fun <T> Iterable<T>.chunkBy(predicate: (T) -> Boolean): List<List<T>> =
    this.fold(emptyMutableTable()) { list, item ->
        when (predicate.invoke(item)) {
            true -> list.apply { add(mutableListOf()) }
            false -> list.apply { last().add(item) }
        }
    }

fun String.toIntOperator(): (Int) -> Int = this.split(" ").run {
    val left = this[0].toIntOrNull()
    val right = this[2].toIntOrNull()
    when (this[1]) {
        "+" -> { a -> ((left ?: a) + (right ?: a)) / 3 }
        "-" -> { a -> ((left ?: a) - (right ?: a)) / 3 }
        "*" -> { a -> ((left ?: a) * (right ?: a)) / 3 }
        else -> error("Invalid operator")
    }
}

fun String.toModAwareOperator(divProduct: Int): (ModAwareInt) -> ModAwareInt = this.split(" ").run {
    val left = this[0].toIntOrNull()?.let { ModAwareInt(it.toBigInteger(), divProduct.toBigInteger()) }
    val right = this[2].toIntOrNull()?.let { ModAwareInt(it.toBigInteger(), divProduct.toBigInteger()) }
    when (this[1]) {
        "+" -> { a -> ((left ?: a) + (right ?: a)) }
        "-" -> { a -> ((left ?: a) - (right ?: a)) }
        "*" -> { a -> ((left ?: a) * (right ?: a)) }
        else -> error("Invalid operator")
    }
}

private fun <T> monkeyThrower(predicate: (T) -> Boolean, t: Int, f: Int): (Monkey<T>) -> Unit =
    { monkey ->
        val item = monkey.items[0]
        when (predicate.invoke(item)) {
            true -> monkey.monkeys[t].apply { items = items.toMutableList().apply { add(item) } }
            false -> monkey.monkeys[f].apply { items = items.toMutableList().apply { add(item) } }
        }
        monkey.items = monkey.items.drop(1)
        monkey.inspected++
    }

data class ModAwareInt(val value: BigInteger, val mod: BigInteger) {

    operator fun plus(other: BigInteger) = ModAwareInt((value + other) % mod, mod)
    operator fun plus(other: ModAwareInt) = ModAwareInt((value + other.value) % mod, mod)

    operator fun minus(other: BigInteger) = ModAwareInt((value - other) % mod, mod)
    operator fun minus(other: ModAwareInt) = ModAwareInt((value - other.value) % mod, mod)

    operator fun times(other: BigInteger) = ModAwareInt((value * other) % mod, mod)
    operator fun times(other: ModAwareInt) = ModAwareInt((value * other.value) % mod, mod)

    operator fun rem(mod: BigInteger) = value % mod
}