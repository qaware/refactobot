package de.qaware.refactobot.model.codebase

import de.qaware.refactobot.util.splitPath
import java.nio.file.Path
import java.nio.file.Paths

@DslMarker
annotation class CodebaseMarker

/**
 * Builder DSL for codebase objects, for testing purposes.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
@CodebaseMarker
class CodebaseBuilder() {

    val modules = mutableListOf<Module>()

    fun module(modulePath: String, body: ModuleBuilder.() -> Unit) {

        val moduleBuilder = ModuleBuilder(Paths.get(modulePath))
        body(moduleBuilder)

        modules.add(Module(modulePath = modulePath, sourceFolders = moduleBuilder.sourceFolders))
    }

    @CodebaseMarker
    class ModuleBuilder(val contextPath : Path) {
        val sourceFolders = mutableListOf<SourceFolder>()

        fun sourceFolder(path: String, init: SourceFolderBuilder.() -> Unit) {
            val sourcePathBuilder = SourceFolderBuilder(contextPath.resolve(path))
            init(sourcePathBuilder)
            sourceFolders.add(SourceFolder(path, sourcePathBuilder.files))
        }
    }

    @CodebaseMarker
    class SourceFolderBuilder(val contextPath : Path) {
        val files = mutableListOf<File>()

        fun file(filePath: String, type: FileType) {

            val (path, name) = splitPath(filePath)
            val fullPath = contextPath.resolve(path)
            files.add(File(fullPath, path, name, type))
        }
    }
}

fun codebaseBuilder(rootPath: Path, init: CodebaseBuilder.() -> Unit): Codebase {
    val codebaseBuilder = CodebaseBuilder()
    init(codebaseBuilder)
    return Codebase(rootPath = rootPath, modules = codebaseBuilder.modules)
}

