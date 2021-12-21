// Advent of Code 2021, Day 20, Trench Map

fun main() {

    data class Map(val key: String, val grid: List<String>) {

        override fun toString(): String {
            var str = key + "\n\n"
            grid.forEach {
                it.forEach {
                    str += when (it) {
                        '0' -> '.'
                        '1' -> '#'
                        else -> it
                    }
                }
                str += '\n'
            }
            return str
        }
    }

    fun printMap(map1: Array<Array<Char>>) {
        for (element in map1) {
            for (x in 0 until map1[0].size) {
                val c = element[x]
                print(
                    when (c) {
                        '0' -> '.'
                        '1' -> '#'
                        else -> c
                    }
                )
            }
            println()
        }
    }

    fun count(map1: Array<Array<Char>>): Long {
        var sum = 0L
        for (element in map1) {
            for (x in 0 until map1[0].size) {
                if (element[x] == '1') sum++
            }
        }
        return sum
    }

    fun enhance(map1: Array<Array<Char>>, key: String, map2: Array<Array<Char>>, shrink: Int = 0) {
        for (y in shrink..map1.size - 3 - shrink) {
            for (x in shrink..map1[y].size - 3 - shrink) {
                val str = String(map1[y + 0].slice(x..x + 2).toCharArray()) +
                        String(map1[y + 1].slice(x..x + 2).toCharArray()) +
                        String(map1[y + 2].slice(x..x + 2).toCharArray())
                val num = str.toInt(2)
                map2[y + 1 - shrink][x + 1 - shrink] = key[num]
            }
        }
    }

    fun imageEnhance(map: Map, num: Int): Long {
        // use windowed!
        var map1 = Array(map.grid.size) { Array(map.grid[0].length) { '0' } }

        // copy
        for (y in map.grid.indices) {
            val line = map.grid[y]
            for (x in line.indices) {
                map1[y][x] = line[x]
            }
        }

        for (i in 0 until num) {
            val map2 = Array(map1.size - 2) { Array(map1.size - 2) { '0' } }
            enhance(map1, map.key, map2, 1)
            map1 = map2
        }
        return count(map1)
    }

    fun parse(input: List<String>): Map {
        val newKey = input[0].map {
            when (it) {
                '.' -> '0'
                '#' -> '1'
                else -> it
            }
        }
        val key = String(newKey.toCharArray())
        val map = mutableListOf<String>()

        val padAmt = 101 // 100 doesn't work for 50x
        val padRow = "0".repeat(input[2].length + 2 * padAmt)
        for (i in 0 until padAmt) { // top padding
            map.add(padRow)
        }

        for (i in 2 until input.size) {
            val line = String(input[i].map { if (it == '.') '0' else '1' }.toCharArray())
            val pad = "0".repeat(padAmt)
            val newLine = pad + line + pad // pad the sides
            map.add(newLine)
        }
        for (i in 0 until padAmt) { // bottom padding
            map.add(padRow)
        }

        return Map(key, map)
    }

    val testInput = parse(readInput("Day20_test"))
    check(imageEnhance(testInput, 2) == 35L)
    check(imageEnhance(testInput, 50) == 3351L)

    val input = parse(readInput("Day20"))
    println(imageEnhance(input, 2))
    println(imageEnhance(input, 50))
}