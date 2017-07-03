package de.qaware.repackager.executor.git

import java.nio.file.Path

/**
 * Technology-agnostic wrapper around JGit, for better testability.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
interface Repository {


    /**
     * Commits changes to files to the repository.
     *
     * @param addedFiles paths to the added files, relative to the repo root.
     */
    fun commit(addedFiles: Set<Path>, deletedFiles: Set<Path>, msg: String)


}