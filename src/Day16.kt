// Day 16, Advent of Code 2021, Packet Decoder

// Convert string of hex digits to string of binary
fun parseInput(input: String): List<Packet> {
    val binary = String(input.map { it.toString().toInt(16).toString(2).padStart(4, '0') }
        .flatMap { it.toList() }
        .toCharArray())
    val packets = mutableListOf<Packet>()
    val index = parsePackets(binary, 0, packets)
    check(binary.substring(index).all { it == '0' })
    return packets
}

abstract class Packet {
    var version = -1
    var type = -1
    var index = -1

    abstract fun evaluate() : Long
}

class LiteralPacket(bitStream: String, currIndex: Int) : Packet() {
    var value = -1L

    init {
        index = currIndex
        version = bitStream.substring(index, index+3).toInt(2)
        index += 3
        type = bitStream.substring(index, index+3).toInt(2)
        check(type == 4)
        index += 3
        value = 0L
        do {
            val initChar = bitStream[index]
            index++
            value = (value shl 4) + bitStream.substring(index, index+4).toInt(2)
            index+=4
        } while(initChar != '0')
    }

    override fun evaluate() = value
}

class OperatorPacket (bitStream: String, currIndex: Int) : Packet() {
    val packets = mutableListOf<Packet>()

    init {
        index = currIndex
        version = bitStream.substring(index, index+3).toInt(2)
        index += 3
        type = bitStream.substring(index, index+3).toInt(2)
        index += 3
        val lengthType = bitStream[index]
        index++
        if (lengthType == '0') {
            var numBits = bitStream.substring(index, index+15).toInt(2)
            index+=15
            while (numBits > 0) {
                val startIndex = index
                val subPackets = mutableListOf<Packet>()
                index = parsePackets(bitStream, index, subPackets)
                numBits -= (index - startIndex)
                packets.addAll(subPackets)
            }
        } else {
            val numPackets = bitStream.substring(index, index+11).toInt(2)
            index+=11
            for (i in 0 until numPackets) {
                val subPackets = mutableListOf<Packet>()
                index = parsePackets(bitStream, index, subPackets)
                packets.addAll(subPackets)
            }
        }
    }

    override fun evaluate() : Long {
        return when (type) {
            0 -> packets.fold(0L) {sum, it -> sum + it.evaluate()}
            1 -> packets.fold(1L) {sum, it -> sum * it.evaluate()}
            2 -> packets.minOf{ it.evaluate() }
            3 -> packets.maxOf{ it.evaluate() }
            5 -> if (packets[0].evaluate() > packets[1].evaluate()) 1L else 0L
            6 -> if (packets[0].evaluate() < packets[1].evaluate()) 1L else 0L
            7 -> if (packets[0].evaluate() == packets[1].evaluate()) 1L else 0L
            else -> throw Exception ("Bad type $type")
        }
    }
}

fun parsePackets(bitStream: String, index: Int, packets: MutableList<Packet>) : Int {
    val newIndex : Int
    val packet = when (bitStream.substring(index+3, index+6)) {
        "100" -> LiteralPacket(bitStream, index)
        else -> OperatorPacket(bitStream, index)
    }
    newIndex = packet.index
    packets.add(packet)
    return newIndex
}

fun sumVersions(packets: List<Packet>) : Long {
    var sum = 0L
    packets.forEach {
        when (it) {
            is LiteralPacket -> sum += it.version
            is OperatorPacket -> sum += it.version + sumVersions(it.packets)
        }
    }
    return sum
}

fun main() {
    fun part1(packets: List<Packet>) = sumVersions(packets)

    fun part2(packets: List<Packet>) = packets[0].evaluate()

    check(part1(parseInput("D2FE28")) == 6L) // 110100101111111000101000
    check(part1(parseInput("38006F45291200")) == 9L) // 00111000000000000110111101000101001010010001001000000000
    check(part1(parseInput("EE00D40C823060")) == 14L) // 11101110000000001101010000001100100000100011000001100000
    check(part2(parseInput("C200B40A82")) == 3L)
    check(part2(parseInput("04005AC33890")) == 54L)
    check(part2(parseInput("880086C3E88112")) == 7L)
    check(part2(parseInput("CE00C43D881120")) == 9L)
    check(part2(parseInput("D8005AC2A8F0")) == 1L)
    check(part2(parseInput("F600BC2D8F")) == 0L)
    check(part2(parseInput("9C005AC2F8F0")) == 0L)
    check(part2(parseInput("9C0141080250320F1802104A08")) == 1L)

    val input = parseInput(readInputAsOneLine("Day16"))
    println(part1(input))
    println(part2(input))
}
