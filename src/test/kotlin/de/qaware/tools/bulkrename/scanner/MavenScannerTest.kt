package de.qaware.tools.bulkrename.scanner

import org.junit.Test
import java.nio.file.Paths

/**
 * Test stub

 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class MavenScannerTest {

    @Test
    fun scanMavenCodebase() {

        val codebase = MavenScanner().scanCodebase(Paths.get("P:/codebase/code"));

    }

}