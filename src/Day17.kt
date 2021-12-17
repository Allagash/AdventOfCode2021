// Day 17, Advent of Code 2021, Trick Shot
import kotlin.math.max

data class Trench(val minX: Int, val maxX: Int, val minY: Int, val maxY: Int) {

    fun intersects(x: Int, y: Int): Boolean {
        return (x in minX..maxX) && (y in minY..maxY)
    }
}

fun main() {
    fun part1and2(trench: Trench): Pair<Int, Int> {
        val part2Answer = mutableSetOf<Pair<Int, Int>>()
        var maxYHeightOverall = Int.MIN_VALUE
        for (initXVel in 0..trench.maxX + 1) {
            for (initYVel in trench.minY - 1..1000) { // what's the heuristic for upper bound?
                var x = 0
                var y = 0
                var xv = initXVel
                var yv = initYVel
                var maxYHeight = Int.MIN_VALUE
                do {
                    maxYHeight = max(maxYHeight, y)
                    x += xv
                    xv = when {
                        (xv > 0) -> xv - 1
                        (xv < 0) -> xv + 1
                        else -> xv
                    }
                    y += yv
                    yv -= 1
                    if (trench.intersects(x, y)) {
                        part2Answer.add(Pair(initXVel, initYVel))
                        if (maxYHeight > maxYHeightOverall) {
                            maxYHeightOverall = maxYHeight
                        }
                        break
                    }
                } while (x <= trench.maxX && y >= trench.minY)
            }
        }
        return Pair(maxYHeightOverall, part2Answer.size)
    }

    fun parseInput(input: String): Trench {
        val coords = input.split("target area: x=", "..", ", y=")
            .filter { it.isNotEmpty() }
            .map { it.toInt() }
        return Trench(coords[0], coords[1], coords[2], coords[3])
    }

    val testInput = parseInput(readInputAsOneLine("Day17_test"))
    val testOutput = part1and2(testInput)
    check(testOutput.first == 45)
    check(testOutput.second == 112)

    val input = parseInput(readInputAsOneLine("Day17"))
    val output = part1and2(input)
    output.toList().forEach { println(it) }
}
