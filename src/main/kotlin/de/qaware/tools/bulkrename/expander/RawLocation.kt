package de.qaware.tools.bulkrename.expander

import de.qaware.tools.bulkrename.model.plan.RefactoringSubject
import de.qaware.tools.bulkrename.model.plan.Step

/**
 * Intermediate state type that describes a new (or intermediate) location of a file.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
data class RawLocation(
        val module: String,
        val sourceRoot: String,
        val path: String,
        val filename: String
) {
    private fun getByKey(key: RefactoringSubject) =
            when (key) {
                RefactoringSubject.FILE_NAME -> filename
                RefactoringSubject.MODULE -> module
                RefactoringSubject.FILE_PATH -> path
                RefactoringSubject.SOURCE_ROOT -> sourceRoot
            }

    private fun updateByKey(key: RefactoringSubject, newValue: String): RawLocation =
            when (key) {
                RefactoringSubject.FILE_NAME -> this.copy(filename = newValue)
                RefactoringSubject.MODULE -> this.copy(module = newValue)
                RefactoringSubject.FILE_PATH -> this.copy(path = newValue)
                RefactoringSubject.SOURCE_ROOT -> this.copy(sourceRoot = newValue)
            }

    private fun ruleMatches(rule : Map.Entry<RefactoringSubject, Step.Replacement>): Boolean =
            rule.value.regex.matches(getByKey(rule.key))

    private fun applyRule(rule : Map.Entry<RefactoringSubject, Step.Replacement>): RawLocation =
            updateByKey(rule.key, getByKey(rule.key).replace(rule.value.regex, rule.value.replacement))

    fun applyStep(step: Step): RawLocation =

            // if all regexes match their corresponding components...
            if (step.replacements.all { ruleMatches(it) }) {

                // apply all rule components
                step.replacements.entries.fold(this, RawLocation::applyRule)

            } else {
                // otherwise ignore this rule
                this;
            }

}
