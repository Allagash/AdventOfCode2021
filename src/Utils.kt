import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

// For Day 2
data class Vector(var direction: String, var amount: Int)

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt").readLines()

fun readInputAsInts(name: String) = File("src", "$name.txt").readLines().map { it.toInt() }

fun readInputsAsVectors(name: String) : List<Vector> {
    val testInput = File("src", "$name.txt").readLines().map {
        it.trim().split(" ")
    }
    return testInput.map {
        Vector(it[0], it[1].toInt())
    }
}

// Read a 2D grid as input. Each cell is one digit.
fun readSingleDigitGrid(name: String) : List<List<Int>> {
    // convert each ASCII char to an int, so '0' -> 0, '1' -> 1, etc.
    return File("src", "$name.txt").readLines().map { it.trim().map { j -> j.code - '0'.code } }
}

// Stack
// https://stackoverflow.com/questions/46900048/how-can-i-use-stack-in-kotlin

typealias Stack<T> = MutableList<T>

fun <T> mutableStackOf(): Stack<T> = mutableListOf<T>()

fun <T> Stack<T>.push(item: T) = add(item)

fun <T> Stack<T>.pop(): T = if (isNotEmpty()) removeAt(lastIndex) else throw Exception("Can't pop from empty stack!")

fun <T> Stack<T>.peek(): T = if (isNotEmpty()) this[lastIndex] else throw Exception("Can't call peek on empty stack!")

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)
