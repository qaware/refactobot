package de.qaware.refactobot.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Test for partition function.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class PartitionTest {

    private fun String.parts() : List<String> = this.split(" ")

    @Test
    fun testSplitting() {

        // splitting
        assertThat(partition("a b c d e f g".parts(), 3))
                .isEqualTo(listOf("a b c".parts(), "d e f".parts(), listOf("g")))
    }

    @Test
    fun testEmpty() {
        val emptyList: List<Int> = emptyList()
        assertThat(partition(emptyList, 3)).isEqualTo(emptyList)
    }

    @Test
    fun testJustOnePart() {
        assertThat(partition("a b c d".parts(), 6)).isEqualTo(listOf("a b c d".parts()))
    }


}