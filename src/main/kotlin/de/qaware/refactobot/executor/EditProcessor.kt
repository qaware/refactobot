package de.qaware.refactobot.executor

import de.qaware.refactobot.model.operation.FileOperation
import de.qaware.refactobot.model.operation.Location
import java.io.Writer

/**
 * Code to apply edit operations on files and strings.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
object EditProcessor {

    /**
     * Applies a list of edit operations to the given lines of text, writing the result to the given writer.
     *
     * @param input the input text.
     * @param output the writer to use for the output text.
     * @param edits the list of edits.
     */
    fun applyEdits(input: List<String>, output: Writer, edits: List<FileOperation.Edit>) {

        val source = LineSource(input)

        for ((span, replacementString) in edits.sortedBy { it.span.start }) {

            // copy over any material before the edit location
            output.write(source.readUpTo(span.start))

            // discard the text up to the end location, and write out the replacement instead
            source.readUpTo(span.end)
            output.write(replacementString)
        }
        output.write(source.readRest())
        output.close()
    }

    /**
     * Helper class that can be used to consume a list of lines of text in pieces.
     *
     * Maintains a 'cursor position' internally, from which subsequent text can be read, updating the cursor position.
     *
     * @property lines the lines of text to operate on
     */
    class LineSource(private val lines: List<String>) {

        private var currentPosition: Location = Location(0, 0)

        /**
         * Returns the text from the current position up to the given new position, and updates the current position
         * accordingly.
         *
         * @param newPosition the position up to which text should be consumed.
         * @return the text from the current position to the new position.
         */
        fun readUpTo(newPosition: Location): String {

            val sb = StringBuffer()

            if (currentPosition.line > newPosition.line ||
                    currentPosition.line == newPosition.line && currentPosition.column > newPosition.column) {
                throw IllegalStateException("Cannot move backwards.")
            } else if (currentPosition.line < newPosition.line) {

                // rest of current line
                sb.append(lines[currentPosition.line].substring(currentPosition.column))

                // any lines in between
                for (i in IntRange(currentPosition.line + 1, newPosition.line - 1)) {
                    sb.append(lines[i])
                }

                currentPosition = Location(newPosition.line, 0)
            }

            if (newPosition.column > 0) {
                sb.append(lines[currentPosition.line].substring(currentPosition.column, newPosition.column))
            }

            currentPosition = newPosition

            return sb.toString()
        }

        /**
         * Consumes all remaining text.
         *
         * @return the remaining text.
         */
        fun readRest() = readUpTo(Location(lines.size, 0))
    }
}

