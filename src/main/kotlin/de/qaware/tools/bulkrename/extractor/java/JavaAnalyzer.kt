package de.qaware.tools.bulkrename.extractor.java

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.ImportDeclaration
import de.qaware.tools.bulkrename.extractor.java.visitors.*
import de.qaware.tools.bulkrename.model.codebase.File
import de.qaware.tools.bulkrename.model.operation.Location
import de.qaware.tools.bulkrename.model.reference.Reference
import de.qaware.tools.bulkrename.util.fileToClass
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
    fun extractReferences(file: File, inputStream: InputStream, filesByClass: Map<String, File>,
                          filesInSamePackage: List<File>): Set<Reference> {
        val compilationUnit = inputStream.use { JavaParser.parse(it) }

        if (compilationUnit.types.size != 1) {
            throw UnsupportedOperationException("Multiple types per compilation unit not supported, but found in " + file)
        }

        val importMap = compilationUnit.imports
                .map { analyzeImport(it) }
                .filterNotNull()
                .toMap()
                .mapValues { entry -> filesByClass[entry.value] }
                .filterValues { it != null }

        val implicitImportMap = filesInSamePackage
                .associateBy { fileToClass(it.fileName) }

        val context = (object : ReferenceExtractionContext {
            override fun getCurrentFile() = file
            override fun resolveFullName(fqcn: String) = filesByClass[fqcn]
            override fun resolveSimpleName(simpleName: String): File? =
                    when {
                        simpleName in importMap -> importMap[simpleName]
                        simpleName in implicitImportMap -> implicitImportMap[simpleName]
                        else -> null
            }
        })

        val visitors = listOf(ImportVisitor(context), ClassDeclarationVisitor(context), ClassReferenceVisitor(context), ConstructorDeclarationVisitor(context),
                PackageDeclarationVisitor(context), AnnotationVisitor(context))
        val references = visitors.flatMap { v -> v.extractReferences(compilationUnit) }.toSet()

        // check for references to implicitly imported classes
        val usedImplicitClasses =
                references.mapNotNull { ref -> if (ref is JavaSimpleTypeReference) ref.target else null }.toSet()
                        .intersect(filesInSamePackage)

        return references +
                JavaImplicitImportReference(file, usedImplicitClasses, getImplicitImportLocation(compilationUnit))
    }

    private fun getImplicitImportLocation(compilationUnit: CompilationUnit): Location {

        val line =
                if (compilationUnit.imports.isEmpty())
                    compilationUnit.`package`.begin.line + 1
                else
                    compilationUnit.imports[0].begin.line

        return Location(line - 1, 0) // our locations are zero-based
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

        return null
    }

}