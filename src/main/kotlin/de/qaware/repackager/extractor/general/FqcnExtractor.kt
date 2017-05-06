package de.qaware.repackager.extractor.general

import de.qaware.repackager.extractor.java.JavaQualifiedTypeReference
import de.qaware.repackager.model.codebase.File
import de.qaware.repackager.model.operation.Location
import de.qaware.repackager.model.operation.Span
import de.qaware.repackager.model.reference.Reference
import org.apache.commons.lang3.StringUtils

/**
 * Util methods for extracting fully qualified class names in text (e.g., xml files or string literals)
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
object FqcnExtractor {

    /**
     * Matches occurrences of possible class names.
     *
     * We make a few assumptions:
     * - A fully-qualified class name contains at least one dot, since non-packaged classes are unsupported.</li>
     * - Class names are surrounded by non-alphanumeric characters</li>
     */
    private val PATTERN = Regex("([a-z]\\w+\\.)+[A-Z]\\w*")

    /**
     * Emits references for any occurrence of a fqcn in the given string.
     *
     * @param file the current file
     * @param filesByClassName a map to look up files by class name
     * @param str the string where reference should be found
     * @param startLocation the location of the string in the current file
     * @param emit the emit function that handles results
     */
    fun findFqcnReferences(file: File, filesByClassName: (String) -> File?, str: String, startLocation: Location, emit: (Reference) -> Unit) {
        for (match in PATTERN.findAll(str)) {

            // if the class name matches a file in the codebase...
            val fileEntry = filesByClassName(match.value)
            if (fileEntry != null) {

                // report it as a reference.
                emit(JavaQualifiedTypeReference(file, fileEntry,
                        Span(relativeLocation(str, startLocation, match.range.first),
                                relativeLocation(str, startLocation, match.range.endInclusive + 1))))
            }
        }
    }

    private fun relativeLocation(str: String, startLocation: Location, offset: Int): Location {

        val before = str.substring(0, offset)
        val lineOffset = StringUtils.countMatches(before, '\n')

        if (lineOffset > 0) {

            val newCol = offset - before.indexOfLast { it == '\n' } - 1
            return Location(startLocation.line + lineOffset, newCol)

        } else {
            return startLocation + offset
        }
    }


}