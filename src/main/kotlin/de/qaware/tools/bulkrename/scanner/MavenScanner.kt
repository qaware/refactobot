package de.qaware.tools.bulkrename.scanner

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import de.qaware.tools.bulkrename.model.codebase.Codebase
import de.qaware.tools.bulkrename.model.codebase.File
import de.qaware.tools.bulkrename.model.codebase.FileType
import de.qaware.tools.bulkrename.model.codebase.Module
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
        const val MAIN_SUBDIRECTORY: String = "src/main"
        const val TEST_SUBDIRECTORY: String = "src/test"
        const val JAVA_EXTENSION: String = ".java"
    }

    override fun scanCodebase(rootDir: Path): Codebase {

        val modules: List<Module> = Files.find(rootDir, 50, BiPredicate { path, attrs ->
            attrs.isRegularFile && path.fileName.toString() == "pom.xml"
        })
                .filter { p -> !p.contains(Paths.get("target")) }
                .map { p -> createModule(p) }
                .collect(Collectors.toList<Module>())

        return Codebase(rootDir, modules)
    }


    fun createModule(pathToPom: Path): Module {

        val rootPath = pathToPom.parent

        val mainPath = rootPath.resolve(MavenScannerConstants.MAIN_SUBDIRECTORY)
        val testPath = rootPath.resolve(MavenScannerConstants.TEST_SUBDIRECTORY)

        val mainFiles = findAllFilesRecursively(mainPath)
        val testFiles = findAllFilesRecursively(testPath)

        return Module(rootPath.fileName.toString(),
                rootPath,
                mainFiles,
                testFiles)
    }

    private fun findAllFilesRecursively(rootDir: Path): List<File> {
        val paths = findAllFilePathsRecursively(rootDir)
        return parseFiles(paths)
    }

    private fun parseFiles(filePaths: Iterable<Path>): List<File> {
        return filePaths.map { p -> parseFile(p) }.toList()
    }

    private fun parseFile(filePath: Path): File {
        val name = filePath.fileName.toString()
        val type = getFileTypeFromFileName(name)
        val entity = getEntity(filePath, type)
        return File(filePath, name, entity, type)
    }

    private fun getEntity(filePath: Path, fileType: FileType): String {
        if (fileType == FileType.JAVA) {
            val inputStream = FileInputStream(filePath.toFile())
            inputStream.use {
                val compilationUnit = JavaParser.parse(it)
                val classVisitor = ClassVisitor()
                classVisitor.visit(compilationUnit, compilationUnit.`package`.packageName)
                return classVisitor.fullyQualifiedClassName
            }
        }
        return filePath.fileName.toString()
    }

    private fun getFileTypeFromFileName(fileName: String): FileType {
        if (fileName.toLowerCase().endsWith(MavenScannerConstants.JAVA_EXTENSION)) {
            return FileType.JAVA
        }
        return FileType.OTHER
    }

    private fun findAllFilePathsRecursively(rootDir: Path): List<Path> {
        val foundFiles = ArrayList<Path>()
        if (Files.exists(rootDir)) {
            val directoryStream = Files.newDirectoryStream(rootDir)
            for (path in directoryStream) {
                if (path.toFile().isDirectory) {
                    foundFiles.addAll(findAllFilePathsRecursively(path))
                } else {
                    foundFiles.add(path)
                }
            }
        }
        return foundFiles
    }

    private class ClassVisitor : VoidVisitorAdapter<String>() {

        var fullyQualifiedClassName: String = ""

        override fun visit(n: ClassOrInterfaceDeclaration?, arg: String?) {
            if (arg != null) {
                fullyQualifiedClassName = arg + "."
            }
            if (n != null) {
                fullyQualifiedClassName += n.name
            }
            super.visit(n, arg)
        }
    }
}