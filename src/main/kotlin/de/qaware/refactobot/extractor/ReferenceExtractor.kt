package de.qaware.refactobot.extractor

import de.qaware.refactobot.model.codebase.Codebase
import de.qaware.refactobot.model.reference.Reference

/**
 * Extracts file references from a given codebase. Both source and target of the reference must
 * be within the codebase in order to be extracted.
 *
 * @author Florian Engel florian.engel@qaware.de
 */
interface ReferenceExtractor {

    /**
     * Extracts references from the given codebase and returns them as a map of every _incoming_ reference (value) to
     * a file (key).
     *
     * @param codebase the codebase to extract references from
     * @return a map of all files in the codebase (key) to a list of all files referencing that file (value).
     */
    fun extractReferences(codebase: Codebase): Set<Reference>
}