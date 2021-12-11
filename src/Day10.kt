// Day 10, Advent of Code 2021, Syntax Scoring

fun main() {
    val OPEN = listOf('(', '[', '{', '<')
    val CLOSE = listOf(')', ']', '}', '>')
    val PART1_POINTS = listOf(3, 57, 1197, 25137)
    val PART2_POINTS = listOf(1, 2, 3, 4)

    fun part1and2(input: List<String>): Pair<Long, Long> { // Long required for Part2
        var score1 = 0L // Part1
        val scores2 = mutableListOf<Long>() // for Part 2
        input.forEach {
            val stack = mutableStackOf<Int>()
            for (c in it) {
                when {
                    c in OPEN -> stack.push(OPEN.indexOf(c))
                    stack.isEmpty() || CLOSE.indexOf(c) != stack.peek() -> {
                        score1 += PART1_POINTS[CLOSE.indexOf(c)]
                        stack.clear() // signify corrupted
                        break
                    }
                    else -> stack.pop() // matching closing delimiter
                }
            }
            if (stack.isNotEmpty()) {
                var score = 0L // Longs needed
                while (stack.isNotEmpty()) {
                    score = score * 5 + PART2_POINTS[stack.pop()]
                }
                scores2.add(score)
            }
        }
        scores2.sort()
        return Pair(score1, scores2[scores2.size/2])
    }

    val testInput = readInput("Day10_test")
    val testAnswers = part1and2(testInput)
    check(testAnswers.first == 26397L)
    check(testAnswers.second == 288957L)

    val input = readInput("Day10")
    val answers = part1and2(input)
    println(answers.first)
    println(answers.second)
}
