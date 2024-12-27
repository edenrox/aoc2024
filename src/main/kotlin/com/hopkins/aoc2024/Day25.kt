package com.hopkins.aoc2024

import java.io.File

fun main(args: Array<String>) {
    // Read the input file
    val lines: List<String> = File("input/input25.txt").readLines()

    val sections = lines.chunked(8).map { it.subList(0, 7) }

    println("Sections:")
    sections.forEach { println(" $it") }

    val locks = sections.filter {
        it.first().startsWith("#####")
    }.map { toHeights(it) }

    val keys = sections.filter {
        it.last().startsWith("#####")
    }.map { toHeights(it.reversed()) }

    println("Keys:")
    keys.forEach { println(" $it")}

    println("Locks:")
    locks.forEach { println(" $it")}

    var result = 0
    for (key in keys) {
        for (lock in locks) {
            if (!overlaps(key, lock)) {
                result++
            }
        }
    }
    println("Result: $result")
}

private fun overlaps(key: List<Int>, lock: List<Int>): Boolean =
    key.zip(lock).any { (a, b) -> a + b > 5 }



private fun toHeights(lines: List<String>): List<Int> {
    println("Lines: $lines")
    return (0 until 5).map { x ->
        val column = (1 until 6).map {
            y -> lines[y][x]
        }
        column.takeWhile { it == '#'}.count()
    }
}