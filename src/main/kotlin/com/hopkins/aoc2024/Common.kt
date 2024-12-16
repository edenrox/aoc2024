package com.hopkins.aoc2024

import kotlin.math.abs

data class Point2d(val x: Int, val y: Int) {
    fun add(other: Point2d): Point2d =
        Point2d(x + other.x, y + other.y)

    fun multiply(magnitude: Int) =
        Point2d(x * magnitude, y * magnitude)

    fun subtract(other: Point2d): Point2d =
        Point2d(x - other.x, y - other.y)

    fun absolute(): Point2d =
        Point2d(abs(x), abs(y))

    companion object {
        val ORIGIN = Point2d(0, 0)
    }
}

object StraightDirections {
    val UP = Point2d(0, -1)
    val DOWN = Point2d(0, 1)
    val LEFT = Point2d(-1, 0)
    val RIGHT = Point2d(1, 0)

    val ALL = listOf(UP, DOWN, LEFT, RIGHT)
}