package de.qaware.repackager.executor.git

import java.nio.file.Path

/**
 * Technology-agnostic component for handling (git) repositories. This is just a simple wrapper around jGit for better
 * testability.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
interface RepositoryFactory {

    /**
     * Returns the repository root path, given some path that points into a repository.
     *
     * @param path a path
     * @return the root of the repository, or null, if no repository is found.
     */
    fun findRepositoryRoot(path: Path): Path?

    /**
     * Returns a repository object, for a given root path which must point to an existing repository on disk.
     *
     * @param rootPath some path pointing to a repository
     * @return the repository object, or null, if no repository is found on disk.
     */
    fun getRepository(rootPath: Path): Repository?

}