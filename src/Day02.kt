// Day 2, Advent of Code 2021, Dive!
fun main() {
    fun part1(input: List<Vector>): Int {
        var forward = 0
        var depth = 0

        input.forEach {
            when (it.direction) {
                "forward" -> forward += it.amount
                "down" -> depth += it.amount
                "up" -> depth -= it.amount
                else ->  throw IllegalArgumentException("Name ${it.direction}")
            }
        }
        return forward * depth
    }

    fun part2(input: List<Vector>): Int {
        var forward = 0
        var depth = 0
        var aim = 0

        input.forEach {
            when (it.direction) {
                "forward" -> {
                    val forwardAmt = it.amount
                    forward += forwardAmt
                    depth += aim * forwardAmt
                }
                "down" -> aim += it.amount
                "up" -> aim -= it.amount
                else -> throw IllegalArgumentException("Name ${it.direction}")
            }
        }
        return forward * depth
    }

    // test if implementation meets criteria from the description, like:
    var vectors = readInputsAsVectors("Day02_test")

    check(part1(vectors) == 150)
    check(part2(vectors) == 900)

    vectors = readInputsAsVectors("Day02")
    println(part1(vectors))
    println(part2(vectors))
}
