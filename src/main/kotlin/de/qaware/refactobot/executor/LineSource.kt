package de.qaware.refactobot.executor

import de.qaware.refactobot.model.operation.Location


/**
 * Helper class that can be used to consume a list of lines of text in pieces.
 */
class LineSource(private val lines : List<String>) {

    private var currentPosition: Location = Location(0, 0)

    fun readUpTo(newPosition : Location) : String {

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

    fun readRest() = readUpTo(Location(lines.size, 0))
}