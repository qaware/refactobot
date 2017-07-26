package de.qaware.refactobot.integration

import de.qaware.refactobot.executor.batch.BatchedActionExecutor
import de.qaware.refactobot.executor.git.GitActionExecutor
import de.qaware.refactobot.executor.git.impl.JgitRepositoryFactory
import de.qaware.refactobot.expander.SequentialExpander
import de.qaware.refactobot.extractor.java.JavaReferenceExtractor
import de.qaware.refactobot.extractor.text.TextReferenceExtractor
import de.qaware.refactobot.model.plan.SchematicRefactoringPlan
import de.qaware.refactobot.planner.ActionPlannerImpl
import de.qaware.refactobot.scanner.MavenScanner
import java.nio.file.Path

/**
 * Integration of the individual components into a tool.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
object BulkRenameTool {

    fun refactorMavenCodebase(root : Path, refactoringPlan: SchematicRefactoringPlan, commitMsg: String, batchSize: Int) {

        println("Scanning codebase...")
        val codebase = MavenScanner().scanCodebase(root)
        println("Found ${codebase.modules.size} modules.")

        println("Expanding refactoring plan...")
        val fullRefactoringPlan = SequentialExpander(codebase).expandRefactoringPlan(refactoringPlan)

        println("Extracting references...")
        val references =
                JavaReferenceExtractor().extractReferences(codebase) +
                TextReferenceExtractor().extractReferences(codebase)

        println("Found ${references.size} references.")

        println("Planning actions...")
        val operations = ActionPlannerImpl().planActions(fullRefactoringPlan, references)

        println("Executing actions...")
        BatchedActionExecutor(batchSize, GitActionExecutor(JgitRepositoryFactory(), root))
                .execute(operations, commitMsg)
    }

}
