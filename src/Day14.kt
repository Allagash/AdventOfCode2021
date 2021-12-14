// Day 14, Advent of Code 2021, Extended Polymerization

typealias StepMap = Map<String, Char>
typealias CharCount = MutableMap<Char, Long>
typealias Cache = MutableMap<Pair<String, Int>, CharCount>

// Add all keys, values from second CharCount to this one. Values will be added together if they share a key.
fun  CharCount.add(other: CharCount) = other.map{this[it.key] = (this[it.key]?: 0) + it.value}

data class Polymer(val start: String, val steps: StepMap)

fun getCount(bounds: String, numSteps: Int, polymer: Polymer, cache: Cache) : CharCount {
    check(numSteps >= 1)
    check(bounds.length == 2)
    val currCache = cache[Pair(bounds, numSteps)]
    if (currCache != null) return currCache

    val charCount = mutableMapOf<Char, Long>()
    val c = polymer.steps[bounds]!!
    charCount[c] = (charCount[c] ?: 0) + 1
    if (numSteps > 1) { // if numSteps is 1, we don't recurse, just add generated char to cache
        charCount.add(getCount(bounds.substring(0, 1) + c, numSteps - 1, polymer, cache))
        charCount.add(getCount(c + bounds.substring(1), numSteps - 1, polymer, cache))
    }
    cache[Pair(bounds, numSteps)] = charCount
    return charCount
}

fun main() {
    fun calcPolymer(polymer: Polymer, numSteps: Int): Long {
        val charCount = mutableMapOf<Char, Long>()
        val cache = mutableMapOf<Pair<String, Int>, CharCount>()
        // Count characters in initial string
        polymer.start.forEach { charCount[it] = (charCount[it] ?: 0) + 1 }
        for (i in 0 until polymer.start.length-1) {
            charCount.add(getCount(polymer.start.substring(i, i+2), numSteps, polymer, cache ))
        }
        val max = charCount.maxByOrNull { it.value }!!.value
        val min = charCount.minByOrNull { it.value }!!.value
        return max - min
    }

    fun parseInput(input: List<String>) : Polymer {
        val start = input[0].trim()
        val steps = mutableMapOf<String, Char>()

        for (i in 1 until input.size) {
            val line = input[i].trim()
            if (line.isEmpty()) continue
            val step = line.split(" -> ")
            steps[step[0]] = step[1][0]
        }
        return Polymer(start, steps)
    }

    val testInput = readInput("Day14_test")
    val parsed = parseInput(testInput)
    check(calcPolymer(parsed, 1) == 1L) // NCNBCHB
    check(calcPolymer(parsed, 2) == 5L) // NBCCNBBBCBHCB
    check(calcPolymer(parsed, 3) == 7L) // NBBBCNCCNBBNBNBBCHBHHBCHB 11b - 4h
    check(calcPolymer(parsed, 4) == 18L) // NBBNBNBBCCNBCNCCNBBNBBNBBBNBBNBBCBHCBHHNHCBBCBHCB = 23 - 5
    check(calcPolymer(parsed, 10) == 1588L)

    val input = parseInput(readInput("Day14"))
    println(calcPolymer(input, 10))
    println(calcPolymer(input, 40))
}
