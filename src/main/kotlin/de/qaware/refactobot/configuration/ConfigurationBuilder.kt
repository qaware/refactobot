package de.qaware.refactobot.configuration

import de.qaware.refactobot.model.plan.RefactoringSubject
import de.qaware.refactobot.model.plan.SchematicRefactoringPlan
import de.qaware.refactobot.model.plan.Step

/**
 * Builder/DSL for configurations.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class ConfigurationBuilder {

    val steps : MutableList<Step> = mutableListOf()

    fun build(init: ConfigurationBuilder.() -> Unit): Configuration {

        this.init()


        return Configuration("", SchematicRefactoringPlan(steps.toList()))
    }

    fun steps(initSteps: StepsBuilder.() -> Unit) {

        StepsBuilder().initSteps()


    }

    class StepsBuilder() {

        val steps : MutableList<Step> = mutableListOf()

        fun rename(renamePair: Pair<String, String>) {
            steps += Step(mapOf(Pair(RefactoringSubject.FILE_NAME,
                    Step.Replacement(Regex(renamePair.first), renamePair.second))))
        }

    }

}
