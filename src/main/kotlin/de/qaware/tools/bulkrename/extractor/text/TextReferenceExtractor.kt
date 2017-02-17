package de.qaware.tools.bulkrename.extractor.text

import de.qaware.tools.bulkrename.extractor.ReferenceExtractor
import de.qaware.tools.bulkrename.extractor.java.JavaQualifiedTypeReference
import de.qaware.tools.bulkrename.model.codebase.Codebase
import de.qaware.tools.bulkrename.model.codebase.FileType
import de.qaware.tools.bulkrename.model.operation.Location
import de.qaware.tools.bulkrename.model.operation.Span
import de.qaware.tools.bulkrename.model.reference.Reference
import de.qaware.tools.bulkrename.util.fileToClass
import de.qaware.tools.bulkrename.util.slashify

/**
 * Reference extractor for arbitrary text files, such as xml or xhtml.
 *
 * Only detects fully-qualified class names in the files, and flags them for replacement.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class TextReferenceExtractor : ReferenceExtractor {

    /**
     * Matches occurrences of possible class names.
     *
     * We make a few assumptions:
     * - A fully-qualified class name contains at least one dot, since non-packaged classes are unsupported.</li>
     * - Class names are surrounded by non-alphanumeric characters</li>
     */
    private val PATTERN = Regex("([a-z]\\w+\\.)+[A-Z]\\w*")


    override fun extractReferences(codebase: Codebase): Set<Reference> {

        val textFiles = codebase.allFiles.filter { it.type == FileType.OTHER }
        val javaFiles = codebase.allFiles.filter { it.type == FileType.JAVA }

        val filesByClassName =
                javaFiles.associateBy { file -> fileToClass(file.path.resolve(file.fileName).slashify()) }

        val refs : MutableSet<Reference> = mutableSetOf()

        // for each file...
        for (file in textFiles) {

            // for each line in the file...
            codebase.rootPath.resolve(file.fullName).toFile().useLines {
                for ((lineNo, line) in it.withIndex()) {

                    // for each pattern that looks like a class...
                    for (match in PATTERN.findAll(line)) {

                        // if the class name matches a file in the codebase...
                        val fileEntry = filesByClassName[match.value]
                        if (fileEntry != null) {

                            // report it as a reference.
                            val span = Span(Location(lineNo, match.range.first), Location(lineNo, match.range.endInclusive + 1))
                            refs += JavaQualifiedTypeReference(file, fileEntry, span)
                        }
                    }
                }

            }
        }

        return refs.toSet()
    }


}