package de.qaware.tools.bulkrename.extractor

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.ImportDeclaration
import de.qaware.tools.bulkrename.extractor.visitors.ClassDeclarationVisitor
import de.qaware.tools.bulkrename.extractor.visitors.ClassReferenceVisitor
import de.qaware.tools.bulkrename.extractor.visitors.ImportVisitor
import de.qaware.tools.bulkrename.model.codebase.*
import de.qaware.tools.bulkrename.model.reference.Reference
import de.qaware.tools.bulkrename.util.fileToClass
import de.qaware.tools.bulkrename.util.slashify
import java.io.FileInputStream
import java.nio.file.Path
import java.util.*

/**
 * A Java-aware implementation of the ReferenceExtractor.
 *
 * Collects all references from Java-files in a given codebase and returns
 * a map of files to their incoming references from other Java-files.
 */
class JavaReferenceExtractor : ReferenceExtractor {

    override fun extractReferences(codebase: Codebase): Set<Reference> {
        val filesByClass = createClassMap(codebase)
        val outgoingReferences = codebase.modules.map { extractReferencesFromModule(it, codebase.rootPath, filesByClass) }
                .reduce { left, right -> left.plus(right) }
        return outgoingReferences
    }

    private fun createClassMap(codebase: Codebase): Map<String, File> {
        return codebase.modules
                .flatMap { it.sourceFolders }
                .flatMap { it.files }
                .filter { it.type == FileType.JAVA }
                .associateBy({ file -> fileToClass(file.path.resolve(file.fileName).slashify()) }, { it })
    }

    /**
     * Extracts all outgoing references from all files in the given module, which are preset in the knownFilesByEntity-map.
     *
     * @param module the module to extract the references from
     * @param codebaseRootPath the absolute root path of the codebase
     * @param filesByClass a map of each known entity in the codebase to its file
     * @return a map of every file in the given module to a set of files which are referenced by that file.
     *
     */
    private fun extractReferencesFromModule(module: Module, codebaseRootPath: Path, filesByClass: Map<String, File>): Set<Reference> {
        val absoluteModulePath = codebaseRootPath.resolve(module.modulePath)
        return module.sourceFolders
                .flatMap { folder -> extractReferencesFromSourceFolder(absoluteModulePath, folder, filesByClass) }
                .toSet()
    }

    private fun extractReferencesFromSourceFolder(absoluteModulePath: Path, folder: SourceFolder, filesByClass: Map<String, File>) : List<Reference> {
        val absoluteFolderPath = absoluteModulePath.resolve(folder.path)
        return folder.files.flatMap { file -> extractReferencesFromFile(file, absoluteFolderPath, filesByClass) }
    }

    /**
     * Extracts all outgoing references from the given file, which are preset in the knownFilesByEntity-map.
     *
     * @param file the file to extract the references from
     * @param absoluteSourceFolderPath the absolute path to the model containing the given file
     * @param filesByClass a map of each known entity in the codebase to its file
     * @return a set of files which are referenced by the given file.
     */
    private fun extractReferencesFromFile(file: File, absoluteSourceFolderPath: Path, filesByClass: Map<String, File>): Set<Reference> {
        val filePath = absoluteSourceFolderPath.resolve(file.path).resolve(file.fileName)
        if (file.type == FileType.JAVA) {
            val compilationUnit = FileInputStream(filePath.toFile()).use { JavaParser.parse(it) }
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
        // todo other files
        return HashSet()
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

