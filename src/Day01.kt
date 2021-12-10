// Day 1, Advent of Code 2021

fun main() {
    fun part1(input: List<Int>): Int {
        var count = 0
        for (i in 0 until input.size-1) {
            if (input[i+1] > input[i]) {
                count++
            }
        }
        return count
    }

    fun part2(input: List<Int>): Int {
        var count = 0
        // sliding window part 2
        var slidingWindow = input[0] + input[1] + input[2]

        for (i in 1..input.size-3) {
            val slidingWindowNext = input[i] + input[i+1] + input[i+2]
            if (slidingWindowNext > slidingWindow) {
                count++
            }
            slidingWindow = slidingWindowNext
        }
        return count
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInputAsInts("Day01_test")
    check(part1(testInput) == 7)
    check(part2(testInput) == 5)

    val input = readInputAsInts("Day01")
    println(part1(input))
    println(part2(input))
}
