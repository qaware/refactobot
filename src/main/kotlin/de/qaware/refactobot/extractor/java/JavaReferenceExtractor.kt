package de.qaware.refactobot.extractor.java

import de.qaware.refactobot.extractor.ReferenceExtractor
import de.qaware.refactobot.model.codebase.Codebase
import de.qaware.refactobot.model.codebase.File
import de.qaware.refactobot.model.codebase.FileType
import de.qaware.refactobot.model.reference.Reference
import de.qaware.refactobot.util.fileToClass
import de.qaware.refactobot.util.slashify
import java.io.FileInputStream

/**
 * A Java-aware implementation of the ReferenceExtractor.
 *
 * Collects all references from Java-files in a given codebase and returns
 * a map of files to their incoming references from other Java-files.
 */
class JavaReferenceExtractor : ReferenceExtractor {

    override fun extractReferences(codebase: Codebase): Set<Reference> {

        val javaFiles = codebase.allFiles.filter { it.type == FileType.JAVA }

        val filesByClassName =
                javaFiles.associateBy { file -> fileToClass(file.path.resolve(file.fileName).slashify()) }

        val filesBySimpleClassName =
                javaFiles.groupBy { file -> fileToClass(file.fileName) }

        val filesByPackage = javaFiles.groupBy(File::path)

        fun analyseFile(file: File): Set<Reference> {
            val inputStream = FileInputStream(codebase.rootPath.resolve(file.fullName).toFile())
            return JavaAnalyzer().extractReferences(file, inputStream, filesByClassName, filesBySimpleClassName,
                    filesByPackage[file.path]!!)
        }

        return javaFiles.flatMap { analyseFile(it) }.toSet()
    }

}

