package de.qaware.refactobot.integration

import de.qaware.refactobot.configuration.Configuration
import de.qaware.refactobot.configuration.MutableConfiguration
import de.qaware.refactobot.executor.batch.BatchedActionExecutor
import de.qaware.refactobot.executor.git.GitActionExecutor
import de.qaware.refactobot.executor.git.impl.JgitRepositoryFactory
import de.qaware.refactobot.expander.SequentialExpander
import de.qaware.refactobot.extractor.java.JavaReferenceExtractor
import de.qaware.refactobot.extractor.text.TextReferenceExtractor
import de.qaware.refactobot.planner.ActionPlannerImpl
import de.qaware.refactobot.scanner.MavenScanner
import java.nio.file.Paths

/**
 * Integration of the individual components into a tool.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class Refactobot(val config: Configuration) {

    /**
     * Runs the refactoring, based on the given configuration.
     */
    fun run(codebaseLocation: String) {

        val root = Paths.get(codebaseLocation)

        println("Scanning codebase...")
        val codebase = MavenScanner().scanCodebase(root)
        println("Found ${codebase.modules.size} modules.")

        println("Expanding refactoring plan...")
        val fullRefactoringPlan = SequentialExpander(codebase).expandRefactoringPlan(listOf(config.refactoring))

        println("Extracting references...")
        val references =
                JavaReferenceExtractor().extractReferences(codebase) +
                TextReferenceExtractor().extractReferences(codebase)

        println("Found ${references.size} references.")

        println("Planning actions...")
        val operations = ActionPlannerImpl().planActions(fullRefactoringPlan, references)

        println("Executing actions...")
        BatchedActionExecutor(config.batchSize, GitActionExecutor(JgitRepositoryFactory(), root))
                .execute(operations, config.commitMessage)
    }

    companion object {

        /**
         * Main entry function to construct a configuration object.
         */
        fun configure(configFn: MutableConfiguration.() -> Unit): Refactobot =
                Refactobot(MutableConfiguration().apply(configFn).configuration)
    }

}
