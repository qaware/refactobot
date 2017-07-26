package de.qaware.refactobot.model.plan

/**
 * Refactoring plan, consisting of rename steps.
 *
 * Steps may contain wildcards, and a schematic plan is to be interpreted sequentially. That is, for each step, apply
 * it to all matching files, and then proceed with the following steps.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 * @author Florian Engel florian.engel@qaware.de
 */
data class SchematicRefactoringPlan(

        val steps: List<Step>

)

