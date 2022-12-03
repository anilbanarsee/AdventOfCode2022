@file:JvmName("Utils")

import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Stream

fun readAsStream(resourceName: String): Stream<String>? {

    val inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream(resourceName) ?: return null
    return BufferedReader(InputStreamReader(inputStream)).lines()
}

const val ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
