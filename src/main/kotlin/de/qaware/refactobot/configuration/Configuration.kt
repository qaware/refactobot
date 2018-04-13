package de.qaware.refactobot.configuration

import de.qaware.refactobot.model.plan.RefactoringMapping

/**
 * Root configuration object.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class Configuration(
        val refactoring: RefactoringMapping,
        val batchSize: Int,
        val commitMessage: String) {

}