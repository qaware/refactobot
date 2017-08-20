package de.qaware.refactobot.configuration

import de.qaware.refactobot.model.plan.old.SchematicRefactoringPlan
import de.qaware.refactobot.model.plan.old.Step

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


    }

}
