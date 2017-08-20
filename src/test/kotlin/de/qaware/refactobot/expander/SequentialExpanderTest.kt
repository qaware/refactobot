package de.qaware.refactobot.expander

import de.qaware.refactobot.model.codebase.*
import de.qaware.refactobot.model.plan.FileLocation
import de.qaware.refactobot.model.plan.RefactoringSubject
import de.qaware.refactobot.model.plan.SchematicRefactoringPlan
import de.qaware.refactobot.model.plan.Step
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.nio.file.Paths

/**
 * Test for SequentialExpander
 *
 * @author Florian Engel florian.engel@qaware.de
 */
class SequentialExpanderTest{

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



    @Test
    fun testExpandRefactoringPlan(){

        val refactoringPlan = SchematicRefactoringPlan(listOf(
                Step(mapOf(
                        Pair(RefactoringSubject.MODULE, Step.Replacement(Regex("modules/foo"), "modules/FOO")),
                        Pair(RefactoringSubject.FILE_NAME, Step.Replacement(Regex("([a-zA-Z]*)DTO.java"), "$1Dto.java")),
                        Pair(RefactoringSubject.FILE_PATH, Step.Replacement(Regex("""de/qaware/tools/bulkrename/([a-zA-Z/]*)"""), """de/qaware/tools/bulkrename/foobar/$1"""))
                )),
                Step(mapOf(
                        Pair(RefactoringSubject.MODULE, Step.Replacement(Regex("modules/FOO"), "modules/foobar")),
                        Pair(RefactoringSubject.SOURCE_ROOT, Step.Replacement(Regex("src/main/java"), "src/test/java")),
                        Pair(RefactoringSubject.FILE_NAME, Step.Replacement(Regex("([a-zA-Z]*)Dto.java"), "$1Bean.java")),
                        Pair(RefactoringSubject.FILE_PATH, Step.Replacement(Regex("""de/qaware/tools/bulkrename/foobar/([a-zA-Z/]*)"""), """de/qaware/tools/bulkrename/foobar/$1/beans"""))
                ))
        ))

        // Execute
        val actualResult = SequentialExpander(codebase).expandRefactoringPlan(refactoringPlan)

        val expectedResult = mapOf(
                Pair(file1, FileLocation(moduleFoobar, moduleFoobar.sourceFolders[0], Paths.get("de/qaware/tools/bulkrename/foobar/test/beans"), "TestBean.java")),
                Pair(file2, FileLocation(moduleFoo, moduleFoo.sourceFolders[0], file2.path, file2.fileName)) // unchanged
        )

        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun testPlainRename(){

        val refactoringPlan = SchematicRefactoringPlan(listOf(
                Step(mapOf(
                        Pair(RefactoringSubject.FILE_NAME, Step.Replacement(Regex("([a-zA-Z]*)DTO.java"), "$1Dto.java"))
                ))
        ))

        // Execute
        val actualResult = SequentialExpander(codebase).expandRefactoringPlan(refactoringPlan)

        val expectedResult = mapOf(
                Pair(file1, FileLocation(moduleFoo, moduleFoo.sourceFolders[0], Paths.get("de/qaware/tools/bulkrename/test"), "TestDto.java")),
                Pair(file2, FileLocation(moduleFoo, moduleFoo.sourceFolders[0], file2.path, file2.fileName)) // unchanged
        )

        assertThat(actualResult).isEqualTo(expectedResult)
    }


}
