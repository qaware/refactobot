package de.qaware.repackager.extractor.java

import de.qaware.repackager.model.codebase.File

/**
 * Context information, relevant for extracting references.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
interface ReferenceExtractionContext {

    fun getCurrentFile(): File
    fun resolveFullName(fqcn: String): File?
    fun resolveImportedName(simpleName: String): File?
    fun resolveUniqueSimpleName(simpleName: String): File?

}