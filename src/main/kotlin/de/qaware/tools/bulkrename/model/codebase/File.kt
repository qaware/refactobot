package de.qaware.tools.bulkrename.model.codebase

import java.nio.file.Path

/**
 * A file reference, as part of a codebase.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
data class File(

        /**
         * The full path to the file, relative to the codebase root.
         */
        val fullPath: Path,

        /**
         * The path to the file (not including the file name), relative to the source folder root.
         */
        val path: Path,

        /**
         * The name of the file.
         */
        val fileName: String,

        /**
         * The type of the file.
         */
        val type: FileType
)

