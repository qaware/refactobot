package de.qaware.tools.bulkrename.integration

import de.qaware.tools.bulkrename.executor.Executor
import de.qaware.tools.bulkrename.expander.SequentialExpander
import de.qaware.tools.bulkrename.extractor.JavaReferenceExtractor
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

        val codebase = MavenScanner().scanCodebase(root)

        val fullRefactoringPlan = SequentialExpander(codebase).expandRefactoringPlan(refactoringPlan)
        val references = JavaReferenceExtractor().extractReferences(codebase)
        val operations = ActionPlannerImpl().planActions(fullRefactoringPlan, references)

        val executor = Executor(root)
        operations.forEach { executor.execute(it) }

    }

}
