package de.qaware.refactobot.model.codebase

/**
 * A folder containing source files within a module, such as "src/main/java" or "src/test/java".
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
data class SourceFolder(

        /**
         * The folder path, relative to the module root.
         */
        val path: String,

        /**
         * The files in the source folder.
         */
        val files: List<File>
)
