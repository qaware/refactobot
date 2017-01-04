package de.qaware.tools.bulkrename.extractor

import de.qaware.tools.bulkrename.model.codebase.File

/**
 * Context information, relevant for extracting references.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
interface ReferenceExtractionContext {

    fun getCurrentFile(): File
    fun getFileForClass(fqcn: String): File?
    fun getFileForImportedClass(simpleName: String): File?

}