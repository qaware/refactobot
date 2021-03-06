package de.qaware.refactobot.executor

import de.qaware.refactobot.model.operation.FileOperation
import de.qaware.refactobot.model.operation.Location
import de.qaware.refactobot.model.operation.Span
import de.qaware.refactobot.util.splitLines
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.StringWriter
import kotlin.test.assertEquals

/**
 * Test for LineSource and EditProcessor.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class EditProcessorTest {

    val EXAMPLE_LINES = """
    |Hello, this is a piece of text
    |that spans multiple lines.
    |
    |The end.
    """.trimMargin().splitLines()

    @Test
    fun testLineSource() {
        val source = EditProcessor.LineSource(EXAMPLE_LINES)

        assertEquals(source.readUpTo(Location(0, 0)), "")
        assertEquals(source.readUpTo(Location(0, 6)), "Hello,")
        assertEquals(source.readUpTo(Location(3, 3)), " this is a piece of text\nthat spans multiple lines.\n\nThe")
        assertEquals(source.readRest(), " end.")
    }

    @Test
    fun testFileEditing() {

        val writer = StringWriter()

        EditProcessor.applyEdits(EXAMPLE_LINES, writer, listOf(
                FileOperation.Edit(Span(Location(0, 17), Location(0, 17)), "nice "),
                FileOperation.Edit(Span(Location(1, 5), Location(3, 0)), "was edited automatically. ")
        ))

        assertEquals(writer.toString(), """
        |Hello, this is a nice piece of text
        |that was edited automatically. The end.""".trimMargin())
    }

    @Test
    fun testMultipleEditsOnALine() {

        val writer = StringWriter()
        EditProcessor.applyEdits(listOf("We need apples and oranges."), writer, listOf(
                FileOperation.Edit(Span(Location(0, 8), Location(0, 14)), "lambdas"),
                FileOperation.Edit(Span(Location(0, 19), Location(0, 26)), "recursion")
        ))

        assertThat(writer.toString()).isEqualTo("We need lambdas and recursion.")
    }
}