package de.qaware.refactobot.model.codebase

/**
 * A module descriptor in a codebase.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
data class Module(

        /**
         * The module path, relative to the root path of the codebase.
         *
         * Source paths below are relative to this module path.
         */
        val modulePath: String,

        /**
         * The source folders within this module.
         */
        val sourceFolders: List<SourceFolder>
)