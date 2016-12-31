package de.qaware.tools.bulkrename.model.reference

import de.qaware.tools.bulkrename.model.codebase.File
import de.qaware.tools.bulkrename.model.codebase.Location

/**
 * References from one file to the other.
 *
 * References are pieces of text in the origin that must be adapted when the target file is moved or renamed.
 * They come in different syntactic forms, which is reflected by their type.
 *
 * @author Florian Engel floraian.engel@qaware.de
 */
data class Reference (
        val origin: File,
        val target: File,
        val referenceStart: Location,
        val referenceEnd: Location,
        val referenceType: ReferenceType
)

