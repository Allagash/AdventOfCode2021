// Advent of Code 2021, Day 22, Reboot Reactor
import kotlin.math.max
import kotlin.math.min

fun main() {

    data class Cuboid(
        val on: Boolean,
        val minx: Long, val maxx: Long,
        val miny: Long, val maxy: Long,
        val minz: Long, val maxz: Long
    ) {
        fun intersection(other: Cuboid) : Cuboid? {
            return if (maxx >= other.minx && maxy >= other.miny && maxz >= other.minz &&
                       minx <= other.maxx && miny <= other.maxy && minz <= other.maxz) {
                Cuboid(!other.on,
                    max(minx, other.minx), min(maxx, other.maxx),
                    max(miny, other.miny), min(maxy, other.maxy),
                    max(minz, other.minz), min(maxz, other.maxz))
            } else {
                null
            }
        }
    }

    fun parse(input: List<String>, part1: Boolean): List<Cuboid> {
        val cubes = mutableListOf<Cuboid>()
        // x, y, and z positions of at least -50 and at most 50
        input.forEach {
            val vals = it.split(" x=", "..", ",y=", ",z=")
            val isOn = vals[0] == "on"
            cubes.add(Cuboid(
                isOn, vals[1].toLong(), vals[2].toLong(),
                vals[3].toLong(), vals[4].toLong(),
                vals[5].toLong(), vals[6].toLong()
            ))
        }
        if (!part1) {
            return cubes
        }
        val cubes2 = mutableListOf<Cuboid>()
        cubes.forEach {
            if (it.minx > 50 || it.miny > 50 || it.minz > 50) return@forEach
            if (it.maxx < -50 || it.maxy < -50 || it.maxz < -50) return@forEach
            cubes2.add(Cuboid(it.on, max(it.minx, -50), min(it.maxx, 50),
            max(it.miny, -50), min(it.maxy, 50),
            max(it.minz, -50), min(it.maxz, 50)))
        }
        return cubes2
    }

    fun part1(cuboids: List<Cuboid>): Int {
        val pts = mutableSetOf<Triple<Long, Long, Long>>()
        cuboids.forEach {
            for (x in it.minx..it.maxx) {
                for (y in it.miny..it.maxy) {
                    for (z in it.minz..it.maxz) {
                        if (it.on) {
                            pts.add(Triple(x, y, z))
                        } else {
                            pts.remove(Triple(x, y, z))
                        }
                    }
                }
            }
        }
        return pts.size
    }

    // See https://github.com/xorum-io/advent-of-code-2021/blob/main/src/Day22.kt
    fun part2(cuboids: List<Cuboid>): Long {
        val stack = mutableListOf<Cuboid>()
        cuboids.forEach { cit->
            val newOperations = mutableListOf<Cuboid>()
            stack.forEach { jit->
                cit.intersection(jit)?.let { newOperations.add(it) }
            }
            stack.addAll(newOperations)
            // Only apply if turning cubes on. For instance, if
            // all cubes are off & we have an off instruction, we don't
            // do anything. We only care about off instructions if they
            // intersect previous instructions.
            if (cit.on) {
                stack.add(cit)
            }
        }
        return stack.sumOf {
            (if (it.on) 1 else -1) *
                    (it.maxx - it.minx + 1) *
                    (it.maxy - it.miny + 1) *
                    (it.maxz - it.minz + 1)
        }
    }

    var testInput = parse(readInput("Day22_testd"), true)
    check(part1(testInput) == 474140)
    testInput = parse(readInput("Day22_testd"), false)
    check(part2(testInput) == 2758514936282235L)

    var input = parse(readInput("Day22"), true)
    println(part1(input))
    input = parse(readInput("Day22"), false)
    println(part2(input))
}