// Day 12, Advent of Code 2021, Passage Pathing

fun String.allLowerCase() = this == this.lowercase()

// overflow param is for the extra allowed small cave.
// This param would just be a string if Kotlin allowed var params.  
fun findPaths(input:  Map<String, Set<String>>, currCave: String, smallCaves: MutableSet<String>, overflow: MutableList<String>) : Long{
    val next = input[currCave]
    if (next.isNullOrEmpty()) return 0L
    var count = 0L
    next.forEach {
        if (it == "end") {count += 1; return@forEach} // reached end
        if (it in smallCaves && overflow.isNotEmpty()) return@forEach // already visited
        if (it.allLowerCase()) {
            when (it) {
                !in smallCaves -> smallCaves.add(it)
                else -> overflow.add(it)
            }
        }
        count += findPaths(input, it, smallCaves, overflow)
        when (it) {
            in overflow -> overflow.remove(it)
            else -> smallCaves.remove(it) // we don't care if this fails for uppercase
        }
    }
    return count
}

private fun setUpPaths(input: List<String>): Map<String, Set<String>> {
    val paths = mutableMapOf<String, MutableSet<String>>()
    input.forEach {
        val caves = it.trim().split("-")
        check(caves.size == 2)
        for (i in caves.indices) {
            val j = if (i==0) 1 else 0
            // no paths from the end, no paths to the start
            if (caves[i] != "end" && caves[j] != "start") {
                if (paths[caves[i]]==null ) {
                    paths[caves[i]]= mutableSetOf(caves[j])
                } else {
                    paths[caves[i]]?.add(caves[j])
                }
            }
        }
    }
    return paths
}

fun main() {
    fun part1(input: Map<String, Set<String>>): Long {
        // Part 1 doesn't allow small cave to be visited twice, so fill up overflow slot
        return findPaths(input, "start", mutableSetOf(), mutableListOf("NO OVERFLOW"))
    }

    fun part2(input: Map<String, Set<String>>): Long {
        return findPaths(input, "start", mutableSetOf(), mutableListOf())
    }

    val pathsA = setUpPaths(readInput("Day12_testa"))
    check(part1(pathsA) == 10L)
    check(part2(pathsA) == 36L)

    val pathsB = setUpPaths(readInput("Day12_testb"))
    check(part1(pathsB) == 19L)
    check(part2(pathsB) == 103L)

    val pathsC = setUpPaths(readInput("Day12_testc"))
    check(part1(pathsC) == 226L)
    check(part2(pathsC) == 3509L)

    val paths = setUpPaths(readInput("Day12"))
    println(part1(paths))
    println(part2(paths))
}