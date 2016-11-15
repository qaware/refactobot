package de.qaware.tools.bulkrename.expander

import de.qaware.tools.bulkrename.model.codebase.Codebase
import de.qaware.tools.bulkrename.model.codebase.FileType
import de.qaware.tools.bulkrename.model.codebase.Module
import de.qaware.tools.bulkrename.model.plan.RefactoringSubject
import de.qaware.tools.bulkrename.model.plan.SchematicRefactoringPlan
import de.qaware.tools.bulkrename.model.plan.Step
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse

/**
 * Test class for SequentialExpander
 *
 * @author Florian Engel florian.engel@qaware.de
 */

class SequentialExpanderTest{

    @Test
    fun testExpandRefactoringPlan(){

        // Create two steps which build upon each other
        val sourceExpressions1 = hashMapOf(
            Pair(RefactoringSubject.MODULE_NAME, Regex("foo")),
            Pair(RefactoringSubject.MODULE_PATH, Regex("bar")),
            Pair(RefactoringSubject.FILE_NAME, Regex("([a-zA-Z]*)DTO.java")),
            Pair(RefactoringSubject.FILE_PATH, Regex("""src\\main\\de\\qaware\\tools\\bulkrename\\([a-zA-Z\\]*)"""))
        )
        val targetExpressions1 = hashMapOf(
                Pair(RefactoringSubject.MODULE_NAME, Regex("FOO")),
                Pair(RefactoringSubject.MODULE_PATH, Regex("BAR")),
                Pair(RefactoringSubject.FILE_NAME, Regex("$1Dto.java")),
                Pair(RefactoringSubject.FILE_PATH, Regex("""src\\main\\de\\qaware\\tools\\bulkrename\\foobar\\$1"""))
        )
        val sourceExpressions2 = hashMapOf(
                Pair(RefactoringSubject.MODULE_NAME, Regex("FOO")),
                Pair(RefactoringSubject.MODULE_PATH, Regex("BAR")),
                Pair(RefactoringSubject.FILE_NAME, Regex("([a-zA-Z]*)Dto.java")),
                Pair(RefactoringSubject.FILE_PATH, Regex("""src\\main\\de\\qaware\\tools\\bulkrename\\foobar\\([a-zA-Z\\]*)"""))
        )
        val targetExpressions2 = hashMapOf(
                Pair(RefactoringSubject.MODULE_NAME, Regex("foobar")),
                Pair(RefactoringSubject.MODULE_PATH, Regex("barfoo")),
                Pair(RefactoringSubject.FILE_NAME, Regex("$1Bean.java")),
                Pair(RefactoringSubject.FILE_PATH, Regex("""src\\main\\de\\qaware\\tools\\bulkrename\\foobar\\$1\\beans"""))
        )
        val steps = listOf(
                Step(sourceExpressions1, targetExpressions1),
                Step(sourceExpressions2, targetExpressions2)
        )

        val refactoringPlan = SchematicRefactoringPlan(steps)

        // Create two files, file1 should be matched and transformed, file 2 should not be matched.
        val path1 = File("src/main/de/qaware/tools/bulkrename/test").toPath()
        val name1 = "TestDTO.java"
        val entity1 = "de.qaware.tools.bulkrename.test.TestDTO"
        val type1 = FileType.JAVA

        val file1 = de.qaware.tools.bulkrename.model.codebase.File(path1, name1, entity1, type1)

        val path2 = File("src/main/de/qaware/tools/bulkrename/test").toPath()
        val name2 = "TestEntity.java"
        val entity2 = "de.qaware.tools.bulkrename.test.TestEntity"
        val type2 = FileType.JAVA

        val file2 = de.qaware.tools.bulkrename.model.codebase.File(path2, name2, entity2, type2)

        val moduleName = "foo"
        val modulePath = File("bar").toPath()
        val mainFiles = listOf(file1, file2)
        val testFiles = emptyList<de.qaware.tools.bulkrename.model.codebase.File>()

        val module = Module(moduleName, modulePath, mainFiles, testFiles)

        val codebasePath = File("/home/tester/project/bulkrename/").toPath()
        val codebase = Codebase(codebasePath, listOf(module))

        // Execute
        val sequentialExpander = SequentialExpander()
        val testResult = sequentialExpander.expandRefactoringPlan(refactoringPlan, codebase)

        val refactoringInfoFile1 = testResult.steps[file1]!!

        // Expected changes
        assertEquals("TestBean.java", refactoringInfoFile1.targets[RefactoringSubject.FILE_NAME])
        assertEquals("src\\main\\de\\qaware\\tools\\bulkrename\\foobar\\test\\beans", refactoringInfoFile1.targets[RefactoringSubject.FILE_PATH])
        assertEquals("foobar", refactoringInfoFile1.targets[RefactoringSubject.MODULE_NAME])
        assertEquals("barfoo", refactoringInfoFile1.targets[RefactoringSubject.MODULE_PATH])

        // No transformation was defined for entity, so no target is expected
        assertFalse(refactoringInfoFile1.targets.containsKey(RefactoringSubject.ENTITY))

        // Second file should not match our source matchers, so there should be no targets
        val refactoringInfoFile2 = testResult.steps[file2]
        assertEquals(0, refactoringInfoFile2!!.targets.size)

    }
}
