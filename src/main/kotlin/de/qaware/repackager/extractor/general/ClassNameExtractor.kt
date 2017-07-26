package de.qaware.repackager.extractor.general

import de.qaware.repackager.extractor.java.JavaSimpleTypeReference
import de.qaware.repackager.model.codebase.File
import de.qaware.repackager.model.operation.Location
import de.qaware.repackager.model.operation.Span
import de.qaware.repackager.model.reference.Reference

/**
 * Util methods for extracting simple class names in text (e.g., xml files or string literals)
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
object ClassNameExtractor {

    /**
     * Matches occurrences of possible class names.
     *
     * We make a few assumptions:
     * - A fully-qualified class name contains at least one dot, since non-packaged classes are unsupported.</li>
     * - Class names are surrounded by non-alphanumeric characters</li>
     */
    private val CLASS_NAME_PATTERN = Regex("(?<![\\w\\.])[A-Z]\\w*(?![\\w\\.])")

    /**
     * Emits references for any occurrence of a fqcn in the given string.
     *
     * @param file the current file
     * @param filesByClassName a map to look up files by simple class name
     * @param str the string where reference should be found
     * @param startLocation the location of the string in the current file
     * @param emit the emit function that handles results
     */
    fun findSimpleClassReference(file: File, filesByClassName: (String) -> File?, str: String, startLocation: Location, emit: (Reference) -> Unit) {
        for (match in CLASS_NAME_PATTERN.findAll(str)) {

            // if the class name matches a file in the codebase...
            val fileEntry = filesByClassName(match.value)
            if (fileEntry != null) {

                // report it as a reference.
                emit(JavaSimpleTypeReference(file, fileEntry,
                        Span(startLocation + match.range.first, startLocation + match.range.endInclusive + 1)))
            }
        }
    }

}