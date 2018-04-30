package de.qaware.refactobot.configuration

import de.qaware.refactobot.model.plan.RefactoringMapping

/**
 * Builder/DSL for configurations.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class MutableConfiguration {

    var steps : RefactoringMapping = { it }
    var batchSize : Int = 80;
    var commitMessage : String? = null

    fun doGitCommits(msg: String, batchSize: Int) {
        commitMessage = msg
        this.batchSize = batchSize
    }

    fun refactor(stepFn : RefactoringContext.() -> Unit) {
        steps = { location ->
            RefactoringContext(location).apply(stepFn).newLocation
        }
    }

    val configuration: Configuration
        get() = Configuration(steps, batchSize, commitMessage)


}
