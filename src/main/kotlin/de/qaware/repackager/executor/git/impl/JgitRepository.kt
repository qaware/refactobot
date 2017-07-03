package de.qaware.repackager.executor.git.impl

import de.qaware.repackager.executor.git.Repository
import de.qaware.repackager.util.slashify
import org.eclipse.jgit.api.Git
import java.nio.file.Path


/**
 * Repository implementation in terms of jGit.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class JgitRepository(val repo: org.eclipse.jgit.lib.Repository) : Repository {

    override fun commit(addedFiles: Set<Path>, deletedFiles: Set<Path>, msg: String) {

        val repoRoot = repo.workTree.toPath()

        if (addedFiles.isNotEmpty()) {
            val gitAdd = Git(repo).add()
            addedFiles.forEach { file ->
                gitAdd.addFilepattern(repoRoot.relativize(file).slashify())
            }
            gitAdd.call()
        }

        if (deletedFiles.isNotEmpty()) {
            val gitRm = Git(repo).rm()
            deletedFiles.forEach { file ->
                gitRm.addFilepattern(repoRoot.relativize(file).slashify()) }
            gitRm.call()
        }

        Git(repo).commit().setMessage(msg).call()
    }


}