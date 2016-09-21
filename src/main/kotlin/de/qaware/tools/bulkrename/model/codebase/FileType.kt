package de.qaware.tools.bulkrename.model.codebase

/**
 * Supported file types.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
enum class FileType {

    /**
     * Java source files.
     */
    JAVA,

    /**
     * Other text files. Will be searched for fully-qualified class names, which are considered as
     * references.
     */
    OTHER
}
