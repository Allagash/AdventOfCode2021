// Day 8, Advent of Code 2021, Seven Segment Search

// Canonical solution, with a, b, c, etc. mapped to digit segments given in problem description
val canon = "abcdefg"
// Canonical digits, where each string's characters are sorted.
// Digit 0 has 7 segments (characters) is at index 0, digit 1 has 2 segments and is at index 1, etc.
val canonDigits = arrayListOf("abcefg", "cf", "acdeg", "acdfg", "bcdf", "abdfg", "abdefg", "acf", "abcdefg", "abcdfg")

// Return a list of all permutations of the characters of a string
// For "abc", this returns ["abc", "acb", "bac", "bca", "cab", "cba"]
fun permutations(str: String) : List<String> {
    if (str.length == 1) return listOf(str)
    val ret = mutableListOf<String>()
    str.forEachIndexed { i, c ->
        val permsSubStr = permutations(str.substring(0, i) + str.substring(i + 1))
        permsSubStr.forEach {
            ret.add(c + it)
        }
    }
    return ret
}

// sort the characters within the string
fun String.sort() = String(toCharArray().apply { sort() })

// given a key, unscramble the segment mapping
// sort the characters before returning
fun decode(key: String, cipherText: String) : String {
    val decoded = cipherText.fold("") { str, i ->
        str + canon[key.indexOf(i)]
    }
    return decoded.sort()
}

data class Encoded(val patterns: List<String>, val outputs: List<String>)

fun main() {
    fun part1(encoded: List<Encoded>): Int =
        encoded.fold(0) { allSum, i ->
            allSum + i.outputs.fold(0) { sum, j ->
                sum + if (j.length in listOf(2, 3, 4, 7)) 1 else 0
            }
        }

    fun part2(encoded: List<Encoded>): Int {
        val perms = permutations(canon) // all possible scrambled digits encoded
        var sum = 0
        encoded.forEach { e -> // go through all lines of input
            perms.forEach perm@{ p -> // brute force try all encodings
                for (reading in e.patterns) {
                    if (decode(p, reading) !in canonDigits) {
                        return@perm // not it, try next solution
                    }
                }
                // We have a solution, decode the output digits
                val answer = e.outputs.fold(0) { sum, i ->
                    sum * 10 + canonDigits.indexOf(decode(p, i))
                }
                sum += answer
            }
        }
        return sum
    }

    val inputTest = createdEncoded(readInput("Day08_test"))
    check(part1(inputTest) == 26)
    check(part2(inputTest) == 61229)

    val input = createdEncoded(readInput("Day08"))
    println(part1(input))
    println(part2(input))
}

private fun createdEncoded(input: List<String>) : List<Encoded> {
    val encodedInput = ArrayList<Encoded>()
    input.forEach {
        encodedInput.add(
            Encoded(
                it.substringBefore('|').trim().split(' ').map { j -> j.sort() },
                it.substringAfter('|').trim().split(' ').map { j -> j.sort() }
            )
        )
    }
    return encodedInput
}
