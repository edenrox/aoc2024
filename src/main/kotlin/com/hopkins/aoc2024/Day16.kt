package com.hopkins.aoc2024

import java.io.File
import kotlin.math.abs

fun main(args: Array<String>) {
    // Read the input file
    val lines: List<String> = File("input/input16.txt").readLines().filter { it.isNotEmpty() }

    var walls = mutableSetOf<Point2d>()
    var startN: Point2d? = null
    var endN: Point2d? = null

    lines.forEachIndexed { y, line -> line.forEachIndexed { x, c ->
        val point = Point2d(x, y)
        when (c) {
            '#' -> walls.add(point)
            'S' -> startN = point
            'E' -> endN = point
        }
    }}
    val startPoint = checkNotNull(startN)
    val endPoint = checkNotNull(endN)
    val width = walls.maxOf { it.x } + 1
    val height = walls.maxOf { it.y } + 1
    println("Map: ${width}x${height}")
    println("Start: $startPoint")
    println("End: $endPoint")

    val start = PointWithDirection(startPoint, StraightDirections.RIGHT)
    val bestCost = mutableMapOf<PointWithDirection, Long>()
    bestCost[start] = 0
    val todo = mutableSetOf(start)

    while (todo.isNotEmpty()) {
        val (current, cost) = bestCost.filterKeys { todo.contains(it) }.minBy { (_, value) -> value }
        if (current.point == endPoint) {
            println("Found best path: $cost")
            break
        }

        todo.remove(current)

        val forward = PointWithDirection(current.point.add(current.direction), current.direction)
        if (!walls.contains(forward.point)) {
            val forwardCost = cost + 1
            if (forwardCost < bestCost.getOrDefault(forward, Long.MAX_VALUE)) {
                bestCost[forward] = forwardCost
                todo.add(forward)
            }
        }
        val turnCost = cost + 1000
        val right = PointWithDirection(current.point, incrementDirection(current.direction, 1))
        if (turnCost < bestCost.getOrDefault(right, Long.MAX_VALUE)) {
            bestCost[right] = turnCost
            todo.add(right)
        }

        val left = PointWithDirection(current.point, incrementDirection(current.direction, -1))
        if (turnCost < bestCost.getOrDefault(left, Long.MAX_VALUE)) {
            bestCost[left] = turnCost
            todo.add(left)
        }
    }

}

private data class PointWithDirection(val point: Point2d, val direction: Point2d)



private fun incrementDirection(direction: Point2d, increment: Int): Point2d {
    val index = OrderedDirections.ALL.indexOf(direction)
    var newIndex = (index + increment) % OrderedDirections.ALL.size
    if (newIndex < 0) {
        newIndex += OrderedDirections.ALL.size
    }
    return OrderedDirections.ALL[newIndex]
}
private data object OrderedDirections {
    val ALL = listOf(
        StraightDirections.UP,
        StraightDirections.RIGHT,
        StraightDirections.DOWN,
        StraightDirections.LEFT)
}