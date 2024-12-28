package com.hopkins.aoc2024

import java.io.File

fun main(args: Array<String>) {
    // Read the input file
    val lines: List<String> = File("input/input21.txt").readLines().filter { it.isNotEmpty() }

    val numPadInput = "789\n456\n123\n 0A"
    val numPadSingleMoves = findSingleMoves(numPadInput)
    println("Num Pad:")
    numPadSingleMoves.forEach { println(" $it") }
    println()
    println("Num Pad Best Moves:")
    val numPadBestMoves = findAllMoves(numPadSingleMoves)
    numPadBestMoves.forEach { println(" $it") }
    // Manually specify paths that might overlap the empty square
    numPadBestMoves[Move('0', '1')] = "^<"
    numPadBestMoves[Move('0', '4')] = "^^<"
    numPadBestMoves[Move('0', '7')] = "^^^<"
    numPadBestMoves[Move('A', '1')] = "^<<"
    numPadBestMoves[Move('A', '4')] = "^^<<"
    numPadBestMoves[Move('A', '7')] = "^^^<<"
    numPadBestMoves[Move('7', '0')] = ">vvv"
    numPadBestMoves[Move('7', 'A')] = ">>vvv"
    numPadBestMoves[Move('4', '0')] = ">vv"
    numPadBestMoves[Move('4', 'A')] = ">>vv"
    numPadBestMoves[Move('1', '0')] = ">v"
    numPadBestMoves[Move('1', 'A')] = ">>v"

    val dPadInput = " ^A\n<v>"
    val dPadSingleMoves = findSingleMoves(dPadInput)
    println("D-Pad:")
    dPadSingleMoves.forEach { println(" $it") }
    println("D-Pad Best Moves:")
    val dPadBestMoves = findAllMoves(dPadSingleMoves)
    // Manually specify paths that might overlap the empty square
    dPadBestMoves[Move('^', '<')] = "v<"
    dPadBestMoves[Move('<', '^')] = ">^"
    dPadBestMoves[Move('A', '<')] = "v<<"
    dPadBestMoves[Move('<', 'A')] = ">>^"
    dPadBestMoves.forEach { println(" $it") }


    var total = 0
    for (line in lines) {
        val lineValue = line.dropWhile { it == '0'}.takeWhile { it != 'A'}.toInt()
        println("Line: $line")
        println("Value: $lineValue")
        val code1 = "A$line".filterNot { it == ' '}.zipWithNext().map { (left, right) -> findMove(left, right, numPadBestMoves) }.joinToString(" ")
        println("Code 1: $code1")
        val code2 = "A$code1".filterNot { it == ' '}.zipWithNext().map { (left, right) -> findMove(left, right, dPadBestMoves) }.joinToString(" ")
        println("Code 2: $code2")
        val code3 = "A$code2".filterNot { it == ' '}.zipWithNext().map { (left, right) -> findMove(left, right, dPadBestMoves) }.joinToString("")
        println("Code 3: $code3")
        println("Math: ${code3.length} * $lineValue")
        total += code3.length * lineValue
    }
    println("Total: $total")
}

private fun findMove(left: Char, right: Char, map: Map<Move, String>): String {
    if (left == right) {
        return "A"
    }
    return map[Move(left, right)] + "A"
}

private val DIR_MAP = mapOf(
    StraightDirections.UP to '^',
    StraightDirections.DOWN to 'v',
    StraightDirections.LEFT to '<',
    StraightDirections.RIGHT to '>',
)

private fun getOppositeDirection(dir: Point2d): Point2d =
    when (dir) {
        StraightDirections.UP -> StraightDirections.DOWN
        StraightDirections.DOWN -> StraightDirections.UP
        StraightDirections.LEFT -> StraightDirections.RIGHT
        StraightDirections.RIGHT -> StraightDirections.LEFT
        else -> throw IllegalArgumentException("Unexpected: $dir")
    }

private fun findAllMoves(singleMoves: Map<Move, String>): MutableMap<Move, String> {
    val bestMoves = singleMoves.toMutableMap()

    // Iterate through moves that are up to 4 in length
    for (i in 1 until 5) {
        val newMoves = mutableMapOf<Move, String>()
        for ((currentMove, currentMoveString) in bestMoves) {
            if (currentMoveString.length == i) {
                for ((nextMove, nextMoveString) in singleMoves) {
                    if (currentMove.end == nextMove.start && currentMove.start != nextMove.end) {
                        val newMove = Move(currentMove.start, nextMove.end)
                        if (!bestMoves.containsKey(newMove)) {
                            newMoves[newMove] = currentMoveString + nextMoveString
                        }
                    }
                }
            }
        }
        bestMoves += newMoves
    }
    // Sort the moves to avoid switching back and forth between buttons (<<^ vs. <^<)
    return bestMoves.mapValues { sortButtons(it.value) }.toMutableMap()
}

private fun sortButtons(input: String): String {
    return input.map { it }.sortedBy {
        when (it) {
            'A' -> 4
            '>' -> 3
            '^' -> 2
            'v' -> 1
            '<' -> 0
            else -> throw IllegalArgumentException("Unexpected: $it")
        }
    }.joinToString("")
}

private fun findSingleMoves(input: String): Map<Move, String> {
    val lines = input.split("\n")
    val width = lines[0].length
    val height = lines.size
    val map = lines.flatMapIndexed { y, line -> line.mapIndexed { x, c ->
        val point = Point2d(x, y)
        if (c == ' ') {
            null
        } else {
            point to c
        }
    }}.filterNotNull().toMap()

    val moves = mutableMapOf<Move, String>()
    for (start in map.keys) {
        for (direction in StraightDirections.ALL) {
            val end = start.add(direction)
            if (isInBounds(end, width, height) && map.containsKey(end)) {
                val move = Move(map[start]!!, map[end]!!)
                moves[move] = DIR_MAP[direction]!!.toString()
            }
        }
    }

    return moves
}

private fun isInBounds(point: Point2d, width: Int, height: Int): Boolean =
    point.x >= 0 && point.y >= 0 && point.x < width && point.y < height

private data class Move(val start: Char, val end: Char)

