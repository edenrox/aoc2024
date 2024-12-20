package com.hopkins.aoc2024

import java.io.File
import kotlin.math.abs

fun main(args: Array<String>) {
    // Read the input file
    val lines: List<String> = File("input/input20.txt").readLines().filter { it.isNotEmpty() }

    // Parse the racetrack
    val raceTrack = parseRaceTrack(lines)
    println("Race Track:")
    println("- size: ${raceTrack.width}x${raceTrack.height}")

    // Find the best path
    val bestPath = findBestPath(raceTrack)
    require(bestPath.isNotEmpty())

    // Build an ordinal map
    val bestPathOrdinalMap = bestPath.mapIndexed { index, point -> point to index}.toMap()

    println("Best Path:")
    for (y in 0 until raceTrack.height) {
        for (x in 0 until raceTrack.width) {
            val point = Point2d(x, y)
            if (raceTrack.walls.contains(point)) {
                print("#")
            } else if (point == raceTrack.start) {
                print("S")
            } else if (point == raceTrack.end) {
                print("E")
            } else if (bestPathOrdinalMap.contains(point)) {
                print("O")
            } else {
                print(".")
            }
        }
        println()
    }

    // Iterate through every point in the best path
    println("Shortcuts:")
    val bestPathLength = bestPath.size
    val cheats = mutableListOf<Int>()
    for (current in bestPath) {
        val currentOrdinal = bestPathOrdinalMap[current]!!

        // For each point, if
        for (d1 in StraightDirections.ALL) {
            val next = current.add(d1)
            if (raceTrack.walls.contains(next)) {
                for (d2 in StraightDirections.ALL) {
                    val next2 = next.add(d2)
                    if (bestPathOrdinalMap.contains(next2)) {
                        val ordinalAtNext2 = bestPathOrdinalMap[next2]!!
                        if (ordinalAtNext2 > currentOrdinal + 2) {
                            val size = ordinalAtNext2 - currentOrdinal - 2
                            cheats.add(size)
                        }
                    }
                }
            }
        }
    }
    val cheatCountMap = cheats.groupingBy { it }.eachCount()
    var sum = 0
    for (size in cheatCountMap.keys.sorted()) {
        val count = cheatCountMap[size]!!
        println("$size = $count")
        if (size >= 100) {
            sum += count
        }
    }
    println("Result: $sum")
}

private fun findBestPath(track: RaceTrack): List<Point2d> {
    val todo = mutableSetOf(track.start)
    val bestPathMap = mutableMapOf<Point2d, List<Point2d>>()
    bestPathMap[track.start] = listOf(track.start)

    while (todo.isNotEmpty()) {
        val (current, bestPath) =
            todo
                .map { point -> point to bestPathMap[point]!! }
                .minBy { (point, path) -> path.size + track.estimateDistance(point) }
        todo.remove(current)
        if (current == track.end) {
            return bestPath
        }
        for (direction in StraightDirections.ALL) {
            val next = current.add(direction)
            if (!track.walls.contains(next)) {
                if (bestPath.size + 1 < (bestPathMap[next]?.size ?: Int.MAX_VALUE)) {
                    val nextPath = bestPath + next
                    bestPathMap[next] = nextPath
                    todo.add(next)
                }
            }
        }
    }
    return emptyList()
}

private fun parseRaceTrack(lines: List<String>): RaceTrack {
    var start: Point2d = Point2d.ORIGIN
    var end: Point2d = Point2d.ORIGIN
    val walls = lines.flatMapIndexed { y, line -> line.mapIndexed { x, c ->
        val point = Point2d(x, y)
        when (c) {
            '#' -> point
            'S' -> {
                start = point
                null
            }
            'E' -> {
                end = point
                null
            }
            else -> null
        }
        }}.filterNotNull().toSet()

    return RaceTrack(start, end, walls)
}

private data class RaceTrack(val start: Point2d, val end: Point2d, val walls: Set<Point2d>) {
    val width = walls.maxOf { it.x } + 1
    val height = walls.maxOf { it.y } + 1

    fun estimateDistance(point: Point2d): Int =
        abs(point.x - end.x) + abs(point.y - end.y)
}