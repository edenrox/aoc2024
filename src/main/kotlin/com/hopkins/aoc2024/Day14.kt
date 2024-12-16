package com.hopkins.aoc2024

import java.io.File

fun main(args: Array<String>) {
    // Read the input file
    val lines: List<String> = File("input/input14.txt").readLines().filter { it.isNotEmpty() }

    val size = parseSize(lines[0])
    val robots = lines.drop(1).map { line -> parseRobotLine(line) }

    val robotsAfter100 =
        robots.map { robot ->
            val newPosition = robot.position.add(robot.velocity.multiply(100))
            val afterMod = Point2d(newPosition.x % size.x, newPosition.y % size.y).add(size)
            Point2d(afterMod.x % size.x, afterMod.y % size.y)
        }

    val pointLookup =
        robotsAfter100.groupingBy { it }.eachCount()

    println("Positions:")
    for (y in 0 until size.y) {
        for (x in 0 until size.x) {
            print(pointLookup.getOrDefault(Point2d(x, y), "."))
        }
        println()
    }

    var quadCounts = IntArray(4)
    val halfSize = Point2d(size.x/2, size.y/2)
    robotsAfter100.forEach { point ->
        if (point.x < halfSize.x) {
            if (point.y < halfSize.y) {
                quadCounts[0]++
            } else if (point.y > halfSize.y) {
                quadCounts[2]++
            }
        } else if (point.x > halfSize.x) {
            if (point.y < halfSize.y) {
                quadCounts[1]++
            } else if (point.y > halfSize.y) {
                quadCounts[3]++
            }
        }
    }
    println("Quad counts: ${quadCounts.joinToString(", ")}")
    val result = quadCounts[0].toLong() * quadCounts[1] * quadCounts[2] * quadCounts[3]
    println("Result: $result")

}

private fun parseSize(line: String): Point2d {
    check(line.startsWith("s="))
    return parsePoint(line.drop(2).trim())
}

private fun parseRobotLine(line: String): Robot {
    check(line.startsWith("p="))
    val (left, right) = line.drop(2).trim().split(" v=")

    val position = parsePoint(left)
    val velocity = parsePoint(right)

    return Robot(position, velocity)
}

private fun parsePoint(input: String): Point2d {
    val (left, right) = input.split(",")
    return Point2d(left.trim().toInt(), right.trim().toInt())
}

private data class Robot(val position: Point2d, val velocity: Point2d)
