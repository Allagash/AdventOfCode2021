// Day 11, Advent of Code 2021, Dumbo Octopus

import kotlin.math.*

class OctoMap(initialMap: List<List<Int>>) {
    private var lightLevel = mutableListOf(mutableListOf<Int>())
    var flashes = 0

    init {
        lightLevel.clear()
        initialMap.forEach {
            lightLevel.add(it.toMutableList())
        }
    }

    // https://stackoverflow.com/questions/40463390/how-to-copy-a-two-dimensional-array-in-kotlin
    private fun MutableList<MutableList<Int>>.copy() = map { ArrayList<Int>(it) }

    fun step() {
        addOne()
        do { // keep looping until all flashes propogated
            val newFlashes = calcFlashes()
            flashes += newFlashes
        } while (newFlashes > 0)
        clamp()
    }

    private fun addOne() {
        lightLevel.forEachIndexed { i, it ->
            it.forEachIndexed { j, _ ->
                lightLevel[i][j]++
            }
        }
    }

    // When it flashes, we store value as -LARGE_NUM.
    // Clamp this back to 0
    private fun clamp() {
        lightLevel.forEachIndexed { i, it ->
            it.forEachIndexed { j, _ ->
                lightLevel[i][j] = max(0, lightLevel[i][j])
                check(lightLevel[i][j] <= 9)
            }
        }
    }

    fun allFlashed() : Boolean{
        lightLevel.forEachIndexed { i, it ->
            it.forEachIndexed { j, _ ->
                if (lightLevel[i][j] != 0) return false
            }
        }
        return true
    }

    override fun toString(): String {
        var output = ""
        lightLevel.forEachIndexed { i, it ->
            it.forEachIndexed { j, _ ->
                output += lightLevel[i][j]
            }
            output += '\n'
        }
        return output
    }

    private fun calcFlashes() : Int {
        var newFlashes = 0
        val dirs = listOf(
            Pair(-1, -1), Pair(-1, 0), Pair(-1, 1),
            Pair(0, -1), Pair(0, 0), Pair(0, 1),
            Pair(1, -1), Pair(1, 0), Pair(1, 1)
        )
        val prevVersion = lightLevel.copy()
        lightLevel.forEachIndexed { i, it ->
            it.forEachIndexed { j, _ ->
                if (prevVersion[i][j] > 9) {
                    // flashed. This way we can increment it but still tell
                    // it's been flashed and should be set to 0 later.
                    lightLevel[i][j] = -100000
                    newFlashes++
                }
                dirs.forEach { d ->
                    val (x, y) = Pair(d.first + i, d.second + j)
                    if (x < 0 || x >= lightLevel.size ||y < 0 || y >= it.size) return@forEach
                    if (prevVersion[x][y] > 9) {
                        lightLevel[i][j]++
                    }
                }
            }
        }
        return newFlashes
    }
}

fun main() {
    fun part1(map: OctoMap, steps: Int): Int {
        for (i in 1..steps) {
            map.step()
        }
        return map.flashes
    }

    fun part2(map: OctoMap): Long {
        var step = 0L
        do {
            step++
            map.step()
        } while (!map.allFlashed())
        return step
    }

    check(part1(OctoMap(readSingleDigitGrid("Day11_testa")), 2) == 9)

    check(part1(OctoMap(readSingleDigitGrid("Day11_test")), 100) == 1656)
    check(part2(OctoMap(readSingleDigitGrid("Day11_test"))) == 195L)

    println(part1(OctoMap(readSingleDigitGrid("Day11")), 100))
    println(part2(OctoMap(readSingleDigitGrid("Day11"))))
}
