package de.qaware.refactobot.executor.filesystem

import de.qaware.refactobot.executor.ActionExecutor
import de.qaware.refactobot.executor.EditProcessor
import de.qaware.refactobot.model.operation.FileOperation
import de.qaware.refactobot.util.splitLines
import org.apache.commons.io.FileUtils
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

        if (sourcePath != targetPath && Files.exists(targetPath)) {
            throw IllegalStateException("Target file already exists: " + operation.targetFile)
        }

        if (operation.edits.isEmpty()) {

            if (sourcePath == targetPath) {

                // fast path 1: nothing to do if source and target are identical and the file is unchanged
                return;

            } else {

                // fast path 2: plain move operation, with the file content unchanged.
                Files.createDirectories(targetPath.parent)
                FileUtils.moveFile(sourcePath.toFile(), targetPath.toFile());

            }
        } else {

            // We edit the file content. The following code assumes that we are dealing with a text file.

            val lines = File(sourcePath.toUri()).readText(Charsets.UTF_8).splitLines()

            Files.createDirectories(targetPath.parent)
            val writer = FileWriter(targetPath.toFile())
            EditProcessor.applyEdits(lines, writer, operation.edits)

            if (sourcePath != targetPath) {
                Files.delete(sourcePath)
            }
        }
    }

    override fun execute(operations: List<FileOperation>, commitMsg: String) {
        operations.forEach { execute(it) }
    }
}