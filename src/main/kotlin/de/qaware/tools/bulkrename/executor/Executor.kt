package de.qaware.tools.bulkrename.executor

import de.qaware.tools.bulkrename.model.action.FileOperation
import de.qaware.tools.bulkrename.model.codebase.Codebase
import java.io.File
import java.io.FileWriter
import java.nio.file.Files

/**
 * Executer that executes a list of Actions.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class Executor(private val codebase: Codebase) {

    /**
     * Executes a single file move/edit operation
     */
    fun execute(operation: FileOperation) {
        val rootPath = codebase.rootPath
        val sourcePath = rootPath.resolve(operation.sourceFile)
        val targetPath = rootPath.resolve(operation.targetFile)

        if (Files.exists(targetPath)) {
            throw IllegalStateException("Target file already exists: " + operation.targetFile)
        }

        val lines = File(sourcePath.toUri()).readLines()

        Files.createDirectories(targetPath.parent)
        val writer = FileWriter(targetPath.toFile())
        EditProcessor(lines, writer).applyEdits(operation.edits)
        Files.delete(sourcePath)
    }
}