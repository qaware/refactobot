package de.qaware.tools.bulkrename.rewrites

import org.apache.commons.io.IOUtils

/**
 * Simple tool to read files with rewrite comments in it, and produce the original file and the expected result.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
object RefactoringProblemReader {

    /**
     * Matches strings of the form "/*|*/ABC/*->DEF*/", which means "ABC" should be replaced by "DEF".
     */
    private val REPLACEMENT_REGEX = Regex("/\\*\\|\\*/(.*?)/\\*->(.*?)\\*/")

    /**
     * Matches rewrite rules of the form "org.example.test.Class -> org.example.othertest.OtherClass".
     */
    private val RULE_REGEX = Regex("([\\w\\.]+)\\s*->\\s*([\\w\\.]+)")

    fun getOriginal(str: String): String = str.replace(REPLACEMENT_REGEX, "$1")
    fun getResult(str: String): String = str.replace(REPLACEMENT_REGEX, "$2")

    fun extractPreamble(text: String): Pair<List<String>, String> {

        val lines: List<String> = text.split('\n')
        val preamble = lines
                .filter { it.startsWith("//:")}
                .map { it.substring(3).trim() }

        val rest = lines.filterNot { it.startsWith("//:") }.joinToString("\n")
        return Pair(preamble, rest)
    }


    fun readProblem(filename: String): RefactoringProblem {

        val (preamble, rest) = extractPreamble(readFileFromClasspathToString(filename))
        val originalFileContent = getOriginal(rest)
        val expectedNewFileContent = getResult(rest)

        return RefactoringProblem(preamble.map {rule -> parseRule(rule)}, originalFileContent, expectedNewFileContent)
    }

    fun parseRule(rule: String): Pair<String, String> {

        val match = RULE_REGEX.matchEntire(rule)
        if (match != null) {

            val (left, right) = match.destructured
            return Pair(left, right)

        } else {
            // use left for right
            return Pair(rule, rule)
        }
    }

    private fun readFileFromClasspathToString(name: String): String =
            IOUtils.toString(javaClass.classLoader.getResourceAsStream(name), Charsets.UTF_8)!!
}