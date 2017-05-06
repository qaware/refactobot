package de.qaware.tools.bulkrename.integration

import de.qaware.tools.bulkrename.configuration.ConfigurationBuilder
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
