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
}