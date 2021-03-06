package de.qaware.refactobot.extractor.java

import com.github.javaparser.JavaParser
import com.github.javaparser.ParseException
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.ImportDeclaration
import de.qaware.refactobot.extractor.java.visitors.*
import de.qaware.refactobot.model.codebase.File
import de.qaware.refactobot.model.operation.Location
import de.qaware.refactobot.model.reference.Reference
import de.qaware.refactobot.util.fileToClass
import java.io.InputStream

/**
 * Analysis of java files. Contains certain special treatment of import statements, and the coordinates a bunch of
 * visitors that do the actual extraction work.
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
    fun extractReferences(file: File, inputStream: InputStream,
                          filesByClass: Map<String, File>,
                          filesBySimpleClassName: Map<String, List<File>>,
                          filesInSamePackage: List<File>): Set<Reference> {
        val compilationUnit =
                try { inputStream.use { JavaParser.parse(it) } }
                catch (e : ParseException) {
                    throw IllegalStateException("Error while parsing " + file.fullName, e)
                }

        if (compilationUnit.types.size != 1) {
            throw UnsupportedOperationException("Multiple types per compilation unit not supported, but found in " + file)
        }

        val importMap = compilationUnit.imports
                .flatMap { analyzeImport(it) }
                .toMap()
                .mapValues { entry -> filesByClass[entry.value] }
                .filterValues { it != null }

        val implicitImportMap = filesInSamePackage
                .associateBy { fileToClass(it.fileName) }

        val context = (object : ReferenceExtractionContext {
            override fun getCurrentFile() = file
            override fun resolveFullName(fqcn: String) = filesByClass[fqcn]
            override fun resolveImportedName(simpleName: String): File? =
                    when {
                        simpleName in importMap -> importMap[simpleName]
                        simpleName in implicitImportMap -> implicitImportMap[simpleName]
                        else -> null
            }

            override fun resolveUniqueSimpleName(simpleName: String): File? {

                val found = filesBySimpleClassName[simpleName].orEmpty()
                when (found.size) {
                    0 -> return null
                    1 -> return found[0]
                    else -> {
                        throw UnsupportedOperationException("Ambiguous simple class reference in " + file + ": "
                                + simpleName + " matches several candidates:\n" + found.joinToString("\n"))
                    }
                }
            }
        })

        val visitors : List<CompilationUnitReferenceExtractor> = listOf(
                ImportVisitor(context),
                ClassDeclarationVisitor(context),
                ClassReferenceVisitor(context),
                ConstructorDeclarationVisitor(context),
                PackageDeclarationVisitor(context),
                AnnotationVisitor(context),
                MethodCallVisitor(context),
                StringLiteralVisitor(context),
                JavadocVisitor(context),
                NamedQueryVisitor(context))

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
                    compilationUnit.packageDeclaration.get().begin.get().line + 1
                else
                    compilationUnit.imports[0].begin.get().line

        return Location(line - 1, 0) // our locations are zero-based
    }

    private fun analyzeImport(importDeclaration: ImportDeclaration): List<Pair<String, String>> {

        var result = listOf<Pair<String, String>>()

        val name = importDeclaration.name.toString()
        val nameParts = name.split(".")

        // assume the usual capitalization conventions, to be able to tell which one is the package and which one is the file.

        if (nameParts.dropLast(1).all { it[0].isLowerCase() }) {
            // only the last part starts with an upper case letter, so by the usual naming conventions, we know that this
            // imports a top-level declaration of a compilation unit.

            val baseName = nameParts.last()

            result += Pair(baseName, name)

            // Underscore hack. When a _ class is imported, also count the original class as imported, to make sure that
            // references are properly rewritten.
            if (baseName.last() == '_') {
                result += Pair(baseName.dropLast(1), name.dropLast(1))
            }
        }

        return result
    }

}