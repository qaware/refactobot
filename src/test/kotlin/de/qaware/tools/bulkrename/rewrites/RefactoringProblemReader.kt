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
    private val REPLACEMENT_REGEX = Regex("/\\*\\|\\*/(.*?)/\\*->(.*?)\\*/", RegexOption.DOT_MATCHES_ALL)

    /**
     * Matches rewrite rules of the form "org.example.test.Class -> org.example.othertest.OtherClass".
     */
    private val RULE_REGEX = Regex("([\\w\\.]+)\\s*->\\s*([\\w\\.]+)")

    fun getOriginal(str: String): String = str.replace(REPLACEMENT_REGEX, "$1")
    fun getResult(str: String): String = str.replace(REPLACEMENT_REGEX, "$2")

    fun extractPreamble(text: String): List<Pair<String, String>> =
            text.split('\n')
                    .filter { it.startsWith("//:")}
                    .map { parseRule(it.substring(3).trim()) }


    fun readProblem(filename: String): RefactoringProblem {

        val content = readFileFromClasspathToString(filename)
                .replace("\r\n", "\n")
        val rules = extractPreamble(content)
        val originalFileContent = getOriginal(content)
        val expectedNewFileContent = getResult(content)

        return RefactoringProblem(rules, originalFileContent, expectedNewFileContent)
    }

    fun parseRule(rule: String): Pair<String, String> {

        val match = RULE_REGEX.matchEntire(rule)
        if (match != null) {
            return Pair(match.groupValues[1], match.groupValues[2])

        } else {
            // use left for right
            return Pair(rule, rule)
        }
    }

    private fun readFileFromClasspathToString(name: String): String =
            IOUtils.toString(javaClass.classLoader.getResourceAsStream(name), Charsets.UTF_8)!!
}