package de.qaware.tools.bulkrename.scanner

import de.qaware.tools.bulkrename.model.codebase.File
import de.qaware.tools.bulkrename.model.codebase.FileType
import de.qaware.tools.bulkrename.model.codebase.Module
import de.qaware.tools.bulkrename.test.TestData
import org.junit.Test
import java.nio.file.Paths
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Test class for the MavenScanner implementation
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 * @author Florian Engel florian.engel@qaware.de
 */
class MavenScannerTest {

    @Test
    fun scanMavenCodebase() {
        val codebase = MavenScanner().scanCodebase(Paths.get(TestData.testCodebaseUri))

        // Modules
        val expectedModuleNames = listOf("maven_test_module_a", "maven_test_module_b")
        assertTrue("Names of modules must match ", { codebase.modules.map(Module::name).containsAll(expectedModuleNames) })
        assertTrue("Count of modules must match", { codebase.modules.count() == 2 })

        // Entities
        val module_a = codebase.modules.find { m -> m.name == "maven_test_module_a" }!!
        val mainFiles = module_a.sourceFolders.find { it.path == "src/main/java" }!!.files
        assertTrue("List must contain file", { mainFiles.containsFile("org/example/codebase/a/ClassA.java", FileType.JAVA) })
        assertTrue("List must contain file", { mainFiles.containsFile("org/example/codebase/a/InterfaceA.java", FileType.JAVA) })
        assertTrue("List must contain file", { mainFiles.containsFile("org/example/codebase/a/AnnotationA.java", FileType.JAVA) })
        assertTrue("List must contain file", { mainFiles.containsFile("org/example/codebase/a/other_file.properties", FileType.OTHER) })
        assertTrue("Count of files in list must match", { mainFiles.count() == 4 })

        val testFiles = module_a.sourceFolders.find { it.path == "src/test/java" }!!.files
        assertTrue("List must contain file", { testFiles.containsFile("org/example/codebase/a/TestClassA.java", FileType.JAVA) })
        assertTrue("List must contain file", { testFiles.containsFile("org/example/codebase/a/TestClassB.java", FileType.JAVA) })
        assertTrue("Count of files in list must match", { testFiles.count() == 2 })

        val module_b = codebase.modules.filter { m -> m.name == "maven_test_module_b" }.first()
        val mainFilesB = module_b.sourceFolders.find { it.path == "src/main/java" }!!.files
        assertTrue("List must contain file", { mainFilesB.containsFile("org/example/codebase/b/ClassB.java", FileType.JAVA) })
        assertTrue("List must contain file", { mainFilesB.containsFile("org/example/codebase/b/EnumB.java", FileType.JAVA) })
        assertTrue("Count of files in list must match", { mainFilesB.count() == 2 })

        assertNull(module_b.sourceFolders.find { it.path == "src/test/java" })
    }

    private fun List<File>.containsFile(fullPath: String, type: FileType): Boolean {
        return this.filter { f -> f.path.resolve(f.fileName) == Paths.get(fullPath) && f.type == type }.isNotEmpty()
    }

}