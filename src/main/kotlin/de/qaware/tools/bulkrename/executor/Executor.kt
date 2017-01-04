package de.qaware.tools.bulkrename.executor

import de.qaware.tools.bulkrename.model.operation.FileOperation
import de.qaware.tools.bulkrename.util.slashify
import de.qaware.tools.bulkrename.util.splitLines
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Path

/**
 * Executer that executes a list of Actions.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class Executor(private val rootPath: Path) {

    /**
     * Executes a single file move/edit operation
     */
    fun execute(operation: FileOperation) {
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
}