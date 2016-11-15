package de.qaware.tools.bulkrename.model.plan

/**
 * Refactoring plan, consisting of rename steps.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 * @author Florian Engel florian.engel@qaware.de
 */
data class SchematicRefactoringPlan(

        val steps: List<Step>

)

