package de.qaware.tools.bulkrename.model.plan

import de.qaware.tools.bulkrename.model.codebase.File

/**
 * A fully expanded refactoring plan, matching files with specific file refactoring instructions.
 *
 * @author Florian Engel florian.engel@qaware.de
 */
data class FullRefactoringPlan(

        val steps: Map<File, FileRefactoringInstruction>
)
