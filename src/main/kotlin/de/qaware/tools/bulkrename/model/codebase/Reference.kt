package de.qaware.tools.bulkrename.model.codebase

/**
 * Created by f.engel on 16.11.2016.
 */
data class Reference (
        val origin: File,
        val target: File,
        val referenceStart: Location,
        val referenceEnd: Location,
        val referenceType: ReferenceType
)

