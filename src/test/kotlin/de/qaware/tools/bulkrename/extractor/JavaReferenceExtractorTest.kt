package de.qaware.tools.bulkrename.extractor

import de.qaware.tools.bulkrename.model.codebase.Codebase
import de.qaware.tools.bulkrename.model.codebase.File
import de.qaware.tools.bulkrename.model.operation.Location
import de.qaware.tools.bulkrename.model.operation.Span
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
//    @Ignore // deactivate for now, since this is far too unstable under current changes.
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
                JavaQualifiedTypeReference(classA, interfaceA, Span(Location(2, 7), Location(2, 39))),
                JavaSimpleTypeReference(classA, interfaceA, Span(Location(4, 31), Location(4, 39))),
                JavaSimpleTypeReference(classA, interfaceA, Span(Location(6, 12), Location(6, 21))),
                JavaQualifiedTypeReference(testClassA, testClassB, Span(Location(1, 7), Location(1, 39))),
                JavaSimpleTypeReference(testClassA, testClassB, Span(Location(5, 12), Location(5, 21))),
                JavaQualifiedTypeReference(testClassA, interfaceA, Span(Location(6, 12), Location(6, 44))),
                JavaQualifiedTypeReference(classB, enumB, Span(Location(4, 12), Location(4, 39)))
        )

        assertThat(extractedReferences).isEqualTo(expectedReferences);
    }


    private fun getFileByEntity(codebase: Codebase, fileName: String): File =
            codebase.modules.flatMap { it.sourceFolders }.flatMap { it.files }.find { it.fileName == fileName } ?: fail("Cannot find file.")

}