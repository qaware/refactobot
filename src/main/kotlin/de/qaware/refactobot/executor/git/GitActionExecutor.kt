package de.qaware.refactobot.executor.git

import de.qaware.refactobot.executor.ActionExecutor
import de.qaware.refactobot.executor.filesystem.FilesystemActionExecutor
import de.qaware.refactobot.model.operation.FileOperation
import java.nio.file.Path

/**
 * Action executor that creates a commit in a git repository.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class GitActionExecutor(repositoryFactory: RepositoryFactory, private val codebaseRoot: Path) : ActionExecutor {

    val repo = repositoryFactory.getRepository(codebaseRoot)

    override fun execute(operations: List<FileOperation>, commitMsg: String) {

        // first perform the file operations as usual
        FilesystemActionExecutor(codebaseRoot).execute(operations)

        // compute paths that must be added / removed from git
        val addedPaths = operations.map { codebaseRoot.resolve(it.targetFile) }.toSet()
        val deletedPaths = operations.map { codebaseRoot.resolve(it.sourceFile) }.toSet() - addedPaths

        // commit
        repo.commit(addedPaths, deletedPaths, commitMsg)
        println("Committed revision: " + commitMsg)
    }


}
