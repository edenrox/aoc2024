package com.hopkins.aoc2024

import java.io.File

fun main(args: Array<String>) {
    // Read the input file
    val lines: List<String> = File("input/input06.txt").readLines().filter { it.isNotEmpty() }

    // Parse into a Map
    val width = lines[0].length
    val height = lines.size
    val blocks = findBlocks(lines, '#')
    val map = Map(width, height, blocks)
    val start = findBlocks(lines, '^').first()

    println("Starting map:")
    printMapAndPath(map, listOf(start))

    // Part 1
    val startPosition = PositionAndDirection(start, Day6Directions.UP)
    val path = findPath(startPosition, map)
    println("Path positions: ${path.size}")
    // input06-example = 41
    // input06 = 4711

    // Part 2
    val toSkip = listOf(start)
    val allPoints = path.filterNot { toSkip.contains(it) }
    var count = 0
    for (newBlock in allPoints) {
        val mapWithExtra = Map(map.width, map.height, map.blocks + newBlock)
        if (findPath(startPosition, mapWithExtra).isEmpty()) {
            count++
        }
    }
    println("Ways to make a cycle: $count")
}

private fun findPath(start: PositionAndDirection, map: Map): Set<Point2d> {
    val path = mutableSetOf<Point2d>()
    val pathWithDirection = mutableSetOf<PositionAndDirection>()
    var current = start.position
    var currentDirection = start.direction
    while (map.contains(current)) {
        // Detect a cycle
        val toCheck = PositionAndDirection(current, currentDirection)
        if (pathWithDirection.contains(toCheck)) {
            return emptySet()
        }
        pathWithDirection.add(toCheck)
        path.add(current)


        var next = current.add(currentDirection)
        while (map.blockLookup.contains(next)) {
            currentDirection = turnRight(currentDirection)
            next = current.add(currentDirection)
        }
        current = next
    }
    return path.toSet()
}


private data class PositionAndDirection(val position: Point2d, val direction: Point2d)

private fun turnRight(direction: Point2d): Point2d =
    when (direction) {
        Day6Directions.UP -> Day6Directions.RIGHT
        Day6Directions.RIGHT -> Day6Directions.DOWN
        Day6Directions.DOWN -> Day6Directions.LEFT
        Day6Directions.LEFT -> Day6Directions.UP
        else -> throw IllegalArgumentException("Unexpected direction: $direction")
    }

private fun printMapAndPath(map: Map, path: List<Point2d>) {
    val pathLookup = path.toSet()
    for (y in 0 until map.height) {
        for (x in 0 until map.width) {
            val current = Point2d(x, y)
            print(when {
                pathLookup.contains(current) -> 'X'
                map.blockLookup.contains(current) -> '#'
                else -> '.'
            })
        }
        println()
    }
}

private fun findBlocks(lines: List<String>, toFind: Char): List<Point2d> =
    lines
        .flatMapIndexed { y, line ->
            line.mapIndexed { x, c ->
                if (c == toFind) {
                    Point2d(x, y)
                } else {
                    null
                }}}
        .filterNotNull()

private class Map(val width: Int, val height: Int, val blocks: List<Point2d>) {
    val blockLookup: Set<Point2d> = blocks.toSet()

    fun contains(point: Point2d): Boolean =
        point.x >= 0 && point.y >= 0 && point.x < width && point.y < height
}


private object Day6Directions {
    val UP = Point2d(0, -1)
    val DOWN = Point2d(0, 1)
    val LEFT = Point2d(-1, 0)
    val RIGHT = Point2d(1, 0)
}