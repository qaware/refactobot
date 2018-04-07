package de.qaware.refactobot.configuration

import de.qaware.refactobot.model.plan.RefactoringStep

/**
 * Builder/DSL for configurations.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class MutableConfiguration {

    var codebaseLocation : String? = null
    var steps : RefactoringStep = { it }
    var batchSize : Int = 80;
    var commitMessage : String = "Automated code reorganization"

    fun refactor(stepFn : RefactoringContext.() -> Unit) {
        steps = { location ->
            RefactoringContext(location).apply(stepFn).newLocation
        }
    }

    val configuration: Configuration
        get() = Configuration(codebaseLocation ?: throw IllegalStateException("Undefined configuration value codebaseLocation"),
                steps, batchSize, commitMessage)


}
