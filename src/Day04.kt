// Day 4, Advent of Code 2021, Giant Squid bingo
fun main() {
    val testInput = readInput("Day04_test")
    val calledNums = testInput[0].split(",").map { it.toInt() } // called Bingo numbers
    val boards = setUpBoards(testInput)

    check(part1(calledNums, boards) == 4512)
    check(part2(calledNums, boards) == 1924)

    val input2 = readInput("Day04")
    val calledNums2 = input2[0].split(",").map { it.toInt() } // called Bingo numbers
    val boards2 = setUpBoards(input2)
    println(part1(calledNums2, boards2))
    println(part2(calledNums2, boards2))
}

private fun part1(called: List<Int>, boards: ArrayList<Board>) : Int{
    called.forEach {
        boards.forEach { b ->
            if (b.mark(it) && b.isDone()) {
                return it * b.sumUnMarked()
            }
        }
    }
    return 0
}

data class Board (val values:  Array<Array<Int>>) {
    fun mark(call: Int): Boolean {
        var marked = false
        values.forEach {
            it.forEachIndexed { idx, i ->
                if (i == call) {
                    it[idx] = -1
                    marked = true
                }
            }
        }
        return marked
    }

    fun isDone(): Boolean {
        values.forEach {
            var done = true
            for (i in it) {
                if (i != -1) {
                    done = false; break
                }
            }
            if (done) return true
        }

        for (j in 0..4) {
            var done = true
            for (i in 0..4) {
                if (values[i][j] != -1) {
                    done = false; break
                }
            }
            if (done) return true
        }
        return false
    }

    fun sumUnMarked() =
        values.fold(0) { acc, it ->
            acc + it.fold(0) { jacc, jit -> jacc + if (jit != -1) jit else 0 }
        }

    override fun toString() : String {
        var s = String()
        values.forEach {
            it.forEach {i ->  s += i.toString(); s+= " " }
            s += '\n'
        }
        return s
    }
}

private fun part2(called: List<Int>, squidBoards: ArrayList<Board>) : Int {
    val boardsLeft = mutableSetOf<Int>()
    boardsLeft.addAll(squidBoards.indices)

    called.forEach {
        for (idx in squidBoards.indices) {
            if (!boardsLeft.contains(idx)) {
                continue
            }
            if (squidBoards[idx].mark(it) && squidBoards[idx].isDone()) {
                boardsLeft.remove(idx)
                if (boardsLeft.isEmpty()) {
                    return it * squidBoards[idx].sumUnMarked()
                }
            }
        }
    }
    return 0
}

private fun setUpBoards (
    lines: List<String>
): ArrayList<Board> {
    val boards = ArrayList<Board>()

    var i = 1
    while (i < lines.size) {
        var str = lines[i].trimStart()
        if (str.length < 2) {
            i++
            continue
        }

        val values = Array(5) { Array(5) { 0 } }

        for (idx in 0..4) {
            str = lines[i].trimStart()
            val numStrs = str.split("  ", " ")
            val nums = IntArray(numStrs.size)
            numStrs.forEachIndexed { j, s -> nums[j] = s.toInt() }
            nums.forEachIndexed { j, n -> values[idx][j] = n }
            i++
        }
        boards.add(Board(values))
    }
    return boards
}