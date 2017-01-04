package de.qaware.tools.bulkrename.extractor

import com.github.javaparser.JavaParser
import de.qaware.tools.bulkrename.extractor.visitors.ClassDeclarationVisitor
import de.qaware.tools.bulkrename.extractor.visitors.ClassReferenceVisitor
import de.qaware.tools.bulkrename.extractor.visitors.ImportVisitor
import de.qaware.tools.bulkrename.model.codebase.*
import de.qaware.tools.bulkrename.model.reference.JavaQualifiedTypeReference
import de.qaware.tools.bulkrename.model.reference.JavaSimpleTypeReference
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
            val visitors = listOf(ImportVisitor(), ClassDeclarationVisitor(), ClassReferenceVisitor(file))
            val localReferences = visitors.flatMap { v -> v.extractReferences(compilationUnit) }

            return createReferencesFromLocalReferences(localReferences, file, filesByClass)
        }
        // todo other files
        return HashSet()
    }

    private fun createReferencesFromLocalReferences(rawReferences: Collection<RawReference>, sourceFile: File, filesByClass: Map<String, File>): Set<Reference> {
        val relevantImports = rawReferences.filter { it.referenceType == ReferenceType.IMPORT && filesByClass.containsKey(it.scope!! + "." + it.name) }
        val fullyQualifiedClassReferences = rawReferences.filter { it.referenceType == ReferenceType.FQ_CLASS_OR_INTERFACE_REFERENCE && filesByClass.containsKey(it.scope!! + "." + it.name) }
        val relevantUnqualifiedReferences = rawReferences.filter { it.referenceType == ReferenceType.CLASS_OR_INTERFACE_REFERENCE && relevantImports.map { it.name }.contains(it.name) }

        val importReferences = relevantImports.map { JavaQualifiedTypeReference(sourceFile, filesByClass[it.scope!! + "." + it.name]!!, it.span) }
        val fqcReferences = fullyQualifiedClassReferences.map { JavaQualifiedTypeReference(sourceFile, filesByClass[it.scope!! + "." + it.name]!!, it.span) }
        val localToFQN = relevantImports.associateBy({ it.name }, { it.scope + "." + it.name })
        val unqualifiedReferences = relevantUnqualifiedReferences.map { JavaSimpleTypeReference(sourceFile, filesByClass[localToFQN[it.name]]!!, it.span) }

        return importReferences
                .union(fqcReferences)
                .union(unqualifiedReferences)
    }
}

