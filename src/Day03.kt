// Day 3, Advent of Code 2021
fun main() {
    fun part1(input: List<String>): Int {
        val bitCount = IntArray(input[0].length)
        input.forEach { str -> // explicit lambda parameter
            for (i in str.indices) {
                bitCount[i] += if (str[i] == '1') 1 else 0
            }
        }
        val half = input.size / 2

        var gamma = 0
        var epsilon = 0
        for (i in bitCount.indices) {
            gamma = gamma shl 1
            epsilon = epsilon shl 1
            if (bitCount[i] > half) {
                gamma = gamma or 1
            } else {
                epsilon = epsilon or 1
            }
        }
        return gamma * epsilon
    }

    fun part2(input: List<String>): Int {
        return calcOxygen(true, input) * calcOxygen(false, input)

    }

    // test if implementation meets criteria from the description, like:
    var input = readInput("Day03_test")

    check(part1(input) == 198)
    check(part2(input) == 230)

    input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}

fun calcOxygen(mostCommon: Boolean, lines: List<String>) : Int {
    var process = lines.toMutableSet()
    var index = 0

    while (process.size > 1) {
        val ones = mutableSetOf<String>()
        val zeros = mutableSetOf<String>()
        process.forEach {
            if (it[index] == '1') {
                ones.add(it)
            } else {
                zeros.add(it)
            }
        }
        process = if (mostCommon) {
            if (ones.size >= zeros.size) ones else zeros
        } else {
            if (ones.size >= zeros.size) zeros else ones
        }
        if (process.size == 1) {
            return process.first().toInt(2)
        }
        index++
    }
    if (process.size == 1) {
        return process.first().toInt(2)
    }

    return 0
}
