package com.hopkins.aoc2024

import java.io.File

fun main(args: Array<String>) {
    // Read the input file
    val lines: List<String> = File("input/input07-example.txt").readLines().filter { it.isNotEmpty() }
    val equations = lines.map { parseLine(it) }

    // Part 1
    val validEquations = equations.filter { isValid(it) }
    val sum = validEquations.sumOf { it.total }
    println("Sum: $sum")

    // Part 2
    val validEquations2 = equations.filter { isValid(it, true) }
    val sum2 = validEquations2.sumOf { it.total }
    println("Sum2: $sum2")
}

private fun isValid(equation: Equation, isConcatAllowed: Boolean = false): Boolean {
    return isValid(equation.total, equation.operands, isConcatAllowed)
}

private fun isValid(goal: Long, operands: List<Long>, isConcatAllowed: Boolean): Boolean {
    if (goal < 0) {
        throw IllegalArgumentException("expected positive goal, but found: $goal")
    }
    val operand = operands.last()
    if (operands.size == 1) {
        return operand == goal
    }
    val subList = operands.subList(0, operands.size - 1)

    // assume last operator is a plus
    val plusGoal = goal - operand
    if (plusGoal >= 0) {
        val plusIsValid = isValid(plusGoal, subList, isConcatAllowed)
        if (plusIsValid) {
            return true
        }
    }

    // try last operator is times
    if (goal % operand == 0L) {
        val timesGoal = goal / operand
        val timesIsValid = isValid(timesGoal, subList, isConcatAllowed)

        if (timesIsValid) {
            return true
        }
    }

    // try last operator is concat
    if (isConcatAllowed) {
        val goalString = goal.toString()
        val operandString = operand.toString()
        if (goalString.endsWith(operandString)) {
            val concatGoal = goalString.substring(0, goalString.length - operandString.length)
            if (concatGoal.isEmpty()) {
                return false
            }
            return isValid(concatGoal.toLong(), subList, true)
        }
    }
    return false
}



private fun isValid(equation: Equation, operators: List<Operator>): Boolean {
    var total = equation.operands[0]
    for (i in 0 until operators.size) {
        val operator = operators[i]
        val operand = equation.operands[i+1]
        when (operator) {
            Operator.TIMES -> total *= operand
            Operator.PLUS -> total += operand
        }
    }
    return total == equation.total
}

private fun parseLine(line: String): Equation {
    val (left, right) = line.split(":")

    return Equation(left.trim().toLong(), right.trim().split(" ").map { it.trim().toLong() })
}

private data class Equation(val total: Long, val operands: List<Long>) {
    val numOperators: Int = operands.size - 1
}

private enum class Operator {
    PLUS,
    TIMES
}