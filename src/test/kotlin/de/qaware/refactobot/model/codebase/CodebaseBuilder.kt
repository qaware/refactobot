package de.qaware.refactobot.model.codebase

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

        val moduleBuilder = ModuleBuilder()
        body(moduleBuilder)

        modules.add(Module(modulePath = modulePath, sourceFolders = moduleBuilder.sourceFolders))
    }

    @CodebaseMarker
    class ModuleBuilder {
        val sourceFolders = mutableListOf<SourceFolder>()

        fun sourceFolder(path: String, init: SourceFolderBuilder.() -> Unit) {
            val sourcePathBuilder = SourceFolderBuilder()
            init(sourcePathBuilder)
            sourceFolders.add(SourceFolder(path, sourcePathBuilder.files))
        }
    }

    @CodebaseMarker
    class SourceFolderBuilder {
        val files = mutableListOf<File>()

        fun file(name: String, type: FileType) {
            // TODO
            files.add(File(Paths.get("full/path/to/dummy"), Paths.get("dummy"), name, type))
        }
    }
}

fun codebaseBuilder(rootPath: Path, init: CodebaseBuilder.() -> Unit): Codebase {
    val codebaseBuilder = CodebaseBuilder()
    init(codebaseBuilder)
    return Codebase(rootPath = rootPath, modules = codebaseBuilder.modules)
}

