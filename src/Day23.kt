// Day 23, Advent of Code 2021, Amphipod

import java.util.PriorityQueue
import kotlin.math.abs
import kotlin.system.measureTimeMillis

typealias Positions23 = Map<Pair<Int, Int>, Char>

// Static data for each Amphipod
data class AmphipodMetadata(val cost: Int, val roomEntrance: Pair<Int, Int>, val roomTop: Pair<Int, Int>, var roomBottom: Pair<Int, Int>)

// Set up for Part 1
val amphipodTypes = mapOf(
    'A' to AmphipodMetadata(1, Pair(1, 3),  Pair(2, 3), Pair(3, 3)),
    'B' to AmphipodMetadata(10, Pair(1, 5), Pair(2, 5), Pair(3, 5)),
    'C' to AmphipodMetadata(100, Pair(1, 7), Pair(2, 7), Pair(3, 7)),
    'D' to AmphipodMetadata(1000, Pair(1, 9), Pair(2, 9), Pair(3, 9))
)

fun printToString(pos: Positions23) : String {
    var output = "#############\n#"
    for (i in 1..11) { // hallway
        output += pos[Pair(1, i)] ?: "."
    }
    output += "#\n"
    val roomCols = listOf(3, 5, 7, 9)
    for (i in 0..12) { // top of room
        output += pos[Pair(2, i)] ?: if (i in roomCols) "." else "#"
    }
    output += "\n"
    val numLoops = if (pos.size == 8) 1 else 3
    for (i in 0 until numLoops) {
        output += "  "
        for (y in 2..10) {
            output += pos[Pair(3 + i, y)] ?:  if (y in roomCols) "." else "#"
        }
        output += "\n"
    }
    output += "  #########"
    return output
}

// Manhattan distance
fun Pair<Int, Int>.manhattanDist(other: Pair<Int, Int>) = abs(this.first - other.first) + abs(this.second - other.second)

// Moves from hall into room.
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
    var ok = true
    for (x in (roomTop.first + 1)..roomBottom.first) {
        val roomPos = Pair(x, roomTop.second)
        if (this[roomPos] != null && this[roomBottom] != amphipod){
            ok = false
            break
        }
    }
    if (!ok) return moves // can't move into room w/ wrong amphipod

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

    for (x in (roomTop.first + 1)..roomBottom.first) {
        val roomPos = Pair(x, roomTop.second)
        // if bottom is empty, add that move & cost
        if (this[roomPos] != null) break
        clone = HashMap(this)
        clone.remove(pos)
        clone[roomPos] = amphipod
        moves.add(Pair(clone, (distance + (x - roomTop.first)) * amphipodData.cost))
    }
    return moves
}


fun Positions23.generateRoomMovesTop(pos: Pair<Int, Int>, amphipod: Char) : List<Pair<Positions23, Int>> {
    val moves = mutableListOf<Pair<Positions23, Int>>()

    val amphipodData = amphipodTypes[amphipod]!!

    val roomTop = amphipodData.roomTop
    val roomBottom = amphipodData.roomBottom

    // If we're in the right spot & spots below us are correct, don't generate moves
    if (roomTop == pos) { // && this[roomBottom] ==
        var ok = true
        for (x in (roomTop.first + 1)..roomBottom.first) {
            val roomPos = Pair(x, roomTop.second)
            if (this[roomPos] != amphipod) {
                ok = false
                break
            }
        }
        if (ok) {
            return moves
        }
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
    if (roomBottom.second == pos.second) {
        var ok = true
        for (x in pos.first..roomBottom.first) {
            val roomPos = Pair(x, roomBottom.second)
            if (this[roomPos] != amphipod) {
                ok = false
                break
            }
        }
        if (ok) {
            return moves
        }
    }

    // shouldn't have to loop here, no gaps in room
    // don't generate moves if blocked on top
    if (this[Pair(pos.first-1, pos.second)] != null) {
        return moves
    }

    val clone = HashMap(this)
    clone.remove(pos)
    clone[Pair(pos.first -1, pos.second)] = amphipod

    // Same moves as top of room, plus one more move
    val distToTop = pos.first - amphipodData.roomTop.first
    val newMoves = clone.generateRoomMovesTop(Pair(pos.first - distToTop, pos.second), amphipod)
    newMoves.forEach {
        moves.add(Pair(it.first, it.second + distToTop * amphipodData.cost)) // add the cost of moving up to top
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
                else -> generateRoomMovesBottom(it, this[it]!!)
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
    // Will we have to move an amphipod down to the bottom spots eventually?
    amphipodTypes.forEach {
        for (x in (it.value.roomTop.first + 1)..it.value.roomBottom.first) {
            if (this[Pair(x, it.value.roomTop.second)] != it.key) {
                cost += it.value.cost
            }
        }
    }
    return cost
}


fun main() {

    fun isDone(position: Positions23): Boolean {
        check(position.size == 8 || position.size == 16)
        val part1 = position[Pair(2, 3)] == 'A' && position[Pair(3, 3)] == 'A' &&
                position[Pair(2, 5)] == 'B' && position[Pair(3, 5)] == 'B' &&
                position[Pair(2, 7)] == 'C' && position[Pair(3, 7)] == 'C' &&
                position[Pair(2, 9)] == 'D' && position[Pair(3, 9)] == 'D'
        if (position.size == 8 || !part1) return part1 // if part1 false, we don't have to check part2
        return position[Pair(4, 3)] == 'A' && position[Pair(5, 3)] == 'A' &&
                position[Pair(4, 5)] == 'B' && position[Pair(5, 5)] == 'B' &&
                position[Pair(4, 7)] == 'C' && position[Pair(5, 7)] == 'C' &&
                position[Pair(4, 9)] == 'D' && position[Pair(5, 9)] == 'D'
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
        var iter = 0
        while (openSet.isNotEmpty()) {
            val current = openSet.remove()
            iter++
            //println("Trying $iter")
            //println(printToString(current.first))
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

    fun parse(input: List<String>, part2: Boolean = false) : Positions23 {
        val input2 = input.toMutableList()
        if (part2) {
            input2.add(3, "  #D#C#B#A#")
            input2.add(4, "  #D#B#A#C#")
        }
        val positions = mutableMapOf<Pair<Int, Int>, Char>()
        val chars = listOf("A", "B", "C", "D")
        input2.forEachIndexed { idx, it ->
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
        for (c in 'A'..'D') {
            val bottom = amphipodTypes[c]!!.roomBottom
            amphipodTypes[c]!!.roomBottom = Pair(bottom.first + 2, bottom.second)
        }
        var cost: Long
        val timeInMillis = measureTimeMillis {
            cost = aStarSearch(positions)
        }
        println("part1 time is $timeInMillis")
        return cost
    }

    val testInput1 = parse(readInput("Day23_test"))
    check(part1(testInput1) == 12521L)
    val testInput2 = parse(readInput("Day23_test"), true)
    //println(part2(testInput2))

    val input1 = parse(readInput("Day23"))
    println(part1(input1))
    val input2 = parse(readInput("Day23"), true)
}