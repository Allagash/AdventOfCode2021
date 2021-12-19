// Advent of Code 2021, Day 18, Snailfish
import kotlin.math.max

enum class Day18Status {
    UPDATE_RIGHT, DONE, NO_CHANGE
}

fun main() {

    data class ReduceState(var prevRightNum: Any?, var explodeRight: Int?, var status: Day18Status?)

    class Node(var value: Int?, var left: Node? = null, var right: Node? = null) {

        init {
            check()
        }

        fun check() {
            check((value == null && left != null && right != null) || (value != null && right == null && left == null))
        }

        fun add(next: Node): Node {
            return Node(null, this, next)
        }

        fun explode(recurseLevel: Int, state: ReduceState): ReduceState {
            if (state.status == Day18Status.NO_CHANGE && recurseLevel > 4 && left?.value != null && right?.value != null) { // explode
                if (state.prevRightNum != null) {
                    val node = state.prevRightNum as Node
                    check(node.value != null && node.left == null && node.right == null)
                    node.value = node.value!! + left!!.value!!
                }
                left = null
                val rightVal = right?.value
                right = null
                value = 0
                return ReduceState(null, rightVal, Day18Status.UPDATE_RIGHT)
            }
            if (value != null) {
                if (state.status == Day18Status.UPDATE_RIGHT) {
                    value = value!! + state.explodeRight!!
                    return ReduceState(this, null, Day18Status.DONE)
                }
                return ReduceState(this, null, Day18Status.NO_CHANGE)
            }
            var result = left!!.explode(recurseLevel + 1, state)
            if (result.status == Day18Status.DONE) {
                return result
            }
            result = right!!.explode(recurseLevel + 1, result)
            if (result.status == Day18Status.DONE) {
                return result
            }
            return result
        }

        fun split(recurseLevel: Int, state: ReduceState): ReduceState {
            if (state.status == Day18Status.NO_CHANGE && value != null && value!! >= 10) { //split
                check(left == null && right == null)
                left = Node(value!! / 2)
                right = Node((value!! + 1) / 2)
                value = null
                return ReduceState(null, null, Day18Status.DONE)
            }
            if (value != null) {
                return ReduceState(this, null, Day18Status.NO_CHANGE)
            }
            var result = left!!.split(recurseLevel + 1, state)
            if (result.status == Day18Status.DONE) {
                return result
            }
            result = right!!.split(recurseLevel + 1, result)
            if (result.status == Day18Status.DONE) {
                return result
            }
            return result
        }

        fun magnitude(): Long {
            if (value != null) return value!!.toLong()
            return 3 * left!!.magnitude() + 2L * right!!.magnitude()
        }

        override fun toString() = value?.toString() ?: ("[" + left.toString() + "," + right.toString() + "]")
    }

    fun parse(input: String, i: Int): Pair<Node, Int> {
        if (input[i] != '[') {
            val end = input.indexOfAny(listOf(",", "]"), i)
            val num = input.substring(i, end).toInt()
            return Pair(Node(num), end)
        }
        val (leftNode, commaIdx) = parse(input, i + 1)
        check(input[commaIdx] == ',')
        val (rightNode, endIdx) = parse(input, commaIdx + 1)
        return Pair(Node(null, leftNode, rightNode), endIdx + 1)
    }

    fun reduce(sum: Node) {
        do {
            do {
                val state = sum.explode(1, ReduceState(null, null, Day18Status.NO_CHANGE))
            } while (state.status != Day18Status.NO_CHANGE)
            val state = sum.split(1, ReduceState(null, null, Day18Status.NO_CHANGE))
        } while (state.status != Day18Status.NO_CHANGE)
    }

    fun part1(input: List<String>): Long {
        var sum = parse(input[0], 0).first
        for (i in 1 until input.size) {
            val nodeNext = parse(input[i], 0).first
            sum = sum.add(nodeNext)
            reduce(sum)
        }
        return sum.magnitude()
    }

    fun part2(input: List<String>): Long {
        val nodes = mutableListOf<Node>()
        input.forEach {
            nodes.add(parse(it, 0).first)
        }
        var largestSum = Long.MIN_VALUE
        for (i in nodes.indices) {
            for (j in i + 1 until nodes.size) {
                val sumij = nodes[i].add(nodes[j])
                reduce(sumij) // this screws up the nodes, so have to reparse them
                val mag1 = sumij.magnitude()
                largestSum = max(largestSum, mag1)
                // make more functional!
                nodes[i] = parse(input[i], 0).first
                nodes[j] = parse(input[j], 0).first
                val sumji = nodes[j].add(nodes[i])
                reduce(sumji)
                val mag2 = sumji.magnitude()
                largestSum = max(largestSum, mag2)
                // make more functional!
                nodes[i] = parse(input[i], 0).first
                nodes[j] = parse(input[j], 0).first
            }
        }

        return largestSum
    }

    val testInput = readInput("Day18_test")
    check(part1(testInput) == 4140L)
    check(part2(testInput) == 3993L)

    val input = readInput("Day18")
    println(part1(input))
    println(part2(input))
}