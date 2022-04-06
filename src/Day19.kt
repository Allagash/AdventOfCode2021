// Advent of Code 2021, Day 19, Beacon Scanner

import kotlin.math.abs
import kotlin.math.max
import kotlin.system.measureTimeMillis

// See
// https://github.com/xorum-io/advent-of-code-2021/blob/main/src/Day19.kt
// from https://raw.githubusercontent.com/Jbrusaw/AdventOfCode2021/main/src/day19.kt

fun main() {

    // aka Beacon
    data class Point(val x: Int, val y: Int, val z: Int) {
        operator fun plus(other: Point): Point {
            return Point(x + other.x, y + other.y, z + other.z)
        }

        operator fun minus(other: Point): Point {
            return Point(x - other.x, y - other.y, z - other.z)
        }

        fun manhattanDistance() = abs(x) + abs(y) + abs(z)
    }

    data class Scanner(val id: Int, var readings: List<Point>) {
        var done = false
        var origin = Point(0, 0, 0)
        val alreadyTested = mutableListOf<Scanner>()

        fun rotate(i: Int) : List<Point> {
            check(i in 0..23)
            return when (i) {
                // from https://www.wolframalpha.com/input/?i=yaw%3D180+degrees%2C+pitch%3D90+degrees
                0 ->  readings.map{ Point( it.x,  it.y,  it.z)} // same thing
                1 ->  readings.map{ Point( it.x,  it.z, -it.y)} // roll 90
                2 ->  readings.map{ Point( it.x, -it.y, -it.z)} // roll 180
                3 ->  readings.map{ Point( it.x, -it.z,  it.y)} // roll 270

                4 ->  readings.map{ Point( it.y, -it.x,  it.z)} // yaw 90
                5 ->  readings.map{ Point( it.z, -it.x, -it.y)} // yaw 90 roll 90
                6 ->  readings.map{ Point(-it.y, -it.x, -it.z)}
                7 ->  readings.map{ Point(-it.z, -it.x,  it.y)}

                8 ->  readings.map{ Point(-it.x, -it.y,  it.z)} // yaw 180, roll 0
                9 ->  readings.map{ Point(-it.x, -it.z, -it.y)}
                10 -> readings.map{ Point(-it.x,  it.y, -it.z)}
                11 -> readings.map{ Point(-it.x,  it.z,  it.y)}

                12 -> readings.map{ Point(-it.y,  it.x,  it.z)} // yaw 270, pitch 0
                13 -> readings.map{ Point(-it.z,  it.x, -it.y)}
                14 -> readings.map{ Point( it.y,  it.x, -it.z)}
                15 -> readings.map{ Point( it.z,  it.x,  it.y)}

                16 -> readings.map{ Point( it.z,  it.y, -it.x)} // pitch 90, roll 0
                17 -> readings.map{ Point(-it.y,  it.z, -it.x)} // pitch 90, roll 90
                18 -> readings.map{ Point(-it.z, -it.y, -it.x)}
                19 -> readings.map{ Point( it.y, -it.z, -it.x)}

                20 -> readings.map{ Point(-it.z,  it.y,  it.x)} // pitch -90, roll 0
                21 -> readings.map{ Point( it.y,  it.z,  it.x)} // pitch -90, roll 90
                22 -> readings.map{ Point( it.z, -it.y,  it.x)}
                23 -> readings.map{ Point(-it.y, -it.z,  it.x)}
                else -> throw Exception("$i out of range for rotate")
            }
        }

        fun rotateAndCheck(scanner: Scanner) : Boolean {
            if (scanner.done || alreadyTested.contains(scanner)) {
                return false
            }
            alreadyTested.add(scanner)
            for(i in 0..23) {
                val rotatedPts = scanner.rotate(i)
                rotatedPts.forEach { r ->
                    readings.forEach { orig ->
                        val newOrigin = orig - r
                        val count = rotatedPts.map { it + newOrigin }.count{ readings.contains(it)}
                        if (count >= 12) {
                            scanner.done = true
                            scanner.readings = rotatedPts.map { it + newOrigin }
                            scanner.origin = newOrigin
                            return true
                        }
                    }
                }
            }
            return false
        }
    }

    fun manhattanDistance(scanner1 : Scanner, scanner2: Scanner) =
        (scanner1.origin - scanner2.origin).manhattanDistance()


    fun solve(input: List<Scanner>): Pair<Int, Int> {
        val located = mutableListOf<Scanner>()
        val todo = input.toMutableList()
        located.add(input[0])
        todo.remove(input[0])

        while(todo.isNotEmpty()) {
            val done = mutableListOf<Scanner>()
            located.forEach { l ->
                todo.forEach { t ->
                    if (l.rotateAndCheck(t)) {
                        done.add(t)
                    }
                }
            }
            check(done.isNotEmpty())
            located.addAll(done)
            todo.removeAll(done)
        }
        var maxDistance = 0
        located.forEachIndexed { i, it ->
            for (j in i+1 .. located.lastIndex) {
                maxDistance = max(maxDistance, manhattanDistance(it, located[j]))
            }
        }
        val uniqueBeacons = located.map {it.readings}.flatten().distinct().size
        return Pair(uniqueBeacons, maxDistance)
    }

    fun parse(input: List<String>): List<Scanner> {
        val scanners = mutableListOf<Scanner>()
        val pts = mutableListOf<Point>()
        var id = -1
        input.forEach {
            val beginScanner = it.contains("--- scanner ")
            if (it.trim().isEmpty() || beginScanner) {
                if (beginScanner) {
                    id = it.split("--- scanner ")[1].split(" ---")[0].toInt()
                }
                if (pts.isNotEmpty()) {
                    scanners.add(Scanner(id, pts.toList())) // use toList() to get copy list of points
                    pts.clear()
                }
                return@forEach // like a continue
            }
            val (x, y, z) = it.trim().split(',').map { it.toInt() }
            pts.add(Point(x, y, z))
        }
        if (pts.isNotEmpty()) {
            scanners.add(Scanner(id, pts.toList())) // use toList() to get copy list of points
        }
        return scanners
    }

    val testResults = solve(parse(readInput("Day19_test")))
    check(testResults.first == 79)
    check(testResults.second == 3621)

    var solutions : Pair<Int, Int>
    val timeMs = measureTimeMillis{
        solutions = solve(parse(readInput("Day19")))
    }
    println("Part 1: ${solutions.first}, Part 2: ${solutions.second}, took $timeMs ms")
}