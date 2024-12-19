package com.hopkins.aoc2024

import java.io.File

fun main(args: Array<String>) {
    // Read the input file
    val lines: List<String> = File("input/input19.txt").readLines().filter { it.isNotEmpty() }

    // Parse the input
    val towels = lines[0].split(", ")
    val patterns = lines.drop(1)

    val count =
        patterns.count { pattern ->
            val cache = mutableSetOf<String>()
            val canMake = canMake(towels, pattern, cache)
            println("P: $pattern CanMake: $canMake")
            canMake
        }
    println("Count: $count")
}

private fun canMake(towels: List<String>, pattern: String, cache: MutableSet<String>): Boolean {
    if (cache.contains(pattern)) {
        return false
    }
    if (pattern.isEmpty()) {
        return true
    }
    println(" pattern: $pattern")
    val matchingTowels = towels.filter { pattern.startsWith(it) }.sortedByDescending { it.length}
    for (towel in matchingTowels) {
        println("  towel: $towel")
        val patternRemaining = pattern.drop(towel.length)
        if (canMake(towels, patternRemaining, cache)) {
            return true
        }
    }
    cache.add(pattern)
    return false
}