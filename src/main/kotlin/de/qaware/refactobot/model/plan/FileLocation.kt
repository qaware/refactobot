package de.qaware.refactobot.model.plan

/**
 * Data class for target names/locations with regard to a single file.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 *
 * @param module the new module
 * @param sourceFolder the new source folder
 * @param path the new relative path, within the source folder
 * @param fileName the new file name
 */
data class FileLocation(val module: String, val sourceFolder: String, val path: String, val fileName: String) {

    /**
     * The full path and name of the new file, relative to the codebase root.
     */
    val fullName = "$module/$sourceFolder/$path/$fileName"

}

