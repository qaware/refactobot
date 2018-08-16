package de.qaware.refactobot.integration

import de.qaware.refactobot.util.DirectoryComparison
import org.apache.commons.io.FileUtils
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File


class FullIntegrationTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    val sourceFolder = File(FullIntegrationTest::class.java.getResource("/integration_test/example_codebase").file)
    val expectedResult = File(FullIntegrationTest::class.java.getResource("/integration_test/expected_result").file)

    @Test
    fun test() {

        val tmpDir = temporaryFolder.newFolder("test")

        FileUtils.copyDirectory(sourceFolder, tmpDir)

        val bot = Refactobot.configure {

            refactor {
                renameFile("InterfaceA.java" to "FancyInterface.java")
                renameFile("TestClassB.java" to "TestClassBar.java")
            }

        }

        bot.run(tmpDir.absolutePath)
        
        DirectoryComparison.assertDirectoriesEqual(expectedResult, tmpDir)
        

    }



}