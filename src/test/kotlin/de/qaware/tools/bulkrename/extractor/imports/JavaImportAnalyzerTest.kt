package de.qaware.tools.bulkrename.extractor.imports

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Test for import analysis.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class JavaImportAnalyzerTest {


    @Test
    fun testImportAnalysis() {

        assertThat(JavaImportAnalyzer().getImport("java.util.List", false))
                .isEqualTo(ImportClause.SimpleImport("java.util", "List"))

        assertThat(JavaImportAnalyzer().getImport("java.util.*", false))
                .isEqualTo(ImportClause.PackageImport("java.util"))

        assertThat(JavaImportAnalyzer().getImport("java.util.List.method", true))
                .isEqualTo(ImportClause.SingleClassImport("java.util", "List", "method", true))

    }


}