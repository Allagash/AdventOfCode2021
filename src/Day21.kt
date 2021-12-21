// Advent of Code 2021, Day 21, Dirac Dice

import kotlin.math.max

fun main() {

    class Die {
        var value = 1000L
        var numRolls = 0L


        fun getRoll(): Long {
            value++
            numRolls++
            if (value > 100) value = 1
            //println("  roll $numRolls, val is $value")
            return value
        }

        fun lastRoll() = value
    }

    fun part1(input: List<Int>): Long {
        val die = Die()
        val positions = mutableListOf<Int>()
        positions.addAll(input)
        val score = mutableListOf<Int>()
        positions.forEach { score.add(0) }

        while (true) {
            positions.forEachIndexed { idx, it ->
                //println("Player ${idx + 1}")
                var space = it
                val move = die.getRoll() + die.getRoll() + die.getRoll()
                space += move.toInt()
                while (space > 10) {
                    space -= 10
                } // no space zero! just 1..10
                positions[idx] = space
                score[idx] += space
                //println("    Player  ${idx + 1} pos ${positions[idx]}, score ${score[idx]}")
                if (score[idx] >= 1000) {
                    // println("player ${idx + 1} won! scores = $score")
                    val losingScore = if (idx == 0) score[1] else score[0]
                    return losingScore * die.numRolls
                }
            }
            //println("scores = $score")
        }
        return 0L
    }

    fun getAllDiceRolls(): List<Int> {
        val rolls = mutableListOf<Int>()
        for (i in 1..3) {
            for (j in 1..3) {
                for (k in 1..3) {
                    rolls.add(i + j + k)
                }
            }
        }
        rolls.sort() // max use of cache
        return rolls
    }

    data class GameState(val positions: List<Int>, val scores: List<Int>, val turn: Int, val diceScore: Int)

    val cache = mutableMapOf<GameState, List<Long>>()

    fun quantumDiceRoll(state: GameState, diceRolls: List<Int>): List<Long> {
        val value = cache[state]
        if (value != null) return value

        var newPos = state.positions.toMutableList() // need to create new allocated list?
        newPos[state.turn] += state.diceScore
        while (newPos[state.turn] > 10) {
            newPos[state.turn] -= 10
        } // no space zero! just 1..10
        var newScore = state.scores.toMutableList()
        newScore[state.turn] += newPos[state.turn]
        if (newScore[state.turn] >= 21) { // make 21
            return if (state.turn == 0) {
                listOf(1, 0)
            } else {
                listOf(0, 1)
            }
        }
        val finalWins = mutableListOf<Long>(0L, 0L)
        val newTurn = if (state.turn == 0) 1 else 0
        diceRolls.forEach {
            val newState = GameState(newPos, newScore, newTurn, it)
            val wins = quantumDiceRoll(newState, diceRolls)
            for (i in finalWins.indices) {
                finalWins[i] += wins[i]
            }
        }
        cache[state] = finalWins
        return finalWins

        // add dice to player who is up
        // if victory, return 1,0 or 0,1
        // otherwise, update state
        // for each dice roll, recurse
        // get sum
        // update cache
        // return sum
    }


    fun part2(input: List<Int>): Long {
        val positions = mutableListOf<Int>()
        positions.addAll(input)
        val score = mutableListOf<Int>()
        positions.forEach { score.add(0) }

        val diceRolls = getAllDiceRolls()

        val finalWins = mutableListOf<Long>(0L, 0L)
        diceRolls.forEach {
            val state = GameState(positions, score, 0, it)
            val wins = quantumDiceRoll(state, diceRolls)
            for (i in finalWins.indices) {
                finalWins[i] = finalWins[i] + wins[i]
            }
        }
        println("part 2: $finalWins")

        return max(finalWins[0], finalWins[1])
    }

    fun parse(input: List<String>): List<Int> {
        return input.map {
            it.substring("Player 1 starting position:".length).trim().toInt()
        }
    }

    val testInput = parse(readInput("Day21_test"))
    check(part1(testInput) == 739785L)
    println(part2(testInput))
    // check(part2(testInput) == 0L)

    val input = parse(readInput("Day21"))
    // 444356092776315 too low
    println(part1(input))
    println(part2(input))
}