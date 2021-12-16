import java.util.*
import kotlin.math.abs

// Day 15, Advent of Code 2021, Chiton

typealias Graph = List<List<Int>>
typealias Cell = Pair<Int, Int>

// Manhattan distance
fun Cell.distance(other: Cell) = abs(this.first - other.first) + abs(this.second - other.second)

fun Graph.neighbors(curr: Cell) : List<Cell> {
    val neighbors = mutableListOf<Cell>()
    for (i in listOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1))) {
        val pt = Pair(curr.first + i.first, curr.second + i.second)
        if (pt.first >= 0 && pt.first < this.size && pt.second >=0 && pt.second < this[0].size) {
            neighbors.add(pt)
        }
    }
    return neighbors
}

// A* search
// https://www.redblobgames.com/pathfinding/a-star/implementation.html
fun aStarSearch(graph: Graph, start: Cell, goal: Cell) : Long {
    val openSet = PriorityQueue {t1: Pair<Cell, Long>, t2 : Pair<Cell, Long> -> (t1.second - t2.second).toInt()}
    openSet.add(Pair(start, 0L))
    val cameFrom = mutableMapOf<Cell, Cell?>()
    val costSoFar = mutableMapOf<Cell, Long>()
    cameFrom[start] = null
    costSoFar[start] = 0L
    while (openSet.isNotEmpty()) {
        val current = openSet.remove()
        if (current.first == goal) break
        val neighbors = graph.neighbors(current.first)
        for (next in neighbors) {
            val newCost = costSoFar[current.first]!! + graph[next.first][next.second]
            if (next !in costSoFar || newCost < costSoFar[next]!!) {
                costSoFar[next] = newCost
                val priority = newCost + next.distance(goal)
                openSet.add(Pair(next, priority))
                cameFrom[next] = current.first
            }
        }
    }
    val end = Pair(graph.size-1, graph[0].size-1)
    return costSoFar[end]!!
}

fun main() {
    fun part1(grid: List<List<Int>>): Long {
        val end = Pair(grid.size-1, grid[0].size-1)
        return aStarSearch(grid, Pair(0, 0), end)
    }

    fun part2(grid: List<List<Int>>): Long {
        val bigGraph = MutableList(grid.size * 5) { MutableList(grid[0].size * 5) { 0 } }
        for (x in bigGraph.indices) {
            for (y in 0 until bigGraph[0].size) {
                var newXVal = grid[x % grid.size][y % grid[0].size]
                var inc = x / grid.size + y / grid[0].size
                newXVal += inc
                bigGraph[x][y] = if (newXVal > 9) newXVal - 9 else newXVal
                check(bigGraph[x][y] in 1..9)
            }
        }
        val end = Pair(bigGraph.size - 1, bigGraph[0].size - 1)
        return aStarSearch(bigGraph, Pair(0, 0), end)
    }

    val testInput = readSingleDigitGrid("Day15_test")
    check(part1(testInput) == 40L)
    check(part2(testInput) == 315L)

    val input = readSingleDigitGrid("Day15")
    println(part1(input))
    println(part2(input))
}