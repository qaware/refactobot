package de.qaware.tools.bulkrename.test

import de.qaware.tools.bulkrename.model.codebase.Codebase
import de.qaware.tools.bulkrename.scanner.MavenScanner
import java.nio.file.Paths

/**
 * Some test data
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
object TestData {

    val testCodebaseUri = this.javaClass.classLoader.getResource("maven_test_modules").toURI()

    val exampleCodebase : Codebase by lazy { MavenScanner().scanCodebase(Paths.get(testCodebaseUri)) }

}
