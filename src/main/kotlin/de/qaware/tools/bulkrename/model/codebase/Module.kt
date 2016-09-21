package de.qaware.tools.bulkrename.model.codebase

import java.nio.file.Path

/**
 * A module descriptor in a codebase.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
data class Module(

        /**
         * The name of the module.
         */
        val name : String,

        /**
         * The module path, relative to the root path of the codebase.
         *
         * Source paths below are relative to this module path.
         */
        val modulePath : Path,

        /**
         * The list of files in the main tree.
         */
        val mainFiles : List<File>,

        /**
         * The list of files in the test tree.
         */
        val testFiles : List<File>
)