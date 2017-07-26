package de.qaware.refactobot.test

import de.qaware.refactobot.model.codebase.Codebase
import de.qaware.refactobot.scanner.MavenScanner
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
