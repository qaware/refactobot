package de.qaware.refactobot.model.plan

/**
 * A step in a refactoring plan.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 * @author Florian Engel florian.engel@qaware.de
 */
data class Step(

        val replacements: Map<RefactoringSubject, Replacement>

) {

        data class Replacement(val regex: Regex, val replacement: String)

}