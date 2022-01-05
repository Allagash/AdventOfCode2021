// Advent of Code 2021, Day 22, Reboot Reactor
import kotlin.math.max
import kotlin.math.min

fun main() {

    data class Cuboid(val on: Boolean,
                      val minx: Int, val maxx: Int,
    val miny: Int, val maxy: Int,
    val minz: Int, val maxz: Int
    )

    fun parse(input: List<String>, part1: Boolean): List<Cuboid> {
        val cubes = mutableListOf<Cuboid>()
        // x, y, and z positions of at least -50 and at most 50
        input.forEach {
            val vals = it.split(" x=", "..", ",y=", ",z=")
            val isOn = vals[0] == "on"
            cubes.add(Cuboid(
                isOn, vals[1].toInt(), vals[2].toInt(),
                vals[3].toInt(), vals[4].toInt(),
                vals[5].toInt(), vals[6].toInt()
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
        // set min, max 50?
        return cubes2
    }

    fun part1(cuboids: List<Cuboid>): Int {
        val pts = mutableSetOf<Triple<Int, Int, Int>>()
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

    fun part2(cuboids: List<Cuboid>): Int {
        println("part2, size: ${cuboids.size}")
        val pts = mutableSetOf<Triple<Int, Int, Int>>()
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

    val testInput = parse(readInput("Day22_testb"), true)
    check(part1(testInput) == 590784)

    val testInputC = parse(readInput("Day22_testc"), false)
    println(part2(testInputC))

//    check(imageEnhance(testInput, 2) == 35L)
//    check(imageEnhance(testInput, 50) == 3351L)

    val input = parse(readInput("Day22"), true)
    println(part1(input))
//    println(imageEnhance(input, 2))
//    println(imageEnhance(input, 50))
}