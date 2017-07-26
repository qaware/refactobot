package de.qaware.refactobot.extractor.text

import de.qaware.refactobot.extractor.ReferenceExtractor
import de.qaware.refactobot.extractor.general.FqcnExtractor
import de.qaware.refactobot.model.codebase.Codebase
import de.qaware.refactobot.model.codebase.FileType
import de.qaware.refactobot.model.operation.Location
import de.qaware.refactobot.model.reference.Reference
import de.qaware.refactobot.util.fileToClass
import de.qaware.refactobot.util.slashify

/**
 * Reference extractor for arbitrary text files, such as xml or xhtml.
 *
 * Only detects fully-qualified class names in the files, and flags them for replacement.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class TextReferenceExtractor : ReferenceExtractor {


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

                    // for each thing that is found that looks like a class...
                    FqcnExtractor.findFqcnReferences(file, filesByClassName::get, line, Location(lineNo, 0)) {
                        refs += it
                    }
                }

            }
        }

        return refs.toSet()
    }

}