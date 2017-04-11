package de.qaware.tools.bulkrename.scanner

import de.qaware.tools.bulkrename.model.codebase.Codebase
import java.nio.file.Path

/**
 * Interface for a scanner module.
 *
 * A scanner discovers the codebase structure.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
interface Scanner {

    /**
     * Scans a codebase, and produces a model of it, containing all modules, and the files they
     * contain.
     *
     * @param rootDir the root path that should be scanned.
     * @return the codebase model.
     */
    fun scanCodebase(rootDir: Path) : Codebase

}