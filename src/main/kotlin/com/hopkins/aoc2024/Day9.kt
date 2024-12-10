package com.hopkins.aoc2024

import java.io.File

fun main(args: Array<String>) {
    // Read the input file
    val line: String = File("input/input09.txt").readLines().first { it.isNotEmpty() }

    // Part 1
    val blockSizes = line.map { c -> "$c".toInt() }
    val numBlocks = blockSizes.count()
    println("Num blocks: $numBlocks")

    val blockList = blockSizes.flatMapIndexed { index, size -> generateBlock(index, size) }

    //println("Blocks: ")
    //blockList.forEach { block -> print(if (block == -1) "." else block)}
    println()
    val numNotFree = blockList.filterNot { it == -1}.count()
    println("Not free: $numNotFree")

    val blockListReversedNoFree = blockList.reversed().filterNot { it == -1}
    val packed = mutableListOf<Int>()
    var left = 0
    var right = 0
    for (i in blockListReversedNoFree.indices) {
        if (isFree(blockList[left])) {
            packed.add(blockListReversedNoFree[right++])
        } else {
            packed.add(blockList[left])
        }
        left++
    }
    val checkSum = packed.mapIndexed { index, value -> index.toLong() * value.toLong() }.sum()
    println("Check Sum: $checkSum")

    // Part 2:
    val blockObjects =
        blockSizes
            .mapIndexed { index, size -> newBlock(index, size,
                if (index + 1 < blockSizes.size) {blockSizes[index + 1]} else { 0} )}
            .filterIndexed { index, _ -> index % 2 == 0 }
    println("Block Objects")
    //println(blockObjects.map { block -> block.dump()})

    val defraggedBlocks = blockObjects.toMutableList()
    for (blockId in blockObjects.reversed().map { it.id }) {
        println("Moving block: $blockId")
        val blockToMove = defraggedBlocks.first { it.id == blockId }
        val blockToMoveIndex = defraggedBlocks.indexOf(blockToMove)
        for (destBlock in defraggedBlocks.subList(0, blockToMoveIndex)) {
            if (destBlock.freeSpaceSize >= blockToMove.size) {
                // Move the block
                val destBlockIndex = defraggedBlocks.indexOf(destBlock)
                defraggedBlocks.remove(blockToMove)
                defraggedBlocks[blockToMoveIndex - 1].freeSpaceSize += blockToMove.size + blockToMove.freeSpaceSize

                val freeSpace = destBlock.freeSpaceSize - blockToMove.size
                destBlock.freeSpaceSize = 0
                blockToMove.freeSpaceSize = freeSpace

                defraggedBlocks.add(destBlockIndex + 1, blockToMove)

                if (defraggedBlocks.last().freeSpaceSize == 0) {
                    throw IllegalStateException("unexpected empty freespace at end")
                }
                break
            }
        }
    }

    val totalSpaces = blockSizes.sum()
    println("Spaces: $totalSpaces")

    val totalSpacesDefragged = defraggedBlocks.sumOf { it.size + it.freeSpaceSize }
    println("Defragged spaces: $totalSpacesDefragged")
    println("Defragged Block Objects")
    defraggedBlocks.forEach{ block -> println(block.dump()) }

    var offset = 0
    var checksum2 = 0L
    for(block in defraggedBlocks) {
        checksum2 += calculateBlockValue(offset, block)
        offset += block.size + block.freeSpaceSize
    }

    println("Part 2 Check sum: $checksum2")
}

private fun calculateBlockValue(offset: Int, block: Block): Long {
    var arg1 = 0
    for (i in 0 until block.size) {
        arg1 += offset + i
    }
    return arg1 * block.id.toLong()
}

private fun newBlock(index: Int, size: Int, freeSpaceSize: Int): Block {
    return Block(index / 2, size, freeSpaceSize)
}

class Block(val id: Int, val size: Int, var freeSpaceSize: Int) {
    fun dump(): String =
        id.toString().repeat(size) + ".".repeat(freeSpaceSize)

    override fun equals(other: Any?): Boolean {
        if (other is Block) {
            return other.id == id
        }
        return false
    }
}

private fun isFree(block: Int): Boolean =
    block == -1

private fun generateBlock(index: Int, size: Int): List<Int> {
    val id =
    if (index % 2 == 1) {
        // Free space
        -1
    } else {
        // Disk block for ID
        index / 2
    }
    return (0 until size).map { id }
}
