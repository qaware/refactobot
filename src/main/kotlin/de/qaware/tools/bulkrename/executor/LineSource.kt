package de.qaware.tools.bulkrename.executor

import de.qaware.tools.bulkrename.model.reference.Location


/**
 * Helper class that can be used to consume a list of lines of text in pieces.
 */
class LineSource(private val lines : List<String>) {

    private val NEWLINE : String = "\n"

    private var currentPosition: Location = Location(0, 0)

    fun readUpTo(newPosition : Location) : String {

        val sb = StringBuffer()

        if (currentPosition.line > newPosition.line) {
            throw IllegalStateException("Cannot move backwards.")
        } else if (currentPosition.line < newPosition.line) {

            // rest of current line
            sb.append(lines[currentPosition.line].substring(currentPosition.column)).append(NEWLINE)

            // any lines in between
            for (i in IntRange(currentPosition.line + 1, newPosition.line - 1)) {
                sb.append(lines[i]).append(NEWLINE)
            }

            currentPosition = Location(newPosition.line, 0)
        }

        if (newPosition.column > 0) {
            sb.append(lines[currentPosition.line].substring(0, newPosition.column))
        }

        currentPosition = newPosition

        return sb.toString()
    }

    fun readRest() = readUpTo(Location(lines.size, 0))
}