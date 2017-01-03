package de.qaware.tools.bulkrename.extractor

import de.qaware.tools.bulkrename.model.codebase.Codebase
import de.qaware.tools.bulkrename.model.codebase.File
import de.qaware.tools.bulkrename.model.operation.Location
import de.qaware.tools.bulkrename.model.operation.Span
import de.qaware.tools.bulkrename.model.reference.JavaQualifiedTypeReference
import de.qaware.tools.bulkrename.model.reference.JavaSimpleTypeReference
import de.qaware.tools.bulkrename.scanner.MavenScanner
import org.assertj.core.api.Assertions.assertThat
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
        val extractedReferences = JavaReferenceExtractor().extractReferences(codebase).toSet()

        // Expectation
        val interfaceA = getFileByEntity(codebase, "InterfaceA.java")
        val classA = getFileByEntity(codebase, "ClassA.java")
        val classB = getFileByEntity(codebase, "ClassB.java")
        val enumB = getFileByEntity(codebase, "EnumB.java")
        val testClassA = getFileByEntity(codebase, "TestClassA.java")
        val testClassB = getFileByEntity(codebase, "TestClassB.java")

        val expectedReferences = setOf(
                JavaQualifiedTypeReference(classA, interfaceA, Span(Location(3, 8), Location(3, 40))),
                JavaSimpleTypeReference(classA, interfaceA, Span(Location(5, 32), Location(5, 41))),
                JavaSimpleTypeReference(classA, interfaceA, Span(Location(7, 13), Location(7, 22))),
                JavaQualifiedTypeReference(testClassA, testClassB, Span(Location(2, 8), Location(2, 40))),
                JavaSimpleTypeReference(testClassA, testClassB, Span(Location(6, 13), Location(6, 22))),
                JavaQualifiedTypeReference(testClassA, interfaceA, Span(Location(7, 13), Location(7, 45))),
                JavaQualifiedTypeReference(classB, enumB, Span(Location(5, 13), Location(5, 40)))
        )

        assertThat(extractedReferences).isEqualTo(expectedReferences);
    }


    private fun getFileByEntity(codebase: Codebase, fileName: String): File =
            codebase.modules.flatMap { it.sourceFolders }.flatMap { it.files }.find { it.fileName == fileName } ?: fail("Cannot find file.")

}