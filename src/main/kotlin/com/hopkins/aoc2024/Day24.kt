package com.hopkins.aoc2024

import java.io.File

fun main(args: Array<String>) {
    // Read the input file
    val lines: List<String> = File("input/input24.txt").readLines().filter { it.isNotEmpty() }

    val initialNodes =
        lines
            .filter { it.contains(":") }
            .map { parseInitialNode(it) }

    val gates =
        lines
            .filter { it.contains("->") }
            .map { parseGate(it) }

    val nodes = mutableSetOf<Node>()
    nodes.addAll(initialNodes)
    gates.forEach { nodes.addAll(it.buildNodes()) }

    val nodeLookup = nodes.associateBy { it.name }
    val gatesToProcess = gates.toMutableSet()
    while (gatesToProcess.isNotEmpty()) {
        val gate = gatesToProcess.first { it.isReady(nodeLookup) }
        gatesToProcess.remove(gate)
        nodeLookup[gate.output]!!.value = gate.calculateValue(nodeLookup)
    }

    println("Nodes:")
    nodes.forEach { println(" $it") }

    val output =
        nodes
            .filter { it.name.startsWith("z") }
            .sortedByDescending { it.name }
            .map {
                if (it.value!!) {
                    "1"
                } else {
                    "0"
                }}
            .joinToString("")

    println("Output: $output")
    println("Output Number: ${output.toLong(2)}")
}

private fun parseGate(line: String): Gate {
    val (left, type, right, _, output) = line.split(" ")
    val gateType = GateType.valueOf(type)
    return Gate(left, right, gateType, output)
}

private fun parseInitialNode(line: String): Node {
    val (left, right) = line.split(": ")
    val node = Node(left)
    if (right.trim() == "1") {
        node.value = true
    } else {
        node.value = false
    }
    return node
}

private class Node(val name: String) {
    var value: Boolean? = null

    override fun equals(other: Any?): Boolean {
        if (other is Node) {
            return this.name == other.name
        }
        return false
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String =
        "Node {$name, $value}"
}

private data class Gate(val left: String, val right: String, val type: GateType, val output: String) {

    fun isReady(nodeLookup: Map<String, Node>): Boolean =
        nodeLookup[left]!!.value != null &&
                nodeLookup[right]!!.value != null

    fun calculateValue(nodeLookup: Map<String, Node>): Boolean {
        require(isReady(nodeLookup))
        val leftValue = nodeLookup[left]!!.value!!
        val rightValue = nodeLookup[right]!!.value!!

        return when (type) {
            GateType.AND -> leftValue && rightValue
            GateType.OR -> leftValue || rightValue
            GateType.XOR -> leftValue xor rightValue
        }
    }


    fun buildNodes(): List<Node> =
        listOf(Node(left), Node(right), Node(output))
}

private enum class GateType {
    AND, OR, XOR
}