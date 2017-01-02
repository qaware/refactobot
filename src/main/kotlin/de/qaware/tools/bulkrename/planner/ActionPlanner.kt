package de.qaware.tools.bulkrename.planner

import de.qaware.tools.bulkrename.model.action.Action
import de.qaware.tools.bulkrename.model.codebase.Codebase
import de.qaware.tools.bulkrename.model.codebase.File
import de.qaware.tools.bulkrename.model.plan.NewFileLocation
import de.qaware.tools.bulkrename.model.reference.Reference

/**
 * Action planner for refactoring.
 *
 * @author Alexander Lannes alexander.lannes@qaware.de
 */
class ActionPlanner {

    /**
     * Plans all needed actions for a given refactoring plan with references
     */
    fun planActions(refactoringPlan: Map<File, NewFileLocation>, allReferences: List<Reference>, codebase: Codebase) : List<Action> {

        val actions : List<Action> = listOf()

        val referencesByFile: Map<File, List<Reference>> = allReferences.groupBy(Reference::target)
        val moveActions = mutableListOf<Action>()

        for ((file, newLocation) in refactoringPlan) {

            if (fileIsMoved(file, newLocation)) {

                moveActions += Action.MoveFile(sourceFile = file.fullPath.resolve(file.fileName),
                        targetFile = newLocation.fullPath.resolve(newLocation.fileName))

                // find all references to the file
                val references = referencesByFile[file] ?: emptyList()

                for (reference in references) {
//                    if(reference.type == ReferenceType.IMPORT_DECLARATION) {
//                        actions += EditAction(reference.sourceFile.fileName, ActionType.EDIT_ACTION, reference.range,
//                                step.targetPackage.toString() + "." + step.newFileName.substringBefore(".java"))
//                    }
//                    if(reference.type == ReferenceType.CLASS_DECLARATION || reference.type == ReferenceType.CLASS_REFERENCE) {
//                        actions += EditAction(reference.sourceFile.fileName, ActionType.EDIT_ACTION, reference.range, step.newFileName.substringBefore(".java"))
//                    }
                }
            }

        }
        return actions
    }

    private fun fileIsMoved(file: File, newFileLocation: NewFileLocation) : Boolean =
            !(file.fullPath == newFileLocation.fullPath && file.fileName == newFileLocation.fileName);






}