package com.hopkins.aoc2024

import java.io.File

fun main(args: Array<String>) {
    // Parse the input file into rules and print orders
    val lines: List<String> = File("input/input05.txt").readLines()
    val orderingRules = lines.filter { it.contains("|") }.map { parseRule(it) }
    val printOrders = lines.filter { it.contains(",") }.map { parsePrintOrder(it) }

    // Build a lookup so we can quickly find rules which apply to a given page
    val ruleLookup: Map<Int, List<Rule>> =
        orderingRules
            .flatMap { listOf( it.first to it, it.second to it) }
            .groupBy(keySelector = { it.first }, valueTransform = { it.second })

    // Debug
    println("Num rules: ${orderingRules.size}")
    println("Num orders: ${printOrders.size}")

    // Part 1
    val result1 =
        printOrders
            .filter { isValidPrintOrder(it, ruleLookup) }
            .sumOf { findMiddlePage(it) }
    println("Part 1 Result: $result1")

    // Part 2
    val result2 =
        printOrders
            .filterNot { isValidPrintOrder(it, ruleLookup) }
            .map { reorderPages(it, ruleLookup) }
            .sumOf { findMiddlePage(it) }
    println("Part 2 Result: $result2")
}

private fun reorderPages(pages: List<Int>, ruleLookup: Map<Int, List<Rule>>): List<Int> {
    val pageSet = pages.toMutableSet()
    val allRules = pages.flatMap { ruleLookup.getOrDefault(it, listOf()) }

    val pageOrder = mutableListOf<Int>()
    while (pageSet.isNotEmpty()) {
        // Filter ruleset to only those rules which apply to the pages we have yet to order
        val currentRules = allRules.filter { pageSet.contains(it.second) && pageSet.contains(it.first) }

        // The lowest item is the one which has no rules with that item as the second in a rule
        // Note: there could be multiple lowest items, but then they are equivalent and order between them doesn't matter
        val lowestItem = pageSet.first { page -> currentRules.none { rule -> rule.second == page }}

        pageSet.remove(lowestItem)
        pageOrder.add(lowestItem)
    }
    return pageOrder
}

private fun isValidPrintOrder(pages: List<Int>, ruleLookup: Map<Int, List<Rule>>): Boolean {
    val pageSet = pages.toSet()
    // Filter the rules to only those that apply to this list of pages
    val rules = pages
        .flatMap { ruleLookup.getOrDefault(it, listOf()) }
        .filter { pageSet.contains(it.first) && pageSet.contains(it.second) }

    // Build a map which given a page number tells us its index in the list
    val pageIndexLookup = pages.mapIndexed { index, page -> page to index}.toMap()

    // If all rules are satisfied (index of the first page < index of the second page), then this is a valid order
    val allRulesMatch =
        rules.all { pageIndexLookup[it.first]!! < pageIndexLookup[it.second]!! }

    return allRulesMatch
}

private fun findMiddlePage(pages: List<Int>): Int {
    check(pages.size % 2 == 1)
    return pages[pages.size / 2]
}

private fun parsePrintOrder(line: String): List<Int> =
    line.split(",").map { it.toInt() }


private fun parseRule(line: String): Rule =
    line.split("|").map { it.toInt() }.zipWithNext().map {(first, second) -> Rule(first, second) }.first()

private data class Rule(val first: Int, val second: Int)