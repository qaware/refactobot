package de.qaware.tools.bulkrename.model.operation

/**
 * A textual span, consisting of a start and an end location.
 *
 * The start location is inclusive, and the end is exclusive, such that
 *
 * Span(X, Y) + Span(Y, Z) = Span(X, Z)
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 *
 * @property start the start of the span
 * @property end the end of the span
 */
data class Span(val start: Location, val end: Location) {

    fun shortenBy(n: Int) = Span(start, end = this.end - n)


}