import kotlin.math.*

// Day05, Advent of Code 2021
data class Line (val x0: Int, val y0: Int, val x1: Int, val y1: Int)

fun main() {
    var input = readInput("Day05_test")
    val (answer1, answer2) = parts1and2(input)

    check(answer1 == 5)
    check(answer2 == 12)

    input = readInput("Day05")
    val answers = parts1and2(input)
    println(answers.first)
    println(answers.second)
}

private fun parts1and2(input: List<String>): Pair<Int, Int> {
    val lines = mutableListOf<Line>()
    input.forEach { it ->
        val vals = it.split(",", " -> ")
        lines.add(Line(vals[0].toInt(), vals[1].toInt(), vals[2].toInt(), vals[3].toInt()))
    }

    val map1 = Array(1000) { Array(1000) { 0 } }
    val map2 = Array(1000) { Array(1000) { 0 } }
    lines.forEach {
        val xInc = getInc(it.x0, it.x1)
        val yInc = getInc(it.y0, it.y1)
        var x = it.x0
        var y = it.y0
        val numPts = max(abs(it.x0 - it.x1) + 1, abs(it.y0 - it.y1) + 1)
        for (i in 0 until numPts) {
            if ((xInc == 0) or (yInc == 0)) map1[x][y]++ // horizontal or vertical
            map2[x][y]++
            x += xInc
            y += yInc
        }
    }
    return Pair(count(map1), count(map2))
}

private fun count(map: Array<Array<Int>>) : Int {
    var answer = 0
    map.forEach {
        it.forEach { idx ->
            if (idx > 1) {
                answer++
            }
        }
    }
    return answer
}

private fun getInc(a: Int, b: Int) =
    when {
        a < b -> 1
        a > b -> -1
        else -> 0
    }