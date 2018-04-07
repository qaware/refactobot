package de.qaware.refactobot.configuration

import de.qaware.refactobot.integration.Refactobot
import de.qaware.refactobot.model.plan.FileLocation
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ConfigurationTest {

    @Test
    fun testStepDsl() {
        val step = RefactoringContext.build {
            module += "_foo"
            sourceFolder = "newsrc"
            path += "_bar"
            fileName += ".java"
        }

        val oldLocation = FileLocation("mod", "src", "path", "file")

        assertThat(step(oldLocation)).isEqualTo(FileLocation("mod_foo", "newsrc", "path_bar", "file.java"))
    }


    @Test
    fun testConfigurationDsl() {

        val bot = Refactobot.configure {

            codebaseLocation = "/home/johndoe/my/codebase"
            batchSize = 123
            commitMessage = "commit"

            refactor {
                if (fileName == "hello") {
                    fileName = "world"
                }
            }
        }

        assertThat(bot.config.codebaseLocation).isEqualTo("/home/johndoe/my/codebase")
        assertThat(bot.config.batchSize).isEqualTo(123)
        assertThat(bot.config.commitMessage).isEqualTo("commit")

    }


}