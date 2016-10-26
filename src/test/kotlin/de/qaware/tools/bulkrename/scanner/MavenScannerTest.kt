package de.qaware.tools.bulkrename.scanner

import de.qaware.tools.bulkrename.model.codebase.File
import de.qaware.tools.bulkrename.model.codebase.FileType
import de.qaware.tools.bulkrename.model.codebase.Module
import org.junit.Test
import java.nio.file.Paths
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
        val testFolderLocation = this.javaClass.classLoader.getResource("maven_test_modules")
        val codebase = MavenScanner().scanCodebase(Paths.get(testFolderLocation.toURI()))

        // Modules
        val expectedModuleNames = listOf("maven_test_module_a", "maven_test_module_b")
        assertTrue("Names of modules must match ", { codebase.modules.map(Module::name).containsAll(expectedModuleNames) })
        assertTrue("Count of modules must match", { codebase.modules.count() == 2 })

        // Entities
        val module_a = codebase.modules.filter { m -> m.name == "maven_test_module_a" }.first()
        assertTrue("List must contain file", { containsFile(module_a.mainFiles, "ClassA.java", "de.qaware.tools.bulkrename.scanner.test.a.ClassA", FileType.JAVA) })
        assertTrue("List must contain file", { containsFile(module_a.mainFiles, "InterfaceA.java", "de.qaware.tools.bulkrename.scanner.test.a.InterfaceA", FileType.JAVA) })
        assertTrue("List must contain file", { containsFile(module_a.mainFiles, "AnnotationA.java", "de.qaware.tools.bulkrename.scanner.test.a.AnnotationA", FileType.JAVA) })
        assertTrue("List must contain file", { containsFile(module_a.mainFiles, "other_file", "other_file", FileType.OTHER) })
        assertTrue("Count of files in list must match", { module_a.mainFiles.count() == 4 })

        assertTrue("List must contain file", { containsFile(module_a.testFiles, "TestClassA.java", "de.qaware.tools.bulkrename.scanner.test.a.TestClassA", FileType.JAVA) })
        assertTrue("List must contain file", { containsFile(module_a.testFiles, "TestClassB.java", "de.qaware.tools.bulkrename.scanner.test.a.TestClassB", FileType.JAVA) })
        assertTrue("Count of files in list must match", { module_a.testFiles.count() == 2 })

        val module_b = codebase.modules.filter { m -> m.name == "maven_test_module_b" }.first()
        assertTrue("List must contain file", { containsFile(module_b.mainFiles, "ClassB.java", "de.qaware.tools.bulkrename.scanner.test.b.ClassB", FileType.JAVA) })
        assertTrue("List must contain file", { containsFile(module_b.mainFiles, "EnumB.java", "de.qaware.tools.bulkrename.scanner.test.b.EnumB", FileType.JAVA) })
        assertTrue("Count of files in list must match", { module_b.mainFiles.count() == 2 })

        assertTrue("List must be empty", { module_b.testFiles.isEmpty() })
    }

    private fun containsFile(files: Iterable<File>, name: String, entity: String, type: FileType): Boolean {
        return files.filter { f -> f.fileName == name && f.entity == entity && f.type == type }.isNotEmpty()
    }

}