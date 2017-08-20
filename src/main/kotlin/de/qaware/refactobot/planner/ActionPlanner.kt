package de.qaware.refactobot.planner

import de.qaware.refactobot.model.codebase.File
import de.qaware.refactobot.model.operation.FileOperation
import de.qaware.refactobot.model.plan.FileLocation
import de.qaware.refactobot.model.reference.Reference

/**
 * The action planner.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
interface ActionPlanner {

    /**
     * Given a refactoring plan and a list of references, compute the operations that must be made to each file.
     */
    fun planActions(refactoringPlan: Map<File, FileLocation>, allReferences: Set<Reference>) : List<FileOperation>

}