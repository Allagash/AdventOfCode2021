// Advent of Code 2021, Day 25, Sea Cucumber

fun main() {

    data class Grid( val cells: Array<Array<Char>>) {

        override fun toString(): String {
            var str = ""
            cells.forEachIndexed { _, it ->
                it.forEach {
                    str += it
                }
                str += '\n'
            }
            return str
        }

        fun deepCopy() : Grid {
            val nextCells = cells.clone()
            cells.forEachIndexed { i, chars ->
                nextCells[i] = chars.clone()
            }
            return Grid(nextCells)
        }
    }

    fun part1(input: Grid): Long {
        println("iniiial:\n")
        println(input)
        var steps = 0L
        var grid = input

        do {
            var moved = false
            //val nextGrid = grid.copy()
            var nextGrid = grid.deepCopy()

            // move east
            grid.cells.forEachIndexed { i, it ->
                it.forEachIndexed { j, _ ->
                    val nextj = (j+1) % it.size
                    if (grid.cells[i][j] == '>' && grid.cells[i][nextj] == '.' ) {
                        nextGrid.cells[i][nextj] = '>'
                        nextGrid.cells[i][j] = '.'
                        moved = true
                    }
                }
            }
            grid = nextGrid
            nextGrid = grid.deepCopy()
            // move south
            grid.cells.forEachIndexed { i, it ->
                val nexti = (i+1) % grid.cells.size
                it.forEachIndexed { j, _ ->
                    if (grid.cells[i][j] == 'v' && grid.cells[nexti][j] == '.' ) {
                        nextGrid.cells[nexti][j] = 'v'
                        nextGrid.cells[i][j] = '.'
                        moved = true
                    }
                }
            }
            steps++
            grid = nextGrid

            println("Step $steps:\n")
           // println(grid)

        } while(moved)
        return steps
    }

    fun parse(input: List<String>) : Grid {
        val grid = Array(input.size) { Array(input[0].length) { '.' } }
        input.forEachIndexed { i, it ->
            it.forEachIndexed { j, jit ->
                grid[i][j] = jit
            }
        }
        return Grid(grid)
    }

    val testInput1 = parse(readInput("Day25_test"))
    check(part1(testInput1) == 58L)

    val input = parse(readInput("Day25"))
    println(part1(input))
}