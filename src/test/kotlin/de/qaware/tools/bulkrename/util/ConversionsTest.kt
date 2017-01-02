package de.qaware.tools.bulkrename.util

import org.junit.Test
import kotlin.test.asserter

/**
 * Test for conversion methods.

 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class ConversionsTest {

    val examples : List<Pair<String, String>> =
            listOf(
                    Pair("Foo", "Foo.java"),
                    Pair("a.b.c.D", "a/b/c/D.java")
            )


    @Test
    fun testConversions() {

        for ((className, fileName) in examples) {
            asserter.assertEquals("equal", classToFile(className), fileName)
            asserter.assertEquals("equal", fileToClass(fileName), className)
        }

    }

    @Test(expected = IllegalArgumentException::class)
    fun testInvalid() {
        fileToClass("hello.txt")
    }
}
