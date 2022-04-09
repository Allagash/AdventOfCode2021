// Advent of Code 2021, Day 24, Arithmetic Logic Unit

fun main() {

    fun getSecondParam(
        inst: List<String>,
        register: MutableMap<Char, Long>
    ) = if (inst[2][0] in 'w'..'z') register[inst[2][0]]!! else inst[2].toLong()

    var count = 0L

    fun MutableList<Int>.setToNextLowerNum() {
        var lastIdx = lastIndex
        for (i in lastIndex downTo 0) {
            lastIdx = i
            if (this[lastIdx] != 1) break
        }
        this[lastIdx] -= 1
        for (i in lastIdx+1.. lastIndex) {
            this[i] = 9
        }
    }

    fun getNextNum(num: Long) : Pair<Long, MutableList<Int>> {
        var num1 = num
        var num2 = num
        val digits = mutableListOf<Int>()

        while (num1 > 0) {
            val digit = (num1 % 10L).toInt()
            if (digit == 0) {
                num2--
                num1 = num2
                count++
                digits.clear()
                continue
            }
            digits.add(0, digit)
            num1 /= 10L
        }
        return Pair(num2, digits)
    }

    fun getDigits(num: Long) : MutableList<Int> {
        check(num > 0)
        val digits = emptyList<Int>().toMutableList()
        var mynum = num
        while (mynum > 0) {
            digits.add((mynum % 10).toInt())
            mynum /= 10
        }
        return digits.asReversed()
    }

    fun getZ(digits: List<Int>) : Long {
        var z = digits[0] + 4L
        z = z * 26 + (digits[1]  + 16)
        z = (z * 26) + digits[2] + 14
        val check34 = (digits[3] == digits[2] + 1)
        z = (z / 26) * (if (check34) 1 else 26) + (digits[3] + 3) * (if (check34) 0 else 1)
        z = z * 26 + digits[4] + 11
        z = z * 26 + digits[5] + 13
        val check7 =  (z % 26 - 7) == digits[6].toLong()
        z = (z/26) * ( if (check7) 1 else 26) + (if (check7) 0 else (digits[6] + 11))
        z = z * 26 + digits[7] + 7
        val check9 = ( z % 26 - 12) == digits[8].toLong()
        z = (z / 26 * (25 * ( if (check9) 0 else 1) + 1)) +  (digits[8]+ 12) *  ( if (check9) 0 else 1)
        z = z * 26 +  digits[9]+ 15
        val check11 = ((z % 26 - 16) == digits[10].toLong())
        z = z / 26 * (25 * ( if (check11) 0 else 1) + 1) + (digits[10] + 13) * ( if (check11) 0 else 1)
        val check12 = (z % 26 -9) == digits[11].toLong()
        z = ( z/26 * ( 25 * (if (check12) 0 else 1) + 1)) +  (digits[11] + 1) *  if (check12) 0 else 1
        val check13 = (z % 26 - 8) == digits[12].toLong()
        z = z/26 * ( if (check13) 1 else 26) + if (check13) 0 else (digits[12]+ 15)
        val check14 = ( z % 26 - 8) == digits[13].toLong()
        z = z / 26 * ( if (check14) 1 else 26) + (if (check14) 0 else (digits[13] + 4))
        return z
    }

    fun part1(input: List<String>): Long {
        var minZ = Long.MAX_VALUE

        var count = 0

        var test  = emptyList<Int>().toMutableList()
        for (i in 1..3) test.add(9)
        println("test = $test")
        do {
            test.setToNextLowerNum()
            println("test = $test")
        } while (!test.all{it == 1})

        for (num in 99999999999999L downTo 10000000000000L) {
            // check that no digit is 0!
            // val (newNum, digits) = getNextNum(num)
            // num = newNum
            val digits = getDigits(num)
            if (digits.contains(0)) continue

            val register = mutableMapOf('w' to 0L, 'x' to 0L, 'y' to 0L, 'z' to 0L)

//            input.forEach {
//                val inst = it.split(' ')
//                val firstReg = inst[1][0]
//                val firstRegVal = register[firstReg]!!
//                when (inst[0]) {
//                    "inp" -> register[firstReg] = digits.removeFirst().toLong()
//                    "add" -> register[firstReg] = firstRegVal + getSecondParam(inst, register)
//                    "mul" -> register[firstReg] = firstRegVal * getSecondParam(inst, register)
//                    "div" -> register[firstReg] = firstRegVal / getSecondParam(inst, register)
//                    "mod" -> register[firstReg] = firstRegVal % getSecondParam(inst, register)
//                    "eql" -> register[firstReg] = if (firstRegVal == getSecondParam(inst, register)) 1L else 0L
//                    else -> throw Exception("bad instruction ${inst[0]}")
//                }
//            }
            val newz = getZ(getDigits(num))
//            check(newz == register['z']!!)
            //
            count++
            if (count > 1000000) {
                count = 0
                println("num = $num")
            }
            //println("input: $num, new z = $newz, $register")
            if (minZ > newz) {
                //println("input: $num, $register")
                minZ =  newz
                println("New min value for $num: $minZ")
                if (minZ == 0L) return num
            }
        }
        return 0L
    }


    val testInput1 = readInput("Day24_test")
    part1(testInput1)

//    val input = readInput("Day24")
//    println(part1(input))
}