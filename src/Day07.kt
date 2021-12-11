import java.io.File
import kotlin.math.abs
import kotlin.math.min

// Day 7, Advent of Code 2021, The Treachery of Whales
// Align crab submarines
fun main() {
    val heightsTest = getHeightsSorted("Day07_test")

    // For Part 1, fuel calculation
    val absValue = { x: Int, y: Int -> abs(x - y) }

    // For Part 2, if dist is n, fuel is n*(n+1)/2
    val triangleValue = { x: Int, y: Int -> (abs(x - y) * (abs(x - y) + 1)) / 2 }

    check(minFuel(heightsTest, absValue) == 37)
    check(minFuel(heightsTest, triangleValue) == 168)

    val heights = getHeightsSorted("Day07")
    println(minFuel(heights, absValue))
    println(minFuel(heights, triangleValue))
}

private fun getHeightsSorted(name: String) = File("src", "$name.txt")
    .readText()
    .trim()
    .split(',')
    .map { it.toInt() }
    .sorted()

private fun minFuel(heightsSorted: List<Int>, fuelFunction: (Int, Int)-> Int): Int {
    var minSum = Integer.MAX_VALUE
    for (it in heightsSorted.first()..heightsSorted.last()) { // min to max values of heights
        val sum = heightsSorted.fold(0) { sum, j -> sum + fuelFunction(it, j) }
        minSum = min(minSum, sum)
    }
    return minSum
}
