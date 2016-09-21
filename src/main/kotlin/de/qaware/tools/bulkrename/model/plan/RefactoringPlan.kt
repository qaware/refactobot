package de.qaware.tools.bulkrename.model.codebase

import de.qaware.tools.bulkrename.model.plan.Step

/**
 * Refactoring plan, consisting of rename steps.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
data class RefactoringPlan(

        val steps: List<Step>

)

;