package de.qaware.refactobot.expander

import de.qaware.refactobot.integration.Refactobot
import de.qaware.refactobot.model.codebase.FileType
import de.qaware.refactobot.model.codebase.codebaseBuilder
import de.qaware.refactobot.util.slashify
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
                }
            }
            module("module_b") {
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
                renameFile("(.*)A.java", "$1B.java")
            }

        }

        val plan = SequentialExpander(codebase).expandRefactoringPlan(listOf(bot.config.refactoring))


        assertThat(codebase.allFiles.map { it.fullName.slashify() to plan[it]!!.fullName }.toMap())
                .isEqualTo(mapOf(
                        "module_a/src/main/java/org/example/codebase/a/AnnotationA.java" to "module_a/src/main/java/org/example/codebase/a/AnnotationB.java",
                        "module_a/src/main/java/org/example/codebase/a/ClassA.java" to "module_a/src/main/java/org/example/codebase/a/ClassB.java",
                        "module_a/src/main/java/org/example/codebase/a/InterfaceA.java" to "module_a/src/main/java/org/example/codebase/a/InterfaceB.java",
                        "module_a/src/main/java/org/example/codebase/a/other_file.properties" to "module_a/src/main/java/org/example/codebase/a/other_file.properties",
                        "module_a/src/test/java/org/example/codebase/a/TestClassA.java" to "module_a/src/test/java/org/example/codebase/a/TestClassB.java",
                        "module_b/src/main/java/org/example/codebase/b/ClassB.java" to "module_b/src/main/java/org/example/codebase/b/ClassB.java",
                        "module_b/src/main/java/org/example/codebase/b/EnumB.java" to "module_b/src/main/java/org/example/codebase/b/EnumB.java"
                ))

    }

}