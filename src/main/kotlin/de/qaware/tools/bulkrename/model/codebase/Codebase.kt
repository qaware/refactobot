package de.qaware.tools.bulkrename.model.codebase

import java.nio.file.Path

/**
 * Model of a codebase, consisting of modules.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
data class Codebase(

        /**
         * The root path of the codebase. All other paths are relative to this path.
         */
        val rootPath: Path,

        /**
         * The modules
         */
        val modules: List<Module>
)