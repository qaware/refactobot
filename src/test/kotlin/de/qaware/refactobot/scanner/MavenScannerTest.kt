package de.qaware.refactobot.scanner

import de.qaware.refactobot.model.codebase.FileType
import de.qaware.refactobot.model.codebase.codebaseBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.nio.file.Paths

/**
 * Test class for the MavenScanner implementation
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 * @author Florian Engel florian.engel@qaware.de
 */
class MavenScannerTest {

    val rootPath = Paths.get(javaClass.classLoader.getResource("integration_test/example_codebase").toURI())

    val expectedCodebase = codebaseBuilder(rootPath) {
            module("maven_test_module_a") {
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
    fun scanMavenCodebase() {
        assertThat(MavenScanner().scanCodebase(rootPath)).isEqualTo(expectedCodebase)
   }

}