package de.qaware.tools.bulkrename.extractor

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.expr.QualifiedNameExpr
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import de.qaware.tools.bulkrename.model.codebase.*
import de.qaware.tools.bulkrename.model.reference.Reference
import de.qaware.tools.bulkrename.model.reference.ReferenceType
import de.qaware.tools.bulkrename.util.fileToClass
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
        val knownFilesByEntity = createEntityToFileMap(codebase)
        val outgoingReferences = codebase.modules.map { extractReferencesFromModule(it, codebase.rootPath, knownFilesByEntity) }
                .reduce { left, right -> left.plus(right) }
        return outgoingReferences
    }

    private fun entityForFile(file : File) =
            if (file.type == FileType.JAVA)
                fileToClass(file.path.resolve(file.fileName).joinToString("/"))
            else ""

    private fun createEntityToFileMap(codebase: Codebase): Map<String, File> {
        return codebase.modules
                .flatMap { it.sourceFolders }
                .flatMap { it.files }
                .associateBy({ entityForFile(it) }, { it })
    }

    /**
     * Extracts all outgoing references from all files in the given module, which are preset in the knownFilesByEntity-map.
     *
     * @param module the module to extract the references from
     * @param codebaseRootPath the absolute root path of the codebase
     * @param knownFilesByEntity a map of each known entity in the codebase to its file
     * @return a map of every file in the given module to a set of files which are referenced by that file.
     *
     */
    private fun extractReferencesFromModule(module: Module, codebaseRootPath: Path, knownFilesByEntity: Map<String, File>): Set<Reference> {
        val absoluteModulePath = codebaseRootPath.resolve(module.modulePath)
        return module.sourceFolders
                .flatMap { folder -> extractReferencesFromSourceFolder(absoluteModulePath, folder, knownFilesByEntity) }
                .toSet()
    }

    private fun extractReferencesFromSourceFolder(absoluteModulePath: Path, folder: SourceFolder, knownFilesByEntity: Map<String, File>) : List<Reference> {
        val absoluteFolderPath = absoluteModulePath.resolve(folder.path)
        return folder.files.flatMap { file -> extractReferencesFromFile(file, absoluteFolderPath, knownFilesByEntity) }
    }

    /**
     * Extracts all outgoing references from the given file, which are preset in the knownFilesByEntity-map.
     *
     * @param file the file to extract the references from
     * @param absoluteSourceFolderPath the absolute path to the model containing the given file
     * @param knownFilesByEntity a map of each known entity in the codebase to its file
     * @return a set of files which are referenced by the given file.
     */
    private fun extractReferencesFromFile(file: File, absoluteSourceFolderPath: Path, knownFilesByEntity: Map<String, File>): Set<Reference> {
        val filePath = absoluteSourceFolderPath.resolve(file.path).resolve(file.fileName)
        if (file.type == FileType.JAVA) {
            val inputStream = FileInputStream(filePath.toFile())
            inputStream.use {
                val compilationUnit = JavaParser.parse(it)
                val importVisitor = ReferencesVisitor()
                importVisitor.visit(compilationUnit, compilationUnit.`package`.packageName)
                val localReferences = importVisitor.references
                return createReferencesFromLocalReferences(localReferences, file, knownFilesByEntity)
            }
        }
        // todo other files
        return HashSet()
    }

    private fun createReferencesFromLocalReferences(localReferences: Set<LocalReference>, sourceFile: File, knownFilesByEntity: Map<String, File>): Set<Reference> {
        val references = HashSet<Reference>()
        val relevantImports = localReferences.filter { it.referenceType == ReferenceType.IMPORT && knownFilesByEntity.containsKey(it.scope!! + "." + it.name) }
        val fullyQualifiedClassReferences = localReferences.filter { it.referenceType == ReferenceType.FQ_CLASS_OR_INTERFACE_REFERENCE && knownFilesByEntity.containsKey(it.scope!! + "." + it.name) }
        val relevantUnqualifiedReferences = localReferences.filter { it.referenceType == ReferenceType.CLASS_OR_INTERFACE_REFERENCE && relevantImports.map { it.name }.contains(it.name) }

        val importReferences = relevantImports.map { Reference(sourceFile, knownFilesByEntity[it.scope!! + "." + it.name]!!, it.begin, it.end, it.referenceType) }
        val fqcReferences = fullyQualifiedClassReferences.map { Reference(sourceFile, knownFilesByEntity[it.scope!! + "." + it.name]!!, it.begin, it.end, it.referenceType) }
        val localToFQN = relevantImports.associateBy({ it.name }, { it.scope + "." + it.name })
        val unqualifiedReferences = relevantUnqualifiedReferences.map { Reference(sourceFile, knownFilesByEntity[localToFQN[it.name]]!!, it.begin, it.end, it.referenceType) }
        references.addAll(importReferences)
        references.addAll(fqcReferences)
        references.addAll(unqualifiedReferences)
        return references
    }

    /**
     * A visitor used for the JavaParser. Takes all supported references
     * and offers a set of imports as result.
     */
    private class ReferencesVisitor : VoidVisitorAdapter<String>() {

        var references = HashSet<LocalReference>()

        override fun visit(n: ImportDeclaration?, arg: String?) {
            if (n != null) {
                val reference = LocalReference(
                        ReferenceType.IMPORT,
                        Location(n.name.begin.line, n.name.begin.column),
                        Location(n.name.end.line, n.name.end.column),
                        (n.name as? QualifiedNameExpr)?.qualifier?.toString() ?: throw UnsupportedOperationException("Imports must be qualified."),
                        n.name.name
                )
                references.add(reference)
            }
            super.visit(n, arg)
        }

        override fun visit(n: ClassOrInterfaceType?, arg: String?) {
            if (n != null) {
                if (n.typeArguments.typeArguments.isNotEmpty() && n.begin.line != n.end.line) {
                    // The range of the name is no longer correct for multiline generics, so we simply do not support it.
                    throw UnsupportedOperationException("Multiline usage of generic types is not supported: %s (%d:%d-%d:%d)"
                            .format(n.name, n.begin.line, n.begin.column, n.end.line, n.end.column))
                }
                val fullName = (if (n.scope != null) n.scope.toString() + "." else "") + n.name
                if (n.typeArguments.typeArguments.isNotEmpty() && n.typeArguments.typeArguments.first().begin.column - (n.begin.column + fullName.length) != 1) {
                    throw UnsupportedOperationException("Spaces in generic type references are not supported: %s (%d:%d-%d:%d)"
                            .format(n.name, n.begin.line, n.begin.column, n.end.line, n.end.column))
                }
                val type = if (n.scope != null) ReferenceType.FQ_CLASS_OR_INTERFACE_REFERENCE else ReferenceType.CLASS_OR_INTERFACE_REFERENCE
                val endLocation = if (n.typeArguments.typeArguments.isEmpty()) Location(n.end.line, n.end.column) else Location(n.typeArguments.typeArguments.first().begin.line, n.typeArguments.typeArguments.first().begin.column - 2)
                references.add(LocalReference(type,
                        Location(n.begin.line, n.begin.column),
                        endLocation,
                        n.scope?.toString(),
                        n.name
                ))
            }
            super.visit(n, arg)
        }

        private fun validateClassOrInterfaceType(classOrInterfaceType: ClassOrInterfaceType){

        }
    }

    private data class LocalReference(
            val referenceType: ReferenceType,
            val begin: Location,
            val end: Location,
            val scope: String?,
            val name: String
    )
}