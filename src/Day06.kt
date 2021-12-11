// Day 6, Advent of Code 2021

fun main() {
    val testInput = readInput("Day06_test") // just one line of input...

    check(count(testInput, 80) == 5934L)
    check(count(testInput, 256) == 26984457539L)

    val input = readInput("Day06")
    println(count(input, 80))
    println(count(input, 256))
}

private fun count(input: List<String>, days: Int) : Long {
    // Stack entries, 1 for each possible age 0..8
    val fish = MutableList(9) { 0L }// 0L is key, needs to be Long, not Int for Part 2
    input[0].split(",").forEach { fish[it.toInt()]++ }
    for (i in 1..days) { // days, 80 for part 1, 256 for part 2
        val start = fish.removeFirst() // Fish that are at timer (i.e. index) 1 become 0, etc.
        fish.add(start) // babies
        fish[6] += start // original fish moves from 0 to 6
    }
    return fish.reduce { sum, it -> sum + it }
}
