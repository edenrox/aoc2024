package com.hopkins.aoc2024

import java.io.File
import kotlin.math.abs

fun main(args: Array<String>) {
    val lines: List<String> = File("input/input02.txt").readLines()
    val parsedLines: List<List<Int>> = lines.map { parseLine(it) }

    // Part 1
    val result1 = parsedLines.count { isSafe(it) }
    println("Part 1: $result1")

    // Part 2
    val result2 = parsedLines.count { isSafeWithDampener(it) }
    println("Part 2: $result2")
}

private fun isSafeWithDampener(levels: List<Int>): Boolean {
    for (i in levels.indices) {
        val partialList = levels.filterIndexed { index, _ -> index != i }
        if (isSafe(partialList)) {
            return true
        }
    }
    return false
}

private fun isSafe(levels: List<Int>): Boolean {
    val diffs = levels.zipWithNext().map { it.second - it.first}
    val validDiffs = diffs.filter { isValidDiff(it) }
    val directions = diffs.map { if (it > 0) 1 else -1 }

    val allDiffsValid = validDiffs.count() == diffs.count()
    val allDirectionsSame = directions.all { it == directions.first() }

    return allDiffsValid && allDirectionsSame
}

private fun isValidDiff(diff: Int) =
    abs(diff) in 1..3


private fun parseLine(line: String): List<Int> =
    line.split(" ").map { it.toInt() }