package de.qaware.tools.bulkrename.extractor

import de.qaware.tools.bulkrename.model.codebase.Codebase
import de.qaware.tools.bulkrename.model.codebase.File
import de.qaware.tools.bulkrename.scanner.MavenScanner
import org.junit.Test
import java.nio.file.Paths
import kotlin.test.fail

/**
 * Test class for the JavaReferenceExtractor.
 *
 * @author Florian Engel florian.engel@qaware.de
 */
class JavaReferenceExtractorTest {

    @Test
    fun testExtractReferences() {

        // Read resources/maven_test_modules as codebase
        val testFolderLocation = this.javaClass.classLoader.getResource("maven_test_modules")
        val codebase = MavenScanner().scanCodebase(Paths.get(testFolderLocation.toURI()))

        // Extract references
        val references = JavaReferenceExtractor().extractReferences(codebase)

        // Check if all references are identified correctly
        val interfaceA = getFileByEntity(codebase, "InterfaceA.java")
        val classA = getFileByEntity(codebase, "ClassA.java")
        val testClassA = getFileByEntity(codebase, "TestClassA.java")

//        assertEquals(2, references[interfaceA]!!.size)
//        assertTrue { references[interfaceA]!!.containsAll(setOf(classA, testClassA)) }
//
//        val testClassB = getFileByEntity(codebase, "org.example.codebase.a.TestClassB")
//        assertEquals(1, references[testClassB]!!.size)
//        assertTrue { references[testClassB]!!.contains(testClassA) }
//
//        val enumB = getFileByEntity(codebase, "org.example.codebase.b.EnumB")
//        val classB = getFileByEntity(codebase, "org.example.codebase.b.ClassB")
//        assertEquals(1, references[enumB]!!.size)
//        assertTrue { references[enumB]!!.contains(classB) }
//
//        // Check if no additional (and therefore false) references have been identified
//        val filesWithoutIncomingReferences = references.filterKeys { !setOf(interfaceA, testClassB, enumB).contains(it) }
//        assertTrue { filesWithoutIncomingReferences.filter { it.value.isNotEmpty() }.isEmpty() }
    }

    private fun getFileByEntity(codebase: Codebase, fileName: String): File =
            codebase.modules.flatMap { it.sourceFolders }.flatMap { it.files }.find { it.fileName == fileName } ?: fail("Cannot find file.")

}