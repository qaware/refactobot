package de.qaware.tools.bulkrename.model.codebase

import java.nio.file.Path

/**
 * A file reference, as part of a codebase.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
data class File(

        /**
         * The path to the file (not including the file name), relative to the module root.
         */
        val path: Path,

        /**
         * The name of the file.
         */
        val fileName: String,

        /**
         * The name of the entity that this file represents, e.g., a class.
         * The representation is subject to the FileType.
         *
         * If this does not apply, may be empty.
         */
        val entity: String,

        val type: FileType
)

