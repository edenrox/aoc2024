package com.hopkins.aoc2024

import java.io.File

fun main(args: Array<String>) {
    // Read the input file
    val lines: List<String> = File("input/input12.txt").readLines().filter { it.isNotEmpty() }

    val width = lines[0].length
    val height = lines.size
    println("Map Size: ${width}x${height}")

    val pointCharLookup = lines.flatMapIndexed { y, line -> line.mapIndexed { x, c -> Point2d(x, y) to c }}.toMap()
    val todoList = pointCharLookup.keys.toMutableSet()
    val areas = mutableListOf<Area>()
    while (todoList.isNotEmpty()) {
        val startPoint = todoList.first()
        val connectedPoints = mutableSetOf<Point2d>()
        findConnectedPoints(startPoint, width, height, pointCharLookup, connectedPoints)
        areas.add(Area(connectedPoints.toList()))
        todoList.removeAll(connectedPoints)
    }

    println("Num Areas: ${areas.size}")
    areas.forEach { area ->
        val type = pointCharLookup[area.points.first()]!!
        println("Type: $type")
        println("Area: ${area.calculateArea()}")
        println("Perimeter: ${area.calculatePerimeter()}")
        println("Cost: $${area.calculateCost()}")
    }
    val totalCost = areas.sumOf { it.calculateCost() }
    println("Total Cost: $totalCost")
}

private fun findConnectedPoints(point: Point2d, width: Int, height: Int, map: Map<Point2d, Char>, resultSet: MutableSet<Point2d>) {
    resultSet.add(point)
    val c = map[point]
    for (direction in StraightDirections.ALL) {
        val next = point.add(direction)
        if (isInBounds(next, width, height) && !resultSet.contains(next) && map[next] == c) {
            findConnectedPoints(next, width, height, map, resultSet)
        }
    }
}

private fun isInBounds(point: Point2d, width: Int, height: Int): Boolean =
    point.x >= 0 && point.y >= 0 && point.x < width && point.y < height

private class Area(val points: List<Point2d>) {
    val pointLookup = points.toSet()

    fun calculateCost(): Int =
        calculateArea() * calculatePerimeter()

    fun calculateArea(): Int = pointLookup.size

    fun calculatePerimeter(): Int {
        var perimiter = 0
        return points.sumOf { point -> perimeter(point) }
    }

    private fun perimeter(point: Point2d): Int =
        StraightDirections.ALL.sumOf { direction ->
            if (pointLookup.contains(point.add(direction))) {
                0
            } else {
                1
            }.toInt() }
}