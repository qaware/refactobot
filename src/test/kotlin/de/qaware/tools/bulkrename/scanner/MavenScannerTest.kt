package de.qaware.tools.bulkrename.scanner

import de.qaware.tools.bulkrename.model.codebase.File
import de.qaware.tools.bulkrename.model.codebase.FileType
import de.qaware.tools.bulkrename.model.codebase.Module
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
        val testFolderLocation = this.javaClass.classLoader.getResource("maven_test_modules")
        val codebase = MavenScanner().scanCodebase(Paths.get(testFolderLocation.toURI()))

        // Modules
        val expectedModuleNames = listOf("maven_test_module_a", "maven_test_module_b")
        assertTrue("Names of modules must match ", { codebase.modules.map(Module::name).containsAll(expectedModuleNames) })
        assertTrue("Count of modules must match", { codebase.modules.count() == 2 })

        // Entities
        val module_a = codebase.modules.find { m -> m.name == "maven_test_module_a" }!!
        val mainFiles = module_a.sourceFolders.find { it.path == "src/main/java" }!!.files
        assertTrue("List must contain file", { mainFiles.containsFile("ClassA.java", "org.example.codebase.a.ClassA", FileType.JAVA, "org/example/codebase/a") })
        assertTrue("List must contain file", { mainFiles.containsFile("InterfaceA.java", "org.example.codebase.a.InterfaceA", FileType.JAVA, "org/example/codebase/a") })
        assertTrue("List must contain file", { mainFiles.containsFile("AnnotationA.java", "org.example.codebase.a.AnnotationA", FileType.JAVA, "org/example/codebase/a") })
        assertTrue("List must contain file", { mainFiles.containsFile("other_file", "", FileType.OTHER, "org/example/codebase/a") })
        assertTrue("Count of files in list must match", { mainFiles.count() == 4 })

        val testFiles = module_a.sourceFolders.find { it.path == "src/test/java" }!!.files
        assertTrue("List must contain file", { testFiles.containsFile("TestClassA.java", "org.example.codebase.a.TestClassA", FileType.JAVA, "org/example/codebase/a") })
        assertTrue("List must contain file", { testFiles.containsFile("TestClassB.java", "org.example.codebase.a.TestClassB", FileType.JAVA, "org/example/codebase/a") })
        assertTrue("Count of files in list must match", { testFiles.count() == 2 })

        val module_b = codebase.modules.filter { m -> m.name == "maven_test_module_b" }.first()
        val mainFilesB = module_b.sourceFolders.find { it.path == "src/main/java" }!!.files
        assertTrue("List must contain file", { mainFilesB.containsFile("ClassB.java", "org.example.codebase.b.ClassB", FileType.JAVA, "org/example/codebase/b") })
        assertTrue("List must contain file", { mainFilesB.containsFile("EnumB.java", "org.example.codebase.b.EnumB", FileType.JAVA, "org/example/codebase/b") })
        assertTrue("Count of files in list must match", { mainFilesB.count() == 2 })

        assertNull(module_b.sourceFolders.find { it.path == "src/test/java" })
    }



    private fun List<File>.containsFile(name: String, entity: String, type: FileType, path: String): Boolean {
        return this.filter { f -> f.fileName == name && f.entity == entity && f.type == type && f.path == Paths.get(path) }.isNotEmpty()
    }

}