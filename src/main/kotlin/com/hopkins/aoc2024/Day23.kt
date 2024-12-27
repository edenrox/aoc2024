package com.hopkins.aoc2024

import java.io.File

fun main(args: Array<String>) {
    // Read the input file
    val lines: List<String> = File("input/input23.txt").readLines().filter { it.isNotEmpty() }

    val edges = lines.map {
        val (left, right) = it.split("-")
        Edge(left, right)
    }
    val nodes = edges.flatMap { listOf(it.left, it.right) }.toSet()
    val edgeLookup =
        edges.flatMap {
            listOf(
                it.left to it.right,
                it.right to it.left
            )
        }.groupBy({ it.first }, { it.second })

    println("Nodes: ${nodes.size}")
    println("Edges: ${edges.size}")

    val todo = nodes.toMutableSet()
    val output = mutableSetOf<Set<String>>()
    val outputOnlyTs = mutableSetOf<Set<String>>()
    while (todo.isNotEmpty()) {
        val current = todo.first()
        todo.remove(current)

        val connectedTodo = edgeLookup[current]!!.toMutableSet()
        if (connectedTodo.size >= 2) {
            for (next in connectedTodo) {
                val nextConnected = edgeLookup[next]!!.toMutableSet()
                nextConnected.remove(current)
                for (next2 in nextConnected) {
                    if (edgeLookup[next2]!!.contains(current)) {
                        val set = setOf(current, next, next2)
                        output.add(set)
                        if (set.any { it.startsWith("t") }) {
                            outputOnlyTs.add(set)
                        }
                    }
                }
            }
        }
    }

    println("Output: ")
    printOutput(output)
    println("Output Only Ts:")
    printOutput(outputOnlyTs)
    println("Result: ${outputOnlyTs.size}")
}

private fun printOutput(output: Set<Set<String>>) {
    output.map { it.sorted().joinToString("-")}.forEach { println(" $it")}
}



private data class Edge(val left: String, val right: String)