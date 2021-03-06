package de.qaware.refactobot.model.operation

/**
 * Location in a text file, given by line and column indices.
 *
 * @property line the line (zero-based counting).
 * @property column the column (zero-based counting).
 */
data class Location (
        var line: Int,
        var column: Int
) : Comparable<Location> {

    /**
     * Compares to locations in the natural way.
     */
    override fun compareTo(other: Location): Int = compareValuesBy(this, other, Location::line, Location::column)

    companion object {

        /**
         * Creates a location, given one-based indices.
         *
         * @return a location, with indices normalized.
         */
        fun oneBased(line: Int, column: Int) = Location(line - 1, column - 1)
    }

    operator fun plus(n: Int): Location = Location(line, column + n)

    operator fun minus(n: Int): Location = Location(line, column - n)

}


