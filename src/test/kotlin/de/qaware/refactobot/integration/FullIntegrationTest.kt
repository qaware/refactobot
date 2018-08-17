package de.qaware.refactobot.integration

import de.qaware.refactobot.util.DirectoryComparison
import org.apache.commons.io.FileUtils
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

/**
 * Test for the complete tool, which can also serve as a starting point for your own applications.
 *
 * Copies some codebase into a temporary directory, runs the tool, and compares the outcome with an expected result.
 */
class FullIntegrationTest {

    /**
     * A temporary folder, for testing purposes
     */
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    /**
     * Source codebase.
     */
    val sourceFolder = File(FullIntegrationTest::class.java.getResource("/integration_test/example_codebase").file)

    /**
     * Expected result, for checking correctness in the test.
     */
    val expectedResult = File(FullIntegrationTest::class.java.getResource("/integration_test/expected_result").file)

    @Test
    fun test() {

        // Set up temporary copy, to avoid messing with source files in a test
        val tmpDir = temporaryFolder.newFolder("test")
        FileUtils.copyDirectory(sourceFolder, tmpDir)

        // Invoke configure and invoke the tool, with just two simple renames.
        val refactobot = Refactobot.configure {

            refactor {
                renameFile("InterfaceA.java" to "FancyInterface.java")
                renameFile("TestClassB.java" to "TestClassBar.java")
            }

        }

        refactobot.run(tmpDir.absolutePath)

        // Ensure that the result matches the expectations.
        DirectoryComparison.assertDirectoriesEqual(expectedResult, tmpDir)

    }



}