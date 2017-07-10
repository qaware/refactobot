package de.qaware.repackager.util

/**
 * Simple function to split a list into chunks of a given size.
 *
 * Replace by https://github.com/Kotlin/KEEP/blob/master/proposals/stdlib/window-sliding.md when this becomes available.
 */
fun <A> partition(bigList: List<A>, chunkSize: Int): List<List<A>> =
        bigList.withIndex()
                .groupBy { it.index / chunkSize }
                .map { it.value.map { it.value }}
