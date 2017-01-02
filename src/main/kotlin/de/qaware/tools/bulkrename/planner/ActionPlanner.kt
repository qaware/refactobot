package de.qaware.tools.bulkrename.planner

import de.qaware.tools.bulkrename.model.action.Action
import de.qaware.tools.bulkrename.model.codebase.Codebase
import de.qaware.tools.bulkrename.model.codebase.File
import de.qaware.tools.bulkrename.model.plan.FileRefactoringInstruction
import de.qaware.tools.bulkrename.model.plan.FullRefactoringPlan
import de.qaware.tools.bulkrename.model.plan.RefactoringSubject
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
    fun planActions(refactoringPlan: FullRefactoringPlan, references: List<Reference>, codebase: Codebase) : List<Action> {

        var actions : List<Action> = listOf()

        for (step in refactoringPlan.steps) {

            val changedFile: File = step.key;
            val instruction: FileRefactoringInstruction = step.value;

            val newFilename: String = instruction.targets[RefactoringSubject.FILE_NAME] ?: changedFile.fileName;

//            actions += MoveAction(changedFile.fileName, ActionType.MOVE_ACTION, newFileName, step.targetModule, step.targetPackage)
//            for (reference in references) {
//                if(changedFile.entity == reference.referendedFile.entity) {
//                    if(reference.type == ReferenceType.IMPORT_DECLARATION) {
//                        actions += EditAction(reference.sourceFile.fileName, ActionType.EDIT_ACTION, reference.range,
//                                step.targetPackage.toString() + "." + step.newFileName.substringBefore(".java"))
//                    }
//                    if(reference.type == ReferenceType.CLASS_DECLARATION || reference.type == ReferenceType.CLASS_REFERENCE) {
//                        actions += EditAction(reference.sourceFile.fileName, ActionType.EDIT_ACTION, reference.range, step.newFileName.substringBefore(".java"))
//                    }
//                }
//            }
        }
        return actions
    }


}