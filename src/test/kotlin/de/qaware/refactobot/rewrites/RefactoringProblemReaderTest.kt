package de.qaware.refactobot.rewrites

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Tests for RefactoringProblemReader. Although this is just test code itself, we should be sure that it actually works.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class RefactoringProblemReaderTest {


    @Test
    fun testRewriteSpec() {

        checkRewrite("Hello /*|*/World/*->Universe*/!", "Hello World!", "Hello Universe!")
        checkRewrite("ABC /*|*/DEF/*->GHI*/ JKL /*|*/MNO/*->PQR*/ STU", "ABC DEF JKL MNO STU", "ABC GHI JKL PQR STU")
        checkRewrite("/*|*//*->foo*/", "", "foo") // just insertion
        checkRewrite("/*|*//*->a new line\n*/next line", "next line", "a new line\nnext line") // newline handling

    }

    private fun checkRewrite(rewriteSpec: String, expectedOriginal: String, expectedResult: String) {
        assertThat(RefactoringProblemReader.getOriginal(rewriteSpec)).isEqualTo(expectedOriginal)
        assertThat(RefactoringProblemReader.getResult(rewriteSpec)).isEqualTo(expectedResult)
    }

    /**
     * Small self test to document how the preamble is extracted from a file.
     */
    @Test
    fun textExtractPreamble() {

        val preamble = RefactoringProblemReader.extractPreamble("""
            |Sample text for testing the extraction of the preamble from files
            |with special markers.
            |//: class -> otherclass
            |//: justaclass
            |The end.
            """.trimMargin())
        assertThat(preamble).hasSize(2)
        assertThat(preamble[0]).isEqualTo(Pair("class", "otherclass"))
        assertThat(preamble[1]).isEqualTo(Pair("justaclass", "justaclass"))
    }
}