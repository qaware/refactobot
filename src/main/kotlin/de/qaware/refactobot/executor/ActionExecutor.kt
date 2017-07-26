package de.qaware.refactobot.executor

import de.qaware.refactobot.model.operation.FileOperation

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
     * @param commitMsg the commit message to use (if applicable).
     */
    fun execute(operations: List<FileOperation>, commitMsg: String = "")
}