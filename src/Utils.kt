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

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)
