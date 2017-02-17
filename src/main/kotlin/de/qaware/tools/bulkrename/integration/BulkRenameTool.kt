package de.qaware.tools.bulkrename.integration

import de.qaware.tools.bulkrename.executor.Executor
import de.qaware.tools.bulkrename.expander.SequentialExpander
import de.qaware.tools.bulkrename.extractor.java.JavaReferenceExtractor
import de.qaware.tools.bulkrename.extractor.text.TextReferenceExtractor
import de.qaware.tools.bulkrename.model.plan.SchematicRefactoringPlan
import de.qaware.tools.bulkrename.planner.ActionPlannerImpl
import de.qaware.tools.bulkrename.scanner.MavenScanner
import java.nio.file.Path

/**
 * Integration of the individual components into a tool.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
object BulkRenameTool {

    fun refactorMavenCodebase(root : Path, refactoringPlan: SchematicRefactoringPlan) {

        println("Scanning codebase...")
        val codebase = MavenScanner().scanCodebase(root)
        println("Found ${codebase.modules.size} modules.")

        println("Extracting references...")
        val references =
                JavaReferenceExtractor().extractReferences(codebase) +
                TextReferenceExtractor().extractReferences(codebase)

        println("Found ${references.size} references.")

        println("Expanding refactoring plan...")
        val fullRefactoringPlan = SequentialExpander(codebase).expandRefactoringPlan(refactoringPlan)

        println("Planning actions...")
        val operations = ActionPlannerImpl().planActions(fullRefactoringPlan, references)

        println("Executing actions...")
        val executor = Executor(root)
        operations.forEach { executor.execute(it) }

    }

}
