package de.qaware.repackager.executor

import de.qaware.repackager.model.operation.FileOperation

/**
 * Component that applies a given list of move/edit operations to an actual codebase.
 *
 * Different implementations are used to deal with version control details, etc.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
interface ActionExecutor {

    /**
     * Executes all of the given operations.
     *
     * @param operations a list of file operations.
     */
    fun execute(operations: List<FileOperation>)
}