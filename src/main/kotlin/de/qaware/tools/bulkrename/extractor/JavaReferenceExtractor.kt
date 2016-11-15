package de.qaware.tools.bulkrename.extractor

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import de.qaware.tools.bulkrename.model.codebase.Codebase
import de.qaware.tools.bulkrename.model.codebase.File
import de.qaware.tools.bulkrename.model.codebase.FileType
import de.qaware.tools.bulkrename.model.codebase.Module
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

    override fun extractReferences(codebase: Codebase): Map<File, Set<File>> {
        val knownFilesByEntity = createEntityToFileMap(codebase)
        val outgoingReferences = codebase.modules.map { extractReferencesFromModule(it, codebase.rootPath, knownFilesByEntity) }
                .reduce { left, right -> left.plus(right) }
        return getIncomingFromOutgoingReferencesMap(outgoingReferences, codebase)
    }

    /**
     * Inverts the given reference map so the result maps files to their incoming references instead of their outgoing ones.
     *
     * @param outgoingReferences a map of files to their outgoing references
     * @param codebase the codebase of the given reference map
     * @return a map of files to their incoming references
     */
    private fun getIncomingFromOutgoingReferencesMap(outgoingReferences: Map<File, Set<File>>, codebase: Codebase): Map<File, Set<File>>{
        val incomingReferenceMap = createEmptyFileMap(codebase)
        outgoingReferences.forEach { pair -> pair.value.forEach { incomingReferenceMap[it]!!.add(pair.key) } }
        return incomingReferenceMap
    }

    private fun createEntityToFileMap(codebase: Codebase): Map<String, File> {
        val filesInCodebase = codebase.modules.flatMap { it.mainFiles + it.testFiles }
        return filesInCodebase.associateBy({ it.entity }, { it })
    }

    private fun createEmptyFileMap(codebase: Codebase): Map<File, HashSet<File>>{
        val filesInCodebase = codebase.modules.flatMap { it.mainFiles + it.testFiles }
        return filesInCodebase.associateBy({ it }, { HashSet<File>() })
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
    private fun extractReferencesFromModule(module: Module, codebaseRootPath: Path, knownFilesByEntity: Map<String, File>): Map<File, Set<File>> {
        val filesInModule = module.mainFiles + module.testFiles
        val absoluteModulePath = codebaseRootPath.resolve(module.modulePath)
        return filesInModule.associateBy({ it }, { extractReferencesFromFile(it, absoluteModulePath, knownFilesByEntity) })
    }

    /**
     * Extracts all outgoing references from the given file, which are preset in the knownFilesByEntity-map.
     *
     * @param file the file to extract the references from
     * @param absoluteModulePath the absolute path to the model containing the given file
     * @param knownFilesByEntity a map of each known entity in the codebase to its file
     * @return a set of files which are referenced by the given file.
     */
    private fun extractReferencesFromFile(file: File, absoluteModulePath:Path, knownFilesByEntity: Map<String, File>): Set<File> {
        val filePath = absoluteModulePath.resolve(file.path).resolve(file.fileName)
        if (file.type == FileType.JAVA) {
            val inputStream = FileInputStream(filePath.toFile())
            inputStream.use {
                val compilationUnit = JavaParser.parse(it)
                val importVisitor = ReferencesVisitor()
                importVisitor.visit(compilationUnit, compilationUnit.`package`.packageName)
                val parsedImportNames = importVisitor.importNames
                return parsedImportNames.filter { knownFilesByEntity.containsKey(it) }
                        .map { knownFilesByEntity[it]!! }.toHashSet()
            }
        }
        // todo other files
        return HashSet()
    }

    /**
     * A visitor used for the JavaParser. Takes all supported references
     * and offers a set of imports as result.
     */
    private class ReferencesVisitor : VoidVisitorAdapter<String>() {

        var importNames = HashSet<String>()

        override fun visit(n: ImportDeclaration?, arg: String?) {
            if (n != null) {
                addIfFullyQualified(n.name.toString())
            }
            super.visit(n, arg)
        }

        override fun visit(n: ClassOrInterfaceType?, arg: String?) {
            if (n != null) {
                addIfFullyQualified(n.toString())
            }
            super.visit(n, arg)
        }

        private fun addIfFullyQualified(import: String){
            if (import.contains('.')){
                importNames.add(import)
            }
        }
    }
}