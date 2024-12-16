package com.hopkins.aoc2024

import java.io.File

fun main(args: Array<String>) {
    // Read the input file
    val lines: List<String> = File("input/input13.txt").readLines().filter { it.isNotEmpty() }

    // Parse into a list of games
    val games = mutableListOf<Game>()
    var buttonA: Point2d = Point2d.ORIGIN
    var buttonB: Point2d = Point2d.ORIGIN
    var prize: Point2d
    for (line in lines) {
        if (line.startsWith("Button A:")) {
            buttonA = parsePoint(line)
        } else if (line.startsWith("Button B:")) {
            buttonB = parsePoint(line)
        } else if (line.startsWith("Prize:")) {
            require(buttonA != Point2d.ORIGIN)
            require(buttonB != Point2d.ORIGIN)
            prize = parsePoint(line)
            games.add(Game(buttonA, buttonB, prize))
            buttonA = Point2d.ORIGIN
            buttonB = Point2d.ORIGIN
        }
    }

    val result = games.sumOf { game -> findMinCostToWin(game) }
    println("Result: $result")

}

private fun findMinCostToWin(game: Game): Int {
    for (numA in 0 .. 100) {
        val position = game.buttonA.multiply(numA)

        val delta = game.prize.subtract(position)
        if (delta.x % game.buttonB.x == 0) {
            val numB = delta.x / game.buttonB.x
            if (numB * game.buttonB.y == delta.y && numB <= 100) {
                return numA * 3 + numB
            }
        }
    }
    // Game not winnable
    return 0
}

private fun parsePoint(line: String): Point2d {
    val regex = Regex(" X[=+](\\d.*), Y[=+](\\d.*)")
    val match = regex.find(line)!!
    return Point2d(match.groups[1]!!.value.toInt(), match.groups[2]!!.value.toInt())
}

private data class Game(val buttonA: Point2d, val buttonB: Point2d, val prize: Point2d)