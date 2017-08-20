package de.qaware.refactobot.expander

import de.qaware.refactobot.configuration.RefactoringStep
import de.qaware.refactobot.model.codebase.File
import de.qaware.refactobot.model.plan.FileLocation

/**
 * The expander takes a schematic refactoring plan and creates a full refactoring plan for a given codebase from it.
 *
 * The resulting plan contains specific refactoring instructions for every file rather than regular expressions.
 *
 * @author Florian Engel florian.engel@qaware.de
 */
interface Expander {

        fun expandRefactoringPlan(refactoringPlan: List<RefactoringStep>): Map<File, FileLocation>

}