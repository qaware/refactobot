package de.qaware.repackager.integration

import de.qaware.repackager.executor.batch.BatchedActionExecutor
import de.qaware.repackager.executor.git.GitActionExecutor
import de.qaware.repackager.executor.git.impl.JgitRepositoryFactory
import de.qaware.repackager.expander.SequentialExpander
import de.qaware.repackager.extractor.java.JavaReferenceExtractor
import de.qaware.repackager.extractor.text.TextReferenceExtractor
import de.qaware.repackager.model.plan.SchematicRefactoringPlan
import de.qaware.repackager.planner.ActionPlannerImpl
import de.qaware.repackager.scanner.MavenScanner
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
