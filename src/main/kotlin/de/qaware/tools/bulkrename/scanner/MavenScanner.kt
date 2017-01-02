package de.qaware.tools.bulkrename.scanner

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.body.AnnotationDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.EnumDeclaration
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import de.qaware.tools.bulkrename.model.codebase.*
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.function.BiPredicate
import java.util.stream.Collectors

/**
 * Simple project scanner that uses maven directory structures.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 * @author Florian Engel florian.engel@qaware.de
 */
class MavenScanner : Scanner {

    companion object MavenScannerConstants {
        val SOURCE_FOLDER_LOCATIONS = listOf("src/main/java", "src/test/java")
        const val MAIN_SUBDIRECTORY: String = "src/main"
        const val TEST_SUBDIRECTORY: String = "src/test"
        const val JAVA_EXTENSION: String = ".java"
    }

    override fun scanCodebase(rootDir: Path): Codebase {

        val modules: List<Module> = Files.find(rootDir, 50, BiPredicate { path, attrs ->
            attrs.isRegularFile && path.fileName.toString() == "pom.xml"
        })
                .filter { p -> !p.contains(Paths.get("target")) }
                .map { p -> createModule(p, rootDir) }
                .collect(Collectors.toList<Module>())

        return Codebase(rootDir, modules)
    }


    /**
     * Creates a model of the Maven module residing in the given path.
     * @param pathToPom the absolute path to the pom.xml describing the Maven module
     * @param codebaseRootDir the absolute path to the codebase root
     * @return a module, relative to the given codebase root.
     */
    fun createModule(pathToPom: Path, codebaseRootDir: Path): Module {

        val absoluteModulePath = pathToPom.parent
        val relativeModulePath = codebaseRootDir.relativize(absoluteModulePath)

        val sourceFolders = SOURCE_FOLDER_LOCATIONS
                .map { path -> createSourceFolder(absoluteModulePath, path) }
                .filter { !it.files.isEmpty() } // source roots containing no files are considered non-existing

        val moduleName = absoluteModulePath.fileName.toString()

        return Module(moduleName,
                relativeModulePath,
                sourceFolders)
    }

    /**
     * Finds all files residing in the given search location.
     * This may be any given subdirectory below the given absoluteModuleRoot.
     *
     * @param absoluteModuleRoot the module root
     * @param relativeSubdir a relative subdirectory (e.g. "src/main"
     * @return the source folder
     */
    private fun createSourceFolder(absoluteModuleRoot: Path, relativeSubdir: String): SourceFolder {
        val absoluteScanPath = absoluteModuleRoot.resolve(relativeSubdir)
        val filePaths = scanDirectory(absoluteScanPath)
        val files = filePaths.map { p -> parseFile(p, absoluteScanPath) }
        return SourceFolder(relativeSubdir, files)
    }

    /**
     * Parses a file in the given location and returns a simplified file model
     * relative to the given absolute module root path.
     *
     * @param filePath the absolute paths to the file
     * @param absoluteModuleRoot the module root path
     */
    private fun parseFile(filePath: Path, absoluteModuleRoot: Path): File {
        val name = filePath.fileName.toString()
        val type = getFileTypeFromFileName(name)
        val entity = parseEntityFromPath(filePath, type)
        val relativeFilePath = absoluteModuleRoot.relativize(filePath).parent
        return File(relativeFilePath, name, entity, type)
    }

    /**
     * Parses the (fully qualified) top-level entity name from the given location.
     *
     * @param filePath the path to the target file
     * @param fileType the file type of the target file
     * @return the fully qualified entity name if the file type and the top level entity can be determined,
     *         an empty string otherwise.
     */
    private fun parseEntityFromPath(filePath: Path, fileType: FileType): String {
        var entityName: String = ""
        if (fileType == FileType.JAVA) {
            val inputStream = FileInputStream(filePath.toFile())
            inputStream.use {
                val compilationUnit = JavaParser.parse(it)
                val entityVisitor = TopLevelEntityVisitor()
                entityVisitor.visit(compilationUnit, compilationUnit.`package`.packageName)
                entityName = entityVisitor.fullyQualifiedEntityName
            }
        }
        return entityName
    }

    private fun getFileTypeFromFileName(fileName: String): FileType {
        if (fileName.toLowerCase().endsWith(MavenScannerConstants.JAVA_EXTENSION)) {
            return FileType.JAVA
        }
        return FileType.OTHER
    }

    private fun scanDirectory(rootDir: Path): List<Path> {
        val foundFiles = ArrayList<Path>()
        if (Files.exists(rootDir)) {
            val directoryStream = Files.newDirectoryStream(rootDir)
            for (path in directoryStream) {
                if (path.toFile().isDirectory) {
                    foundFiles.addAll(scanDirectory(path))
                } else {
                    foundFiles.add(path)
                }
            }
        }
        return foundFiles
    }

    /**
     * A visitor used for the JavaParser. Takes all supported top level
     * entities and their package name as parameter and offers the fully
     * qualified entity name as property.
     */
    private class TopLevelEntityVisitor : VoidVisitorAdapter<String>() {

        var fullyQualifiedEntityName: String = ""

        override fun visit(n: ClassOrInterfaceDeclaration?, arg: String?) {
            if (n != null) {
                storeFullyQualifiedEntityName(n.name, arg)
            }
            super.visit(n, arg)
        }

        override fun visit(n: EnumDeclaration?, arg: String?) {
            if (n != null) {
                storeFullyQualifiedEntityName(n.name, arg)
            }
            super.visit(n, arg)
        }

        override fun visit(n: AnnotationDeclaration?, arg: String?) {
            if (n != null) {
                storeFullyQualifiedEntityName(n.name, arg)
            }
            super.visit(n, arg)
        }

        private fun storeFullyQualifiedEntityName(entityName: String?, packageName: String?) {
            if (packageName != null) {
                fullyQualifiedEntityName = packageName + "."
            }
            if (entityName != null) {
                fullyQualifiedEntityName += entityName
            }
        }
    }
}