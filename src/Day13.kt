// Day 13, Advent of Code 2021, Transparent Origami

data class Fold(val axis: String, val amt: Int)

typealias Point = Pair<Int, Int>

fun parseInput(input: List<String>) : Pair<Set<Point>, List<Fold>> {
    var i = 0
    val points = mutableSetOf<Point>()
    val folds = mutableListOf<Fold>()
    while (i < input.size) {
        val line = input[i]
        i++
        if (line.isEmpty()) break

        // how map list of 2 items to Pair?
        // Try https://stackoverflow.com/questions/49518438/idiomatic-way-to-convert-a-list-to-a-pair-in-kotlin/49519857
        val pt = line.split(',').map{it.toInt()}.zipWithNext().single()
        points.add(pt)
    }
    val PREFIX = "fold along "
    while (i < input.size) {
        val line = input[i]
        i++
        if (line.isEmpty()) continue
        check(PREFIX == line.substring(0, PREFIX.length))
        val subStr = line.substring(PREFIX.length).split('=')
        folds.add(Fold(subStr[0], subStr[1].toInt()))
    }
    return Pair(points, folds)
}

fun main() {
    fun printPts(pts: Set<Point>) {
        val maxX = pts.maxByOrNull { it.first }!!.first
        val maxY = pts.maxByOrNull { it.second }!!.second

        for (y in 0..maxY) {
            for (x in 0..maxX) {
                print(if (Pair(x,y) in pts) '#' else ' ')
            }
            println()
        }
    }

    fun part1and2(input:Pair<Set<Point>, List<Fold>>): Long {
        var (currPts, folds) = input
        var part1Answer = 0L
        folds.forEachIndexed { i, fold ->
            val newPts = mutableSetOf<Point>()
            currPts.forEach {
                val pt = when (fold.axis) {
                    "x" -> if (it.first > fold.amt)  Pair(2 * fold.amt - it.first, it.second) else it
                    "y" -> if (it.second > fold.amt)  Pair(it.first, 2 * fold.amt - it.second) else it
                    else -> throw Exception("bad axis")
                }
                newPts.add(pt)
            }
            currPts = newPts
            if (i == 0) part1Answer = newPts.size.toLong()
        }

        println("part 1 answer = $part1Answer")
        printPts(currPts)
        return part1Answer
    }

    val parsed = parseInput(readInput("Day13_test"))
    check(part1and2(parsed) == 17L)

    println("REAL INPUT")
    val parsedInput = parseInput(readInput("Day13"))
    part1and2(parsedInput)
}