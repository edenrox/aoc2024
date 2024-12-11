package com.hopkins.aoc2024

import java.io.File

fun main(args: Array<String>) {
    // Read the input file
    val line: String = File("input/input11.txt").readLines().first { it.isNotEmpty() }
    val stones = line.split(" ").map { it.trim() }.filter { it.isNotEmpty() }.map { it.toLong() }

    // Part 1
    var current = stones
    for (i in 0 until  25) {
        println("Iteration $i")
        current = current.flatMap { updateStone(it) }
    }
    println("Part 1 Result: ${current.size}")

    // Part 2
    var current2 = stones.associateWith { 1L }
    for (i in 0 until  75) {
        println("Iteration $i")

        current2 = current2
            .flatMap { (key, value) -> updateStone(key).map { it to value } }
            .groupingBy { it.first }
            .fold(0L) { acc, element -> acc + element.second}
    }
    println("Part 2 Result: ${current2.values.sum()}")
}

private fun updateStone(stone: Long): List<Long> {
    val stoneStr = stone.toString()
    if (stone == 0L) {
        return listOf(1L)
    } else if (stoneStr.length % 2 == 0) {
        val left = stoneStr.substring(0, stoneStr.length / 2).toLong()
        val right = stoneStr.substring(stoneStr.length / 2, stoneStr.length).toLong()
        return listOf(left, right)
    } else {
        return listOf(stone * 2024)
    }
}