package de.qaware.repackager.expander

import de.qaware.repackager.model.codebase.File
import de.qaware.repackager.model.plan.NewFileLocation
import de.qaware.repackager.model.plan.SchematicRefactoringPlan

/**
 * The expander takes a schematic refactoring plan and creates a full refactoring plan for a given codebase from it.
 *
 * The resulting plan contains specific refactoring instructions for every file rather than regular expressions.
 *
 * @author Florian Engel florian.engel@qaware.de
 */
interface Expander {

        fun expandRefactoringPlan(refactoringPlan: SchematicRefactoringPlan): Map<File, NewFileLocation>

}