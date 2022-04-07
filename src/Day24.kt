// Advent of Code 2021, Day 24, Arithmetic Logic Unit

fun main() {

    fun getSecondParam(
        inst: List<String>,
        register: MutableMap<Char, Long>
    ) = if (inst[2][0] in 'w'..'z') register[inst[2][0]]!! else inst[2].toLong()

    var count = 0L

    fun getNextNum(num: Long) : Pair<Long, MutableList<Long>> {
        var num1 = num
        var num2 = num
        val digits = mutableListOf<Long>()

        while (num1 > 0) {
            val digit = num1 % 10L
            if (digit == 0L) {
                num2--
                num1 = num2
                count++
                digits.clear()
                if (count % 100000L == 0L) {
                    println("num = $num1")
                }
                continue
            }
            digits.add(0, digit)
            num1 /= 10L
        }
        return Pair(num2, digits)
    }

    fun part1(input: List<String>): Long {
        var done = false
        var num = 98765432198765L
        do {
            // check that no digit is 0!
            val (newNum, digits) = getNextNum(num)
            num = newNum

            val register = mutableMapOf<Char, Long>()
            register['w'] = 0L
            register['x'] = 0L
            register['y'] = 0L
            register['z'] = 0L

            input.forEach {
                val inst = it.split(' ')
                val firstReg = inst[1][0]
                val firstRegVal = register[firstReg]!!
                when (inst[0]) {
                    "inp" -> register[firstReg] = digits.removeFirst()
                    "add" -> register[firstReg] = firstRegVal + getSecondParam(inst, register)
                    "mul" -> register[firstReg] = firstRegVal * getSecondParam(inst, register)
                    "div" -> register[firstReg] = firstRegVal / getSecondParam(inst, register)
                    "mod" -> register[firstReg] = firstRegVal % getSecondParam(inst, register)
                    "eql" -> register[firstReg] = if (firstRegVal == getSecondParam(inst, register)) 1L else 0L
                    else -> throw Exception("bad instruction ${inst[0]}")
                    }
            }
            if (register['z'] == 0L) {
                done = true
                return num
            } else {
                num--
            }
        } while(!done)

        return 0L
    }


//    val testInput1 = readInput("Day24_test")
//    check(part1(testInput1) == 0L)

    val input = readInput("Day24")
    println(part1(input))
}