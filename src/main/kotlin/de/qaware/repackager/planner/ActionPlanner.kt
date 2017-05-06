package de.qaware.repackager.planner

import de.qaware.repackager.model.codebase.File
import de.qaware.repackager.model.operation.FileOperation
import de.qaware.repackager.model.plan.NewFileLocation
import de.qaware.repackager.model.reference.Reference

/**
 * The action planner.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
interface ActionPlanner {

    /**
     * Given a refactoring plan and a list of references, compute the operations that must be made to each file.
     */
    fun planActions(refactoringPlan: Map<File, NewFileLocation>, allReferences: Set<Reference>) : List<FileOperation>

}