package de.qaware.refactobot.configuration

import de.qaware.refactobot.model.plan.RefactoringStep

/**
 * Root configuration object.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class Configuration(
        val codebaseLocation: String,
        val refactoring: RefactoringStep,
        val batchSize: Int,
        val commitMessage: String) {

}