package com.hopkins.aoc2024

import java.io.File

const val DEBUG = false

fun main(args: Array<String>) {
    val lines: List<String> = File("input/input04.txt").readLines()

    // Convert the lines of characters into a Map of Point -> Char
    val grid: Map<Point, Char> =
        lines
            .flatMapIndexed { y, line -> line.mapIndexed { x, c -> Point(x, y) to c }}
            .toMap()

    if (DEBUG) {
        println("Input: ")
        printGrid(grid)
    }

    ////////////
    // PART 1 //
    ////////////

    // Find start positions
    val wordToFind = "XMAS"
    val startPositions: Map<Point, Char> =
        grid.filterKeys { key -> isValidStartPoint(grid, key, wordToFind[0]) }

    if (DEBUG) {
        println("Start Positions: ")
        printGrid(startPositions)
    }

    // Find the valid letters
    val validLetters: Map<Point, Char> =
        startPositions
            .flatMap { (point, _) ->
                findValidDirections(grid, point, wordToFind)
                    .flatMap { direction -> generatePoints(point, direction, wordToFind.length) } }
            .associateWith { grid[it]!! }

    if (DEBUG) {
        println("Valid letters: ")
        printGrid(validLetters)
    }

    val numMatches =
        startPositions
            .flatMap { (point, _) ->
                findValidDirections(grid, point, wordToFind) }
            .count()
    println("Part 1 Result: $numMatches")

    ////////////
    // PART 2 //
    ////////////

    // Find start positions
    val startPositions2: Map<Point, Char> =
        grid.filterKeys { key -> isValidStartPoint(grid, key, 'A') }

    println("Start positions 2:")
    printGrid(startPositions2)

    val validXPositions = startPositions2.filter {(point, _) -> isValidX(grid, point) }.count()
    println("Num valid Xes: $validXPositions")
}

private fun isValidX(grid: Map<Point, Char>, point: Point): Boolean {
    val word1 = getWord(grid, point.add(Directions.UP_LEFT), Directions.DOWN_RIGHT, 3)
    val word2 = getWord(grid, point.add(Directions.UP_RIGHT), Directions.DOWN_LEFT, 3)

    return (word1 == "MAS" || word1 == "SAM")
            && (word2 == "MAS" || word2 == "SAM")
}

private fun getWord(grid: Map<Point, Char>, point: Point, direction: Point, length: Int): String {
    return (0 until length)
        .map { index -> point.add(direction.multiply(index)) }
        .map { grid.getOrDefault(it, ' ')}
        .joinToString("")
}

private fun isValidStartPoint(grid: Map<Point, Char>, point: Point, letter: Char): Boolean {
    return grid.getOrDefault(point, ' ') == letter
}

private fun findValidDirections(grid: Map<Point, Char>, point: Point, word: String): List<Point> {
    check(word.isNotEmpty())
    check(grid[point] == word[0])
    return Directions.ALL.filter { isMatch(grid, point, word, it) }
}

private fun isMatch(grid: Map<Point, Char>, point: Point, word: String, direction: Point): Boolean {
    var actual = ""
    var current = point
    for (i in 0 until word.length) {
        actual += grid.getOrDefault(current, ' ')
        current = current.add(direction)
    }
    return actual == word
}

private fun generatePoints(point: Point, direction: Point, length: Int): List<Point> {
    return (0 until length).map { point.add(direction.multiply(it)) }
}

private fun printGrid(grid: Map<Point, Char>) {
    val width = grid.keys.maxOf { it.x } + 1
    val height = grid.keys.maxOf { it.y } + 1
    for (y in 0 until height) {
        for (x in 0 until width) {
           print(grid.getOrDefault(Point(x, y), " "))
        }
        println()
    }
}

data class Point(val x: Int, val y: Int) {
    fun add(other: Point): Point =
        Point(x + other.x, y + other.y)

    fun multiply(magnitude: Int) =
        Point(x * magnitude, y * magnitude)
}

object Directions {
    val UP = Point(0, -1)
    val DOWN = Point(0, 1)
    val LEFT = Point(-1, 0)
    val RIGHT = Point(1, 0)

    val UP_LEFT = UP.add(LEFT)
    val UP_RIGHT = UP.add(RIGHT)
    val DOWN_LEFT = DOWN.add(LEFT)
    val DOWN_RIGHT = DOWN.add(RIGHT)

    val ALL = arrayOf(UP, RIGHT, DOWN, LEFT, UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT)
}