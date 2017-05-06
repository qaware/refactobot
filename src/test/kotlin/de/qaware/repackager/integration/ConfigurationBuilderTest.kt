package de.qaware.repackager.integration

import de.qaware.repackager.configuration.ConfigurationBuilder
import org.junit.Test

/**
 * Experiments with the configuration DSL.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class ConfigurationBuilderTest {

    @Test
    fun testConfigurationBuilder() {

        ConfigurationBuilder().build {

            steps {
                rename("abc" to "def")


            }


        }




    }

}
