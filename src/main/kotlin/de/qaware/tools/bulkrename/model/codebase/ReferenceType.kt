package de.qaware.tools.bulkrename.model.codebase

/**
 * Types of references.
 *
 * @author Florian Engel florian.engel@qaware.de
 */
enum class ReferenceType {

    /**
     * An import declaration
     */
    IMPORT,

    /**
     * A fully-qualified type reference
     */
    FQ_CLASS_OR_INTERFACE_REFERENCE,

    /**
     * A simple type reference
     */
    CLASS_OR_INTERFACE_REFERENCE
}
