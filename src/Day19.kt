// Advent of Code 2021, Day 19, Beacon Scanner


fun main() {

    // sort by x, then store the deltas between x values. Do the same for y, etc.

    data class Scanner(val readings: Set<List<Int>>, val gaps: List<List<Int>>? = null) {

        // these are wrong
        // use https://www.wolframalpha.com/input/?i=yaw%3D180+degrees%2C+pitch%3D90+degrees
        val indexPerms = listOf(
            listOf(0, 1, 2), listOf(0, 2, 1),
            listOf(1, 0, 2), listOf(1, 2, 0),
            listOf(2, 0, 1), listOf(2, 1, 0)
        )

        val rotates = listOf(
            // no mirror images (can't have odd number of -1 per list)
            listOf(1, 1, 1), listOf(1, -1, -1),
            listOf(-1, 1, -1), listOf(-1, -1, 1),
        )

        val scannerTransforms = mutableListOf<Scanner>() // 24 in size, see problem description

        fun populateTransforms() {
            indexPerms.forEach { p ->
                rotates.forEach { r ->
                    val transformed = mutableSetOf<List<Int>>()
                    readings.forEach {
                        val newCoord = it.mapIndexed { idx, _ -> it[p[idx]] * r[idx] }
                        transformed.add(newCoord)
                    }
                    // sort by x, save gaps
                    //orrr, map x -> each coord, sorted by x
                    // for now , just generate gap fingerprint
                    val gaps = mutableListOf<List<Int>>()
                    for (i in 0..2) {
                        val sortedByOneAxis = transformed.sortedBy { it[0] }.map { it[0] }
                        val diffs = mutableListOf<Int>()
                        for (idx in 0..sortedByOneAxis.size - 2) { // is this right last index?
                            diffs.add(sortedByOneAxis[idx + 1] - sortedByOneAxis[idx])
                        }
                        gaps.add(diffs)
                    }
                    scannerTransforms.add(Scanner(transformed, gaps))
                }
            }
        }
    }


    fun part1(input: List<Scanner>): Long {
        input.forEach {
            it.populateTransforms()
        }
        // for  scanner 0
        val scanner0 = input[0]
        for (i in 1 until input.size) {
            input[i].scannerTransforms.forEachIndexed { idx, it ->
                for (x in -1000..1000) {
                    println("input $i, x = $x")
                    for (y in -1000..1000) {
                        for (z in -1000..1000) {
                            val moveAmount = listOf(x, y, z)
                            // move all points
                            val setPts = mutableSetOf<List<Int>>()
                            it.readings.forEachIndexed { jidx, r ->
                                setPts.add(r.mapIndexed { midx, it -> it + moveAmount[midx] })
                            }
                            // what is best way to do set intersection? or don't use sets?
                            val size = scanner0.readings.filter { it in setPts }.size
                            if (size > 0) {
                                println("scanner $i, transform $idx, add ($x, $y, $z), intersects $size")
                            }
                            // intersect with scanner0?

                        }
                    }
                }
            }
        }
        // for each other scanner
        // transform each scanner by -1000..1000 in each direction
        // look for set intersection

        return 0L
    }

    fun part2(input: List<Scanner>): Long {
        return 0L
    }

    fun parse(input: List<String>): List<Scanner> {

        val scanners = mutableListOf<Scanner>()
        val setPts = mutableSetOf<List<Int>>()
        input.forEach {
            if (it.trim().isEmpty() || it.contains("--- scanner ")) {
                if (setPts.isNotEmpty()) {
                    scanners.add(Scanner(setPts.toSet())) // deep copy of set of points
                    setPts.clear()
                }
                return@forEach // like a continue
            }
            setPts.add(it.split(',').map { it.toInt() })
        }
        if (setPts.isNotEmpty()) {
            scanners.add(Scanner(setPts.toSet())) // deep copy of set of points
        }
        return scanners
    }

    val testInput = parse(readInput("Day19_test"))
    check(part1(testInput) == 0L)
    check(part2(testInput) == 0L)

    val input = readInput("Day19")
//    println(part1(input))
//    println(part2(input))
}