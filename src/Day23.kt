// Day 23, Advent of Code 2021, Amphipod

import java.util.PriorityQueue
import kotlin.math.abs
import kotlin.system.measureTimeMillis

typealias Positions23 = Map<Pair<Int, Int>, Char>

// Static data for each Amphipod
data class AmphipodMetadata(val cost: Int, val roomEntrance: Pair<Int, Int>, val roomTop: Pair<Int, Int>, val roomBottom: Pair<Int, Int>)

val amphipodTypes = mapOf(
    'A' to AmphipodMetadata(1, Pair(1, 3),  Pair(2, 3), Pair(3, 3)),
    'B' to AmphipodMetadata(10, Pair(1, 5), Pair(2, 5), Pair(3, 5)),
    'C' to AmphipodMetadata(100, Pair(1, 7), Pair(2, 7), Pair(3, 7)),
    'D' to AmphipodMetadata(1000, Pair(1, 9), Pair(2, 9), Pair(3, 9))
)

fun printToString(pos: Positions23) : String {
    var output = "#############\n#"
    for (i in 1..11) {
        output += pos[Pair(1, i)] ?: "."
    }
    output += "#\n"
    val roomCols = listOf(3, 5, 7, 9)
    for (i in 0..12) {
        output += pos[Pair(2, i)] ?: if (i in roomCols) "." else "#"
    }
    output += "\n  "
    for (i in 2..10) {
        output += pos[Pair(3, i)] ?:  if (i in roomCols) "." else "#"
    }
    output += "\n  #########"
    return output
}

// Manhattan distance
fun Pair<Int, Int>.manhattanDist(other: Pair<Int, Int>) = abs(this.first - other.first) + abs(this.second - other.second)

fun Positions23.generateHallMoves(pos: Pair<Int, Int>, amphipod: Char) : List<Pair<Positions23, Int>> {
    val moves = mutableListOf<Pair<Positions23, Int>>()
    // is room open?
    // - is the first spot open?
    // - is the second spot the same char or empty?
    // - are all spots open in hall up to it?
    val amphipodData = amphipodTypes[amphipod]!!
    val roomTop = amphipodData.roomTop
    val roomBottom = amphipodData.roomBottom
    if (this[roomTop] != null) return moves // Room full, can't move into room
    if (this[roomBottom] != null && this[roomBottom] != amphipod) return moves // can't move into room w/ wrong amphipod

    val step = if (roomTop.second >  pos.second) 1 else -1
    // we know it's not right outside the room; that's not allowed
    var startPos = pos.second
    while (startPos != roomTop.second) {
        startPos += step
        if (this[Pair(pos.first, startPos)] != null) {
            return moves
        }
    }
    var clone = HashMap(this)
    clone.remove(pos)
    clone[roomTop] = amphipod

    // get manhattan distance to top of room
    val distance = pos.manhattanDist(roomTop)
    moves.add(Pair(clone, distance * amphipodData.cost))

    // if bottom is empty, add that move & cost
    if (this[roomBottom] == null) {
        clone = HashMap(this)
        clone.remove(pos)
        clone[roomBottom] = amphipod
        moves.add(Pair(clone, (distance + 1) * amphipodData.cost))
    }
    return moves
}


fun Positions23.generateRoomMovesTop(pos: Pair<Int, Int>, amphipod: Char) : List<Pair<Positions23, Int>> {
    val moves = mutableListOf<Pair<Positions23, Int>>()

    val amphipodData = amphipodTypes[amphipod]!!

    val roomTop = amphipodData.roomTop
    val roomBottom = amphipodData.roomBottom

    // If we're in the right spot & spot below us is correct, don't generate moves
    if (roomTop == pos && this[roomBottom] == amphipod) {
        return moves
    }

    val startPos = Pair(pos.first-1, pos.second)
    check(pos.first-1 == 1)
    check(this[startPos] == null) // spot above room is always empty by puzzle def'n

    var yPos = pos.second + 1
    val roomCols = listOf(3, 5, 7, 9) // columns for each room

    while(yPos <= 11 && this[Pair(1, yPos)] == null) {
        if (yPos in roomCols) {yPos++; continue} // can't stop outside room
        val clone = HashMap(this)
        clone.remove(pos)
        clone[Pair(1, yPos)] = amphipod
        moves.add(Pair(clone, pos.manhattanDist(Pair(1, yPos)) * amphipodData.cost))
        yPos++
    }

    yPos = pos.second - 1

    // go left, for each that's empty, add move
    while(yPos > 0 && this[Pair(1, yPos)] == null) {
        if (yPos in roomCols) {yPos--; continue} // can't stop outside room
        val clone = HashMap(this)
        clone.remove(pos)
        clone[Pair(1, yPos)] = amphipod
        moves.add(Pair(clone, pos.manhattanDist(Pair(1, yPos)) * amphipodData.cost))
        yPos--
    }

    return moves
}

fun Positions23.generateRoomMovesBottom(pos: Pair<Int, Int>, amphipod: Char) : List<Pair<Positions23, Int>> {
    val moves = mutableListOf<Pair<Positions23, Int>>()

    val amphipodData = amphipodTypes[amphipod]!!
    val roomBottom = amphipodData.roomBottom

    // If we're in the right spot, don't generate moves
    if (roomBottom == pos) {
        return moves
    }

    // don't generate moves if blocked on top
    if (this[Pair(pos.first-1, pos.second)] != null) {
        return moves
    }

    val clone = HashMap(this)
    clone.remove(pos)
    clone[Pair(pos.first -1, pos.second)] = amphipod

    // Same moves as top of room, plus one more move
    val newMoves = clone.generateRoomMovesTop(Pair(pos.first -1, pos.second), amphipod)
    newMoves.forEach {
        moves.add(Pair(it.first, it.second + amphipodData.cost)) // add the cost of moving up one
    }
    return moves
}

// Return next moves & cost for each
fun Positions23.generateMoves(): List<Pair<Positions23, Int>> {
    val moves = mutableListOf<Pair<Positions23, Int>>()

    val positions = this.keys
    positions.forEach {
        moves.addAll(
            when (it.first) {
                1 -> generateHallMoves(it, this[it]!!)
                2 -> generateRoomMovesTop(it, this[it]!!)
                3 -> generateRoomMovesBottom(it, this[it]!!)
                else -> throw Exception("bad position")
            }
        )
    }
    return moves
}

// Calc cost for A* - costs at least this much
fun Positions23.calcHeuristicCost() : Long {
    var cost = 0L
    this.forEach {
        val amphipodData = amphipodTypes[it.value]!!
        val dist = when {
            // not in the room, so calc distance to spot above the room & down one. That's the minimum cost, not the max.
            it.key.second != amphipodData.roomTop.second -> it.key.manhattanDist(amphipodData.roomEntrance) + 1
            it.key == amphipodData.roomBottom -> 0 // already in place
            it.key == amphipodData.roomTop && this[amphipodData.roomBottom] == it.value -> 0 // already in place
            // In room, but will have to eventually move out of way & back
            else -> 2 * (1 +  it.key.manhattanDist(amphipodData.roomEntrance)) // Can't just move to entrance given puzzle rules, so add 1 since moving over by at least 1.
        }
        cost += amphipodData.cost * dist
    }
    // Will we have to move an amphipod down to the bottom spot eventually?
    amphipodTypes.forEach {
        if (this[it.value.roomBottom] != it.key) {
            cost += it.value.cost
        }
    }
    return cost
}


fun main() {

    fun isDone(position: Positions23): Boolean {
        check(position.size == 8)
        return position[Pair(2, 3)] == 'A' && position[Pair(3, 3)] == 'A' &&
                position[Pair(2, 5)] == 'B' && position[Pair(3, 5)] == 'B' &&
                position[Pair(2, 7)] == 'C' && position[Pair(3, 7)] == 'C' &&
                position[Pair(2, 9)] == 'D' && position[Pair(3, 9)] == 'D'
    }

    // A* search
    // https://www.redblobgames.com/pathfinding/a-star/implementation.html
    fun aStarSearch(position: Positions23) : Long {
        val openSet = PriorityQueue {t1: Pair<Positions23, Int>, t2 : Pair<Positions23, Int> -> (t1.second - t2.second)}
        openSet.add(Pair(position, 0))
        val cameFrom = mutableMapOf<Positions23, Positions23?>()
        val costSoFar = mutableMapOf<Positions23, Long>()
        cameFrom[position] = null
        costSoFar[position] = 0L
        var end : Positions23? = null
        while (openSet.isNotEmpty()) {
            val current = openSet.remove()
            if (isDone(current.first)) {
                end = current.first
                break
            }
            val moves = current.first.generateMoves()
            for (next in moves) {
                val newCost = costSoFar[current.first]!! + next.second
                if (next.first !in costSoFar || newCost < costSoFar[next.first]!!) {
                    costSoFar[next.first] = newCost
                    val priority = newCost + next.first.calcHeuristicCost()
                    openSet.add(Pair(next.first, priority.toInt()))
                    cameFrom[next.first] = current.first
                }
            }
        }
        return costSoFar[end]!!
    }

    fun parse(input: List<String>) : Positions23 {
        val positions = mutableMapOf<Pair<Int, Int>, Char>()
        val chars = listOf("A", "B", "C", "D")
        input.forEachIndexed { idx, it ->
            var startIdx = -1
            while(true) {
                val found = it.findAnyOf(chars, startIdx + 1) ?: return@forEachIndexed
                startIdx = found.first
                positions[Pair(idx, startIdx)] = found.second[0]
            }
        }
        return positions
    }

    fun part1(positions: Positions23): Long {
        var cost: Long
        val timeInMillis = measureTimeMillis {
            cost = aStarSearch(positions)
        }
        println("part1 time is $timeInMillis")
        return cost
    }

    fun part2(positions: Positions23): Long {
        return 0L
    }

    val testInput = parse(readInput("Day23_test"))
    check(part1(testInput) == 12521L)

    val input = parse(readInput("Day23"))
    println(part1(input))
}