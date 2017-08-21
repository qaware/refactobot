package de.qaware.refactobot.model.plan.old

import de.qaware.refactobot.model.plan.FileLocation
import de.qaware.refactobot.model.plan.RefactoringStep

/**
 * Methods that apply symbolic steps to a location.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
object LocationMapper {

    private fun getByKey(location: FileLocation, key: RefactoringSubject) =
            when (key) {
                RefactoringSubject.FILE_NAME -> location.fileName
                RefactoringSubject.MODULE -> location.module
                RefactoringSubject.FILE_PATH -> location.path
                RefactoringSubject.SOURCE_ROOT -> location.sourceFolder
            }

    private fun updateByKey(location: FileLocation, key: RefactoringSubject, newValue: String): FileLocation =
            when (key) {
                RefactoringSubject.FILE_NAME -> location.copy(fileName = newValue)
                RefactoringSubject.MODULE -> location.copy(module = newValue)
                RefactoringSubject.FILE_PATH -> location.copy(path = newValue)
                RefactoringSubject.SOURCE_ROOT -> location.copy(sourceFolder = newValue)
            }

    private fun ruleMatches(location: FileLocation, rule : Map.Entry<RefactoringSubject, Step.Replacement>): Boolean =
            rule.value.regex.matches(getByKey(location, rule.key))

    private fun applyRule(location: FileLocation, rule : Map.Entry<RefactoringSubject, Step.Replacement>): FileLocation =
            updateByKey(location, rule.key, getByKey(location, rule.key).replace(rule.value.regex, rule.value.replacement))

    fun getStep(step: Step): RefactoringStep = { location: FileLocation ->

            // if all regexes match their corresponding components...
            if (step.replacements.all { ruleMatches(location, it) }) {

                // apply all rule components
                step.replacements.entries.fold(location, LocationMapper::applyRule)

            } else {
                // otherwise ignore this rule
                location
            }
    }
}