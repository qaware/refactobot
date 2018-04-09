package de.qaware.refactobot.configuration

import de.qaware.refactobot.integration.Refactobot
import de.qaware.refactobot.model.plan.FileLocation
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ConfigurationTest {

    @Test
    fun testConfigurationDsl() {

        val bot = Refactobot.configure {

            batchSize = 123
            commitMessage = "commit"

            refactor {
                module += "_foo"
                sourceFolder = "newsrc"
                path += "_bar"
                fileName += ".java"
            }
        }

        assertThat(bot.config.batchSize).isEqualTo(123)
        assertThat(bot.config.commitMessage).isEqualTo("commit")

        val oldLocation = FileLocation("mod", "src", "path", "file")
        val expectedLocation = FileLocation("mod_foo", "newsrc", "path_bar", "file.java")
        assertThat(bot.config.refactoring(oldLocation)).isEqualTo(expectedLocation)
    }


}