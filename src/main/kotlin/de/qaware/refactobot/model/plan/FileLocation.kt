package de.qaware.refactobot.model.plan

/**
 * Data class for target names/locations with regard to a single file.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
data class FileLocation(

        /**
         * The new module.
         */
        val module: String,

        /**
         * The new source folder
         */
        val sourceFolder: String,

        /**
         * The new relative path, within the source folder.
         */
        val path: String,

        /**
         * The new filename
         */
        val fileName: String
) {

    /**
     * The full path and name of the new file, relative to the codebase root.
     */
    val fullName = "$module/$path/$fileName"

}

