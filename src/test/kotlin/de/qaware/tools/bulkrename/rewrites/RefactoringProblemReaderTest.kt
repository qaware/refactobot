package de.qaware.tools.bulkrename.rewrites

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

        val (preamble, rest) = RefactoringProblemReader.extractPreamble("""
            |Sample text for testing the extraction of the preamble from files
            |with special markers.
            |//: These two lines are the preamble
            |//: second line
            |The end.
            """.trimMargin())
        assertThat(preamble).hasSize(2)
        assertThat(preamble[0]).isEqualTo("These two lines are the preamble")
        assertThat(preamble[1]).isEqualTo("second line")

        assertThat(rest).isEqualTo("""
        |Sample text for testing the extraction of the preamble from files
        |with special markers.
        |The end.
        """.trimMargin())
    }

    @Test
    fun testReadProblem() {

        val problem = RefactoringProblemReader.readProblem("rewrites/TestClassRename.java")

        println(problem.moveRules)
        println(problem.originalFile)
        println(problem.expectedResult)


    }

}