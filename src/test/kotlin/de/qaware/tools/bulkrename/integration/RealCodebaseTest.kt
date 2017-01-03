package de.qaware.tools.bulkrename.integration

import de.qaware.tools.bulkrename.model.plan.RefactoringSubject
import de.qaware.tools.bulkrename.model.plan.SchematicRefactoringPlan
import de.qaware.tools.bulkrename.model.plan.Step
import org.junit.Test
import java.nio.file.Paths

/**
 * Tests on real codebases.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class RealCodebaseTest {

    @Test
    fun testPsa() {

        BulkRenameTool.refactorMavenCodebase(Paths.get("P:/codebase2/code"),
                SchematicRefactoringPlan(listOf(
                        renameStep(Regex("(.*)DaoBean\\.java"), "$1DaoImpl.java")
                )))
    }


    private fun renameStep(from: Regex, to: String) =
            Step(mapOf(Pair(RefactoringSubject.FILE_NAME, Step.Replacement(from, to))))

}
