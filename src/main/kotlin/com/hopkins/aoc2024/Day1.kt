package com.hopkins.aoc2024

import java.io.File

fun main(args: Array<String>) {
    val lines : List<String> = File("input/input01.txt").readLines()

    val (listA, listB) = lines.map { parseLine(it) }.unzip()

    // Part 1
    val result1 = listA.sorted().zip(listB.sorted()).map { (a, b) -> Math.abs(a - b) }.sum()
    println("Part 1 Result: $result1")

    // Part 2
    val frequencyMap: Map<Int, Int> = listB.groupingBy { it }.eachCount()
    val result2 = listA.map { it * frequencyMap.getOrDefault(it, 0) }.sum()
    println("Part 2 Result: $result2")
}

private fun parseLine(line: String): Pair<Int, Int> =
    line.split("   ").map { it.toInt() }.zipWithNext().first()