package com.hopkins.aoc2024

import java.io.File

fun main(args: Array<String>) {
    // Read the input file
    val lines: List<String> = File("input/input22.txt").readLines().filter { it.isNotEmpty() }

    val secrets: List<Long> = lines.map { it.toLong() }
    var result = 0L
    for (secret in secrets) {
        var s2k = secret
        for (i in 0 until 2000) {
            s2k = next(s2k)
        }
        println("$secret: $s2k")
        result += s2k
    }
    println("Result: $result")
}

private fun next(secret: Long): Long {
    val a = prune(mix(secret, secret * 64L))
    val b = prune(mix(a, a / 32L))
    return prune(mix(b, b * 2048))
}

private fun mix(a: Long, b: Long): Long {
    return a xor b
}

private fun prune(number: Long): Long {
    return number % 16777216
}