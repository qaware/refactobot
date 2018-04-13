package de.qaware.refactobot.expander

import de.qaware.refactobot.integration.Refactobot
import de.qaware.refactobot.model.codebase.File
import de.qaware.refactobot.model.codebase.FileType
import de.qaware.refactobot.model.codebase.codebaseBuilder
import de.qaware.refactobot.model.plan.FileLocation
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.nio.file.Paths

/**
 * Test for the expansion process.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class ExpanderTest {

    val codebase = codebaseBuilder(Paths.get("codebase")) {
            module("module_a") {
                sourceFolder("src/main/java") {
                    file("org/example/codebase/a/AnnotationA.java", FileType.JAVA)
                    file("org/example/codebase/a/ClassA.java", FileType.JAVA)
                    file("org/example/codebase/a/InterfaceA.java", FileType.JAVA)
                    file("org/example/codebase/a/other_file.properties", FileType.OTHER)
                }
                sourceFolder("src/test/java") {
                    file("org/example/codebase/a/TestClassA.java", FileType.JAVA)
                    file("org/example/codebase/a/TestClassB.java", FileType.JAVA)
                }
            }
            module("maven_test_module_b") {
                sourceFolder("src/main/java") {
                    file("org/example/codebase/b/ClassB.java", FileType.JAVA)
                    file("org/example/codebase/b/EnumB.java", FileType.JAVA)
                }
            }
        }

    @Test
    fun testExpander() {

        val bot = Refactobot.configure {

            refactor {
                if (fileName == "AnnotationA.java") {
                    fileName = "AnnotationB.java"
                }

            }

        }

        val plan: Map<File, FileLocation> = SequentialExpander(codebase).expandRefactoringPlan(listOf(bot.config.refactoring))
        val annFile = codebase.allFiles.find { it.fileName == "AnnotationA.java" }!!
        assertThat(plan[annFile]!!.fileName).isEqualTo("AnnotationB.java")

    }

}