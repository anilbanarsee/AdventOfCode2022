class Day7 {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) = result(readAsStream(args[0])!!.toList()).run(::println)

        private fun result(input: List<String>): Results = Results().apply {
            val head = input.cutOn { it.startsWith("$") } //cut inputs into chunks
                .map { it[0].toFileCommand().apply { output = it.drop(1) } } // convert chunks into commands
                .fold(File(name = "/")) { dir, cmd -> cmd.function.invoke(dir, cmd)!! }
                .cd("/")!! //run commands, then grab head

            part1 = head.dirs().map { it.size() }.filter { it < 100000 }.sum() // get sizes of dirs and sum <100000
            part2 = (head.size() + 30000000 - 70000000)
                .let { required -> head.dirs().map { it.size() }.filter { it > required } }.min()

        }
    }
}

data class FileCommand(
    val function: (File, FileCommand) -> File?,
    val params: List<String> = listOf(),
    var output: List<String> = listOf()
) {
    companion object {
        fun getCommandFunction(str: String): (File, FileCommand) -> File? = when(str){
            "cd" -> {dir, cmd -> dir.cd(cmd.params[0]) }
            "ls" -> { dir, cmd -> cmd.output
                .fold(dir) { d, item -> item.split(" ").let { d.make(it[1], it[0].toIntOrNull()) } } }
            else -> error("Unsupported file command")
        }
    }
}

fun String.toFileCommand(): FileCommand = this.split(" ").let {
    FileCommand(FileCommand.getCommandFunction(it[1]), it.drop(2))
}

data class File(
    val parent: File? = null,
    val subFiles: MutableList<File> = mutableListOf(),
    val name: String,
    var fileSize: Int = 0
) {

    fun dirs(): List<File> = subFiles.fold(mutableListOf()) { list, file ->
        when (file.subFiles.size) {
            0 -> list
            else -> list.apply { add(file) }.apply { addAll(file.dirs()) }
        }
    }

    fun size(): Int = subFiles.fold(0) { total, file -> total + file.fileSize + file.size() }

    fun make(name: String, size: Int?): File = this.apply {
        subFiles.add(File(parent = this, name = name).apply { size?.let { this.fileSize = it } })
    }

    fun cd(name: String): File? = when (name) {
        "/" -> head()
        ".." -> parent
        else -> subFiles.find { it.name == name }
    }

    private fun head(): File = when (parent) {
        null -> this; else -> parent.head()
    }
}

fun <T> List<T>.cutOn(predicate: (T) -> Boolean): List<List<T>> =
    this.fold(mutableListOf<MutableList<T>>()) { list, item ->
        list.apply {
            predicate.invoke(item).let {
                when (it) {
                    true -> list.add(mutableListOf(item))
                    false -> list.last().add(item)
                }
            }
        }
    }

fun String.toIntOrNull(): Int? = try { toInt() } catch (e: NumberFormatException) { null }