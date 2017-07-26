package de.qaware.repackager.scanner

import de.qaware.repackager.model.codebase.*
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
class MavenScanner(val extensions: Set<String> = setOf("java", "xml", "xhtml", "properties", "xjb", "tmpl")) : Scanner {

    companion object MavenScannerConstants {
        val SOURCE_FOLDER_LOCATIONS = listOf(
                "src/main/java",
                "src/test/java",
                "src/main/resources",
                "src/test/resources",
                "src/main/config",
                "src/main/jboss",
                "src/main/admintools",
                "src/main/webapp")
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
     * @param codebaseRoot the absolute path to the codebase root
     * @return a module, relative to the given codebase root.
     */
    fun createModule(pathToPom: Path, codebaseRoot: Path): Module {

        val absoluteModulePath = pathToPom.parent
        val relativeModulePath = codebaseRoot.relativize(absoluteModulePath)

        val sourceFolders = SOURCE_FOLDER_LOCATIONS
                .map { path -> createSourceFolder(absoluteModulePath, path, codebaseRoot) }

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
     * @param codebaseRoot the codebase root path
     * @return the source folder
     */
    private fun createSourceFolder(absoluteModuleRoot: Path, relativeSubdir: String, codebaseRoot: Path): SourceFolder {
        val absoluteScanPath = absoluteModuleRoot.resolve(relativeSubdir)
        val filePaths = scanDirectory(absoluteScanPath)
        val files = filePaths.map { p -> parseFile(p, absoluteScanPath, codebaseRoot) }
        return SourceFolder(relativeSubdir, files)
    }

    /**
     * Parses a file in the given location and returns a simplified file model
     * relative to the given absolute module root path.
     *
     * @param filePath the absolute paths to the file
     * @param sourceRoot the source folder root path
     * @param codebaseRoot the code base root path
     */
    private fun parseFile(filePath: Path, sourceRoot: Path, codebaseRoot: Path): File {
        val name = filePath.fileName.toString()
        val type = getFileTypeFromFileName(name)
        val relativeFilePath = sourceRoot.relativize(filePath).parent ?: Paths.get("")
        val fullPath = codebaseRoot.relativize(filePath).parent ?: Paths.get("")
        return File(fullPath, relativeFilePath, name, type)
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
//                    if (extensions.contains(path.toFile().extension.toLowerCase())) {
                    foundFiles.add(path)
//                }
            }
        }
        }
        return foundFiles
    }
}