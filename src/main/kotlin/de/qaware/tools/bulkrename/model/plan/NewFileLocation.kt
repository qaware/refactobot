package de.qaware.tools.bulkrename.model.plan

import de.qaware.tools.bulkrename.model.codebase.Module
import de.qaware.tools.bulkrename.model.codebase.SourceFolder
import java.nio.file.Path

/**
 * Data class for target names/locations with regard to a single file.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
data class NewFileLocation(

        /**
         * The new module.
         */
        val module: Module,

        /**
         * The new source folder
         */
        val sourceFolder: SourceFolder,

        /**
         * The new relative path, within the source folder.
         */
        val path: Path,

        /**
         * The new filename
         */
        val fileName: String
) {

    /**
     * The full path and name of the new file, relative to the codebase root.
     */
    val fullName = module.modulePath.resolve(sourceFolder.path).resolve(path).resolve(fileName)

}

