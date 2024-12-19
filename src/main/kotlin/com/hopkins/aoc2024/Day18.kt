package com.hopkins.aoc2024

import java.io.File
import kotlin.math.abs

fun main(args: Array<String>) {
    // Read the input file
    val lines: List<String> = File("input/input18.txt").readLines().filter { it.isNotEmpty() }

    // Parse the input into points
    val points: Set<Point2d> =
        lines.take(1024)
            .map { line ->
                var (x, y) = line.split(",")
                Point2d(x.toInt(), y.toInt())
            }
            .toSet()


    val width = 71
    val height = 71
    val start = Point2d.ORIGIN
    val end = Point2d(width - 1, height - 1)
    println("Map size: ${width}x${height}")

    println("Map:")
    for (y in 0 until height) {
        for (x in 0 until width) {
            val point = Point2d(x, y)
            if (points.contains(point)) {
                print("#")
            } else {
                print(".")
            }
        }
        println()
    }

    val bestPath: MutableMap<Point2d, Int> = mutableMapOf()
    bestPath[start] = 0
    val todo = mutableSetOf<Point2d>(start)
    while (todo.isNotEmpty()) {
        val (current, cost) = bestPath.filterKeys { todo.contains(it) }.minBy { (key, value) -> value + estimateDistance(key, end)}
        if (current == end) {
            println("Cost: $cost")
            break
        }
        todo.remove(current)

        for (direction in StraightDirections.ALL) {
            val next = current.add(direction)
            val nextCost = cost + 1
            if (isInBounds(next, width, height) && !points.contains(next)) {
                if (nextCost < bestPath.getOrDefault(next, Int.MAX_VALUE)) {
                    todo.add(next)
                    bestPath[next] = nextCost
                }
            }
        }
    }
}

private fun estimateDistance(point: Point2d, goal: Point2d) =
    abs(point.x - goal.x) + abs(point.y - goal.y)

private fun isInBounds(point: Point2d, width: Int, height: Int) =
    point.x >= 0 && point.y >= 0 && point.x < width && point.y < height