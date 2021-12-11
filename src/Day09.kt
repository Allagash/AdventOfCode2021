// Day 9, Advent of Code 2021, Smoke Basin

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readSingleDigitGrid("Day09_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 1134)

    val input = readSingleDigitGrid("Day09")
    println(part1(input))
    println(part2(input))
}

fun List<List<Int>>.isBasin(x: Int, y: Int) : Boolean {
    return ((y == 0 || this[x][y-1] > this[x][y]) &&
            (y == this[0].size-1 || this[x][y] < this[x][y + 1]) &&
            (x == 0 || this[x-1][y] > this[x][y]) &&
            (x == (this.size-1) || this[x+1][y] > this[x][y]))
}

private fun part1(heights: List<List<Int>>): Int {
    return heights.foldIndexed(0) { i, sum, it ->
        sum + it.foldIndexed(0) { j, sum2, jit ->
            sum2 + if (heights.isBasin(i, j)) jit + 1 else 0
        }
    }
}

private fun basinSize( heights: List<List<Int>>, x: Int, y: Int, prevVisited: MutableSet<Pair<Int, Int>>) : Int {
    if (x < 0 || x > heights.size -1 || y < 0 || y > heights[0].size-1) return 0 // off the map
    if (heights[x][y] >= 9) return 0 // border
    if (prevVisited.contains(Pair(x, y))) return 0 // visited
    prevVisited.add(Pair(x,y))

    var sum = 1 // current position
    sum += basinSize(heights, x+1, y, prevVisited)
    sum += basinSize(heights, x-1, y, prevVisited)
    sum += basinSize(heights, x, y+1, prevVisited)
    sum += basinSize(heights, x, y-1, prevVisited)
    return sum
}

private fun part2(heights: List<List<Int>>): Int {
    val sizes = mutableListOf<Int>()
    heights.forEachIndexed { i, arr ->
        arr.forEachIndexed { j, _ ->
            if (heights.isBasin(i, j)){
                sizes.add(basinSize(heights, i, j, mutableSetOf()))
            }
        }
    }
    sizes.sortDescending()
    return sizes[0] * sizes[1] * sizes[2]
}
