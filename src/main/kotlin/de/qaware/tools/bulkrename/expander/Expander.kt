package de.qaware.tools.bulkrename.expander

import de.qaware.tools.bulkrename.model.codebase.Codebase
import de.qaware.tools.bulkrename.model.plan.FullRefactoringPlan
import de.qaware.tools.bulkrename.model.plan.SchematicRefactoringPlan

/**
 * The expander takes a schematic refactoring plan and creates a full refactoring plan for a given codebase from it.
 *
 * The resulting plan contains specific refactoring instructions for every file rather than regular expressions.
 *
 * @author Florian Engel florian.engel@qaware.de
 */
interface Expander {

        fun expandRefactoringPlan(refactoringPlan: SchematicRefactoringPlan, codebase: Codebase): FullRefactoringPlan

}