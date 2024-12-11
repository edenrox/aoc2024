package com.hopkins.aoc2024

import java.io.File

fun main(args: Array<String>) {
    // Read the input file
    val lines: List<String> = File("input/input10.txt").readLines().filter { it.isNotEmpty() }

    val heightLookup: Map<Point2d, Int> =
        lines.flatMapIndexed { y, line -> line.mapIndexed { x, c -> Point2d(x, y) to "$c".toInt()}}.toMap()
    val width = lines[0].length
    val height = lines.size

    println("Map size: ${width}x$height")

    val startPoints = heightLookup.filterValues { it == 0}.keys
    println("Start points: $startPoints")

    // Part 1
    val result = startPoints.sumOf { findReachableNines(it, heightLookup, width, height).count() }
    println("Part 1 - Results: $result")

    // Part 2
    val result2 = startPoints.sumOf { findReachableNinePaths(it, heightLookup, width, height) }
    println("Part 2 - Results: $result2")
}

private fun findReachableNines(startPoint: Point2d, heightLookup: Map<Point2d, Int>, width: Int, height: Int): Set<Point2d> {
    check(isInBounds(startPoint, width, height))
    val startValue = heightLookup[startPoint]!!
    if (startValue == 9) {
        return setOf(startPoint)
    }
    val result = mutableSetOf<Point2d>()
    for (direction in DirectionsD10.ALL) {
        val next = startPoint.add(direction)
        if (isInBounds(next, width, height)) {
            val nextValue = heightLookup[next]!!
            if (nextValue == startValue + 1) {
                result.addAll(findReachableNines(next, heightLookup, width, height))
            }
        }
    }
    return result
}

private fun findReachableNinePaths(startPoint: Point2d, heightLookup: Map<Point2d, Int>, width: Int, height: Int): Int {
    check(isInBounds(startPoint, width, height))
    val startValue = heightLookup[startPoint]!!
    if (startValue == 9) {
        return 1
    }
    var result = 0
    for (direction in DirectionsD10.ALL) {
        val next = startPoint.add(direction)
        if (isInBounds(next, width, height)) {
            val nextValue = heightLookup[next]!!
            if (nextValue == startValue + 1) {
                result += findReachableNinePaths(next, heightLookup, width, height)
            }
        }
    }
    return result
}

private fun isInBounds(point: Point2d, width: Int, height: Int) =
    point.x >= 0 && point.y >= 0 && point.x < width && point.y < height

private object DirectionsD10 {
    val UP = Point2d(0, -1)
    val DOWN = Point2d(0, 1)
    val LEFT = Point2d(-1, 0)
    val RIGHT = Point2d(1, 0)

    val ALL = listOf(UP, DOWN, LEFT, RIGHT)
}