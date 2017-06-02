package de.qaware.repackager.executor.filesystem

import de.qaware.repackager.executor.ActionExecutor
import de.qaware.repackager.executor.EditProcessor
import de.qaware.repackager.model.operation.FileOperation
import de.qaware.repackager.util.slashify
import de.qaware.repackager.util.splitLines
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Path

/**
 * Executer that executes a list of Actions.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class FilesystemActionExecutor(private val rootPath: Path) : ActionExecutor {

    /**
     * Executes a single file move/edit operation
     */
    private fun execute(operation: FileOperation) {
        val sourcePath = rootPath.resolve(operation.sourceFile)
        val targetPath = rootPath.resolve(operation.targetFile)

        println("Moving " + sourcePath.slashify() + " -> " + targetPath.slashify())

        if (sourcePath != targetPath && Files.exists(targetPath)) {
            throw IllegalStateException("Target file already exists: " + operation.targetFile)
        }

        val lines = File(sourcePath.toUri()).readText(Charsets.UTF_8).splitLines()

        Files.createDirectories(targetPath.parent)
        val writer = FileWriter(targetPath.toFile())
        EditProcessor(lines, writer).applyEdits(operation.edits)

        if (sourcePath != targetPath) {
            Files.delete(sourcePath)
        }
    }

    override fun execute(operations: List<FileOperation>, commitMsg: String) {
        operations.forEach { execute(it) }
    }
}