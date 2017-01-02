package de.qaware.tools.bulkrename.executor

import de.qaware.tools.bulkrename.model.action.FileOperation
import de.qaware.tools.bulkrename.model.reference.Location
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
    """.trimMargin().split("\n")

    @Test
    fun testLineSource() {
        val source = LineSource(EXAMPLE_LINES)

        assertEquals(source.readUpTo(Location(0, 0)), "");
        assertEquals(source.readUpTo(Location(0, 6)), "Hello,");
        assertEquals(source.readUpTo(Location(3, 3)), " this is a piece of text\nthat spans multiple lines.\n\nThe");
        assertEquals(source.readRest(), " end.\n")
    }

    @Test
    fun testFileEditing() {

        val writer = StringWriter()

        EditProcessor(EXAMPLE_LINES, writer).applyEdits(listOf(
                FileOperation.Edit(Location(0, 17), Location(0, 17), "nice "),
                FileOperation.Edit(Location(1, 5), Location(3, 0), "was edited automatically. ")
        ))

        assertEquals(writer.toString(), """
        |Hello, this is a nice piece of text
        |that was edited automatically. The end.
        |""".trimMargin())
    }
}