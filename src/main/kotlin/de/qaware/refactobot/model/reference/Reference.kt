package de.qaware.refactobot.model.reference

import de.qaware.refactobot.model.codebase.File
import de.qaware.refactobot.model.operation.FileOperation
import de.qaware.refactobot.model.plan.FileLocation

/**
 * Abstract superclass of all reference types. Each reference has an origin and a target file, which must be known
 * to the framework to find out which references to adapt when a file is moved.
 *
 * The implementation of each subclass defines how the reference must be adjusted when the target is moved/renamed.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
interface Reference {

    val origin : File

    /**
     * Defines how the reference must be adjusted when the target is moved.
     *
     * @param newTarget the new location of the target file.
     * @return an optional edit operation that defines how the source must be rewritten.
     */
    fun getAdjustment(refactoringPlan : Map<File, FileLocation>): FileOperation.Edit?

}
