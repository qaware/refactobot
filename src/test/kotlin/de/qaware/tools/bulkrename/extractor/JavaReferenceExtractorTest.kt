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
 * Created by f.engel on 15.11.2016.
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
        val interfaceA = getFileByEntity(codebase, "org.example.codebase.a.InterfaceA")
        val classA = getFileByEntity(codebase, "org.example.codebase.a.ClassA")
        val testClassA = getFileByEntity(codebase, "org.example.codebase.a.TestClassA")

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

    private fun getFileByEntity(codebase: Codebase, fileName: String): File {
        return codebase.modules.flatMap { it.sourceFolders }.flatMap { it.files }.find { it.entity == fileName } ?: fail("Cannot find file.")
    }
}