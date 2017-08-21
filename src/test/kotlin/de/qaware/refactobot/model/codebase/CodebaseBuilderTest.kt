package de.qaware.refactobot.model.codebase

import org.junit.Test
import java.nio.file.Paths

/**
 * Test for Codebase builder syntax.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class CodebaseBuilderTest {

    @Test
    fun buildCodebase() {

        codebaseBuilder(Paths.get(".")) {
            module("module_a") {
                sourceFolder("src/main/java") {
                    file("org/example/Class1.java", FileType.JAVA)
                    file("org/example/Class2.java", FileType.JAVA)
                }
                sourceFolder("src/test/java") {
                    file("org/example/Class1Test.java", FileType.JAVA)
                    file("org/example/Class2Test.java", FileType.JAVA)
                }
            }
        }

    }

}