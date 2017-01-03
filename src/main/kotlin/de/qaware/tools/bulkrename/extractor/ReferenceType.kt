package de.qaware.tools.bulkrename.extractor

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
     * A simple reference to a top-level type.
     *
     * The corresponding range contains the simple name of the type.
     */
    CLASS_OR_INTERFACE_REFERENCE
}
