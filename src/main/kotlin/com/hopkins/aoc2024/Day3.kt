package com.hopkins.aoc2024

import java.io.File


fun main(args: Array<String>) {
    val lines: List<String> = File("input/input03.txt").readLines()

    // Prepend the implicit "do()" at the start and append the implicit "don't()" at the end of the program
    // This way we can look for "do()<instructions>don't()" segments without special handling for the start/end
    val input = "do()" + lines.joinToString(" ") + "don't()"

    // A multiply operation has 1-3 digits per argument
    val multiplyPattern = Regex("""mul\((\d\d?\d?),(\d\d?\d?)\)""")

    // Part 1
    val numbers = multiplyPattern.findAll(input).map { result -> matchResultToNumbers(result) }.toList()
    println("Numbers: $numbers")
    val result = multiplyThenSumPairs(numbers)
    println("Result: $result")


    // Part 2
    // Note: we need to use a non-greedy match ".*?" so that we match the first "don't()"
    val doDontPattern = Regex("""do\(\)(.*?)don't\(\)""")

    // Only keep the input that is in-between do()/don't() pairs.  The rest of the input can be discarded.
    val input2 = doDontPattern.findAll(input).map { it.groupValues[1]}.joinToString(" ")
    val numbers2 = multiplyPattern.findAll(input2).map { result -> matchResultToNumbers(result) }.toList()
    println("Numbers 2: $numbers2")
    val result2 = multiplyThenSumPairs(numbers2)
    println("Result: $result2")
}

private fun multiplyThenSumPairs(input: List<Pair<Int, Int>>): Long =
    input.sumOf { (left, right) -> left.toLong() * right.toLong() }

private fun matchResultToNumbers(result: MatchResult): Pair<Int, Int> {
    check(result.groupValues.size == 3) { "Expected size 2, but was: ${result.groupValues}"}
    val (_, left, right) = result.groupValues
    return Pair(left.toInt(), right.toInt())
}