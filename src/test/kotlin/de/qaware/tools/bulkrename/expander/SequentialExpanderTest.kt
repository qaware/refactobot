package de.qaware.tools.bulkrename.expander

import de.qaware.tools.bulkrename.model.codebase.*
import de.qaware.tools.bulkrename.model.plan.NewFileLocation
import de.qaware.tools.bulkrename.model.plan.RefactoringSubject
import de.qaware.tools.bulkrename.model.plan.SchematicRefactoringPlan
import de.qaware.tools.bulkrename.model.plan.Step
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.nio.file.Paths

/**
 * Test for SequentialExpander
 *
 * @author Florian Engel florian.engel@qaware.de
 */
class SequentialExpanderTest{

    @Test
    fun testExpandRefactoringPlan(){

        // Create two steps which build upon each other
        val sourceExpressions1 = mapOf(
            Pair(RefactoringSubject.MODULE_NAME, Regex("foo")),
            Pair(RefactoringSubject.FILE_NAME, Regex("([a-zA-Z]*)DTO.java")),
            Pair(RefactoringSubject.FILE_PATH, Regex("""de\\qaware\\tools\\bulkrename\\([a-zA-Z\\]*)"""))
        )
        val targetExpressions1 = mapOf(
                Pair(RefactoringSubject.MODULE_NAME, Regex("FOO")),
                Pair(RefactoringSubject.FILE_NAME, Regex("$1Dto.java")),
                Pair(RefactoringSubject.FILE_PATH, Regex("""de\\qaware\\tools\\bulkrename\\foobar\\$1"""))
        )
        val sourceExpressions2 = mapOf(
                Pair(RefactoringSubject.MODULE_NAME, Regex("FOO")),
                Pair(RefactoringSubject.MODULE_PATH, Regex("src/main/java")),
                Pair(RefactoringSubject.FILE_NAME, Regex("([a-zA-Z]*)Dto.java")),
                Pair(RefactoringSubject.FILE_PATH, Regex("""de\\qaware\\tools\\bulkrename\\foobar\\([a-zA-Z\\]*)"""))
        )
        val targetExpressions2 = mapOf(
                Pair(RefactoringSubject.MODULE_NAME, Regex("foobar")),
                Pair(RefactoringSubject.MODULE_PATH, Regex("src/test/java")),
                Pair(RefactoringSubject.FILE_NAME, Regex("$1Bean.java")),
                Pair(RefactoringSubject.FILE_PATH, Regex("""de\\qaware\\tools\\bulkrename\\foobar\\$1\\beans"""))
        )
        val steps = listOf(
                Step(sourceExpressions1, targetExpressions1),
                Step(sourceExpressions2, targetExpressions2)
        )

        val refactoringPlan = SchematicRefactoringPlan(steps)

        // Create two files, file1 should be matched and transformed, file 2 should not be matched.
        val codebaseRoot = Paths.get("/home/tester/project/bulkrename/")
        val sourceRoot = codebaseRoot.resolve("src/main/java")
        val fullPath = sourceRoot.resolve("de/qaware/tools/bulkrename/test")

        val file1 = File(fullPath, Paths.get("de/qaware/tools/bulkrename/test"), "TestDTO.java", FileType.JAVA)
        val file2 = File(fullPath, Paths.get("de/qaware/tools/bulkrename/test"), "TestEntity.java", FileType.JAVA)

        // The module foo, which contains the files
        val moduleFoo = Module(name = "foo", modulePath = Paths.get("modules/foo"), sourceFolders = listOf(
                SourceFolder("src/main/java", listOf(file1, file2))
        ))
        // An empty target module
        val moduleFoobar = Module(name = "foobar", modulePath = Paths.get("modules/foobar"), sourceFolders = listOf(
                SourceFolder("src/test/java", files = emptyList())
        ))

        val codebase = Codebase(rootPath = codebaseRoot,
                modules = listOf(moduleFoo, moduleFoobar))

        // Execute
        val actualResult = SequentialExpander(codebase).expandRefactoringPlan(refactoringPlan)

        val expectedResult = mapOf(
                Pair(file1, NewFileLocation(moduleFoobar, moduleFoobar.sourceFolders[0], Paths.get("de/qaware/tools/bulkrename/foobar/test/beans"), "TestBean.java")),
                Pair(file2, NewFileLocation(moduleFoo, moduleFoo.sourceFolders[0], file2.path, file2.fileName)) // unchanged
        )

        assertThat(actualResult).isEqualTo(expectedResult);
    }
}
