package com.hopkins.aoc2024

import java.io.File
import kotlin.math.pow

fun main(args: Array<String>) {
    // Read the input file
    val lines: List<String> = File("input/input17.txt").readLines().filter { it.isNotEmpty() }

    // Parse the input
    var registers = lines.filter { it.startsWith("Register ")}.map {
        val (key, value) = it.drop(9).split(": ")
        key to value.toInt()
    }.toMap()
    var program = lines.first { it.startsWith("Program: ") }
        .drop(9).split(",").map { it.toInt()}

    println("Registers: $registers")
    println("Program: $program")

    val computer = Computer(program)
    computer.setRegisters(registers)
    computer.execute()
    println("Output: ${computer.out.joinToString(",")}")
}

class Computer(val program: List<Int>) {
    var a: Int = 0
    var b: Int = 0
    var c: Int = 0
    var ip: Int = 0
    var out = mutableListOf<Int>()

    fun setRegisters(map: Map<String, Int>) {
        a = map["A"]!!
        b = map["B"]!!
        c = map["C"]!!
    }

    private fun getCombo(arg: Int): Int =
        when {
            arg <= 3 -> arg
            arg == 4 -> a
            arg == 5 -> b
            arg == 6 -> c
            else -> throw IllegalArgumentException("Unexpected: $arg")
        }

    fun execute() {
        while (true) {
            if (ip >= program.size) {
                println("Halt!")
                return
            }
            val opCode = OpCode.values()[program[ip]]
            val arg = program[ip+1]
            ip += 2
            when (opCode) {
                OpCode.ADV -> {
                    // Division --> A
                    val result = a / (2.0.pow(getCombo(arg))).toInt()
                    a = result
                }
                OpCode.BXL -> {
                    // Bitwise XOR --> B
                    val result = b xor arg
                    b = result
                }
                OpCode.BST -> {
                    // Combo module 8 --> B
                    val result = getCombo(arg) % 8
                    b = result
                }
                OpCode.JNZ -> {
                    if (a != 0) {
                        ip = arg
                    }
                }
                OpCode.BXC -> {
                    val result = b xor c
                    b = result
                }
                OpCode.OUT -> {
                    val result = getCombo(arg) % 8
                    out += result
                }
                OpCode.BDV -> {
                    val result = a / (2.0.pow(getCombo(arg))).toInt()
                    b = result
                }
                OpCode.CDV -> {
                    val result = a / (2.0.pow(getCombo(arg))).toInt()
                    c = result
                }
            }
        }
    }
}


enum class OpCode {
    ADV,
    BXL,
    BST,
    JNZ,
    BXC,
    OUT,
    BDV,
    CDV,
}