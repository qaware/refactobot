package de.qaware.repackager.executor.git.impl

import de.qaware.repackager.executor.git.Repository
import de.qaware.repackager.executor.git.RepositoryFactory
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.nio.file.Path

/**
 * Repository factory implementation using jgit.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class JgitRepositoryFactory : RepositoryFactory {

    override fun findRepositoryRoot(path: Path): Path? {
        TODO("not implemented")
    }
    override fun getRepository(rootPath: Path): Repository {

        val repo = FileRepositoryBuilder()
                .setMustExist(true)
                .findGitDir(rootPath.toFile())
                .readEnvironment()
                .build()

        return JgitRepository(repo)
    }

}