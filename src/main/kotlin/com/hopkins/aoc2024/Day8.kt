package com.hopkins.aoc2024

import java.io.File

fun main(args: Array<String>) {
    // Read the input file
    val lines: List<String> = File("input/input08.txt").readLines().filter { it.isNotEmpty() }
    val width = lines[0].length
    val height = lines.size
    println("Map size: ${width}x${height}")

    // Convert the input into a set of [Station] objects
    val stations = lines.flatMapIndexed { y, line -> line.mapIndexed {x, c -> Station(Point2d(x, y), c)}}.filterNot { it.frequency == '.'}

    // Give each station an index so that we can print them out in a less verbose way
    var index = 0
    val stationIndex = stations.associateWith { index++ }


    // Group the stations into List<Station> that have the same frequency
    val stationGroups = stations.groupBy { it.frequency }
    val groupSizes = stationGroups.mapValues { (_, value) -> value.size}
    println("Group Sizes: $groupSizes")

    // Find the pairs of stations in each group
    val pairs = stationGroups.flatMap { generatePairs(it.value) }

    // Part 1 - Figure out the location of the anti-nodes by adding the distance to each point
    val pointSet =
        pairs
            .map { (s1, s2) -> s1.position to s2.position}
            .flatMap { (p1, p2) ->
                listOf(
                    p1.add(p1.subtract(p2)),
                    p2.add(p2.subtract(p1))
                ) }
            .filter { isWithinBounds(it, width, height) }
            .toSet()
    println("Part 1 - Num points: ${pointSet.size}")

    // Part 2
    val pointSet2 = mutableSetOf<Point2d>()
        pairs
            .map { (s1, s2) -> s1.position to s2.position}
            .forEach { (p1, p2) -> pointSet2.addAll(generateAllPoints(p1, p2, width, height)) }
    println("Part 2 - Num points: ${pointSet2.size}")

    for (y in 0 until height) {
        for (x in 0 until width) {
            val current = Point2d(x, y)
            if (pointSet2.contains(current)) {
                print("#")
            } else {
                print(".")
            }
        }
        println()
    }
}

private fun generateAllPoints(p1: Point2d, p2: Point2d, width: Int, height: Int): List<Point2d> {
    val distance = p2.subtract(p1)
    var start = p1
    while (true) {
        val next = start.subtract(distance)
        if (isWithinBounds(next, width, height)) {
            start = next
        } else {
            break
        }
    }
    return buildList {
        while (isWithinBounds(start, width, height)) {
            add(start)
            start = start.add(distance)
        }
    }
}

private fun isWithinBounds(point: Point2d, width: Int, height: Int): Boolean =
    point.x >= 0 && point.y >= 0 && point.x < width && point.y < height

private fun generatePairs(stations: List<Station>): List<Pair<Station, Station>> {
    check(stations.size >= 2)
    val first = stations.first()
    if (stations.size == 2) {
        val second = stations[1]
        return listOf(first to second)
    }

    val rest = stations.subList(1, stations.size)
    return rest.map { first to it } +
        generatePairs(rest)
}

private data class Station(val position : Point2d, val frequency: Char)

