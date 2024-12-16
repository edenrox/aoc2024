package com.hopkins.aoc2024

import java.io.File

fun main(args: Array<String>) {
    // Read the input file
    val lines: List<String> = File("input/input15.txt").readLines().filter { it.isNotEmpty() }

    // Parse the map
    val walls = mutableSetOf<Point2d>()
    val boxes = mutableSetOf<Point2d>()
    var robot: Point2d = Point2d.ORIGIN
        lines.filter { it.startsWith("#") }.forEachIndexed { y, line -> line.forEachIndexed { x, c ->
            val point = Point2d(x, y)
            when (c) {
                '#' -> walls.add(point)
                '@' -> robot = point
                'O' -> boxes.add(point)
            }}}
    check(robot != Point2d.ORIGIN)
    val width = walls.maxOf { it.x } + 1
    val height = walls.maxOf { it.y } + 1

    println("Map size: ${width}x${height}")
    println("Robot start: $robot")

    val directions =
        lines.filterNot { it.startsWith("#") }.flatMap { line -> line.map { parseDirection(it)} }

    println("Num directions: ${directions.count()}")
    println("Moves:")
    for (direction in directions) {
        if (canMove(robot, direction, walls, boxes)) {
            moveBoxes(robot.add(direction), direction, boxes)
            robot = robot.add(direction)
            println(robot)
        }
    }

    println("Map:")
    for (y in 0 until height) {
        for (x in 0 until width) {
            val position = Point2d(x, y)
            if (robot == position) {
                print("@")
            } else if (walls.contains(position)) {
                print("#")
            } else if (boxes.contains(position)) {
                print("O")
            } else {
                print(".")
            }
        }
        println()
    }

    val boxSum = boxes.sumOf { box -> box.y * 100 + box.x}
    println("Box Sum: $boxSum")
}

private fun canMove(position: Point2d, direction: Point2d, walls: Set<Point2d>, boxes: Set<Point2d>): Boolean {
    val nextPosition = position.add(direction)
    return if (walls.contains(nextPosition)) {
        false
    } else if (!boxes.contains(nextPosition)) {
        true
    } else {
        canMove(nextPosition, direction, walls, boxes)
    }
}

private fun moveBoxes(position: Point2d, direction: Point2d, boxes: MutableSet<Point2d>) {
    if (!boxes.contains(position)) {
        // No boxes to move
        return
    }
    var lastPosition = position
    while (boxes.contains(lastPosition)) {
        lastPosition = lastPosition.add(direction)
    }
    boxes.remove(position)
    boxes.add(lastPosition)
}

private fun parseDirection(c: Char): Point2d =
    when (c) {
        '^' -> StraightDirections.UP
        '<' -> StraightDirections.LEFT
        '>' -> StraightDirections.RIGHT
        'v' -> StraightDirections.DOWN
        else -> throw IllegalArgumentException("Unexpected char: $c")
    }
