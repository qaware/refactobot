package de.qaware.refactobot.planner

import de.qaware.refactobot.model.codebase.File
import de.qaware.refactobot.model.operation.FileOperation
import de.qaware.refactobot.model.plan.NewFileLocation
import de.qaware.refactobot.model.reference.Reference

/**
 * Action planner that determines the operations to apply to each file.
 *
 * This merely comes down to collecting the relevant references.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class ActionPlannerImpl : ActionPlanner {

    /**
     * Plans all needed actions for a given refactoring plan with references
     */
    override fun planActions(refactoringPlan: Map<File, NewFileLocation>, allReferences: Set<Reference>) : List<FileOperation> {

        val editsPerFile: Map<File, List<FileOperation.Edit>> = collectEditsPerFile(allReferences, refactoringPlan)

        return refactoringPlan.map { entry -> FileOperation(entry.key.fullName, entry.value.fullName, editsPerFile[entry.key] ?: emptyList()) }
    }

    private fun collectEditsPerFile(allReferences: Set<Reference>, refactoringPlan: Map<File, NewFileLocation>): Map<File, List<FileOperation.Edit>> {
        val editsByFile: MutableMap<File, List<FileOperation.Edit>> = hashMapOf()

        for ((originFile, refs) in allReferences.groupBy(Reference::origin)) {

            editsByFile[originFile] =
                    refs.map { ref -> ref.getAdjustment(refactoringPlan) }
                            .filterNotNull()
        }

        return editsByFile
    }

}