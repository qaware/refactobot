package de.qaware.tools.bulkrename.extractor

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.ImportDeclaration
import de.qaware.tools.bulkrename.extractor.visitors.ClassDeclarationVisitor
import de.qaware.tools.bulkrename.extractor.visitors.ClassReferenceVisitor
import de.qaware.tools.bulkrename.extractor.visitors.ImportVisitor
import de.qaware.tools.bulkrename.model.codebase.File
import de.qaware.tools.bulkrename.model.reference.Reference
import java.io.InputStream

/**
 * TODO describe type.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class JavaAnalyzer {

    /**
     * Extracts all outgoing references from the given file, which are preset in the knownFilesByEntity-map.
     *
     * @param file the file to extract the references from
     * @param inputStream the file content
     * @param filesByClass a map of each known entity in the codebase to its file
     * @return a set of files which are referenced by the given file.
     */
    fun extractReferences(file: File, inputStream: InputStream, filesByClass: Map<String, File>): Set<Reference> {
        val compilationUnit = inputStream.use { JavaParser.parse(it) }

        if (compilationUnit.types.size != 1) {
            throw UnsupportedOperationException("Multiple types per compilation unit not supported, but found in " + file)
        }

        val importMap = compilationUnit.imports
                .map { analyzeImport(it) }
                .filterNotNull()
                .toMap()

        val context = (object : ReferenceExtractionContext {
            override fun getCurrentFile() = file
            override fun getFileForClass(fqcn: String) = filesByClass[fqcn]
            override fun getFileForImportedClass(simpleName: String): File? {
                val fqcn = importMap[simpleName]
                return if (fqcn != null) filesByClass[fqcn] else null
            }
        })

        val visitors = listOf(ImportVisitor(context), ClassDeclarationVisitor(context), ClassReferenceVisitor(context))
        return visitors.flatMap { v -> v.extractReferences(compilationUnit) }.toSet()
    }


    private fun analyzeImport(importDeclaration: ImportDeclaration): Pair<String, String>? {

        val name = importDeclaration.name.toString()
        val nameParts = name.split(".")

        // assume the usual capitalization conventions, to be able to tell which one is the package and which one is the file.

        if (nameParts.dropLast(1).all { it[0].isLowerCase() }) {
            // only the last part starts with an upper case letter, so by the usual naming conventions, we know that this
            // imports a top-level declaration of a compilation unit.

            return Pair(nameParts.last(), name)
        }

        return null;
    }

}