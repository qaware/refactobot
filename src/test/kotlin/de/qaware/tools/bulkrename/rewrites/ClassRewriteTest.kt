package de.qaware.tools.bulkrename.rewrites

import com.github.javaparser.JavaParser
import de.qaware.tools.bulkrename.executor.EditProcessor
import de.qaware.tools.bulkrename.extractor.JavaAnalyzer
import de.qaware.tools.bulkrename.model.codebase.File
import de.qaware.tools.bulkrename.model.codebase.FileType
import de.qaware.tools.bulkrename.model.codebase.Module
import de.qaware.tools.bulkrename.model.codebase.SourceFolder
import de.qaware.tools.bulkrename.model.plan.NewFileLocation
import de.qaware.tools.bulkrename.util.*
import org.apache.commons.io.IOUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.ByteArrayInputStream
import java.io.StringReader
import java.io.StringWriter
import java.nio.file.Paths

/**
 * Test that asserts that the various references in a class file are rewritten correctly when classes are moved around.
 *
 *
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
@RunWith(Parameterized::class)
class ClassRewriteTest(val filename: String) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "file={0}")
        fun getFiles() = IOUtils.readLines(ClassRewriteTest::class.java.classLoader.getResourceAsStream("rewrites/"), Charsets.UTF_8)
    }

    @Test
    fun testRewrite() {

        val problem = RefactoringProblemReader.readProblem("rewrites/" + filename)
        val thisClass = getFqcnFromFileContent(problem.originalFile)
        val thisFile = getFileForFqcn(thisClass)

        // in case this file is not present in the rules, add it.
        val adjustedMoveRules =
                if (problem.moveRules.any { it.first == thisClass })
                    problem.moveRules
                else
                    problem.moveRules + Pair(thisClass, thisClass)

        val fileEntries = adjustedMoveRules.map { getFileForFqcn(it.first) }
        val dummyModule = Module("module", Paths.get("."),
                listOf(SourceFolder(".", fileEntries.toList())))

        val filesByClass = fileEntries.associateBy { file -> fileToClass(file.fullName.slashify()) }
        val filesInSamePackage = fileEntries.filter { file -> file.path == thisFile.path }

        fun newLocationOf(newFqcn: String): NewFileLocation {
            val (path, filename) = splitPath(classToFile(newFqcn))
            return NewFileLocation(dummyModule, dummyModule.sourceFolders[0], path, filename)
        }

        val refs =
                JavaAnalyzer().extractReferences(thisFile,
                        ByteArrayInputStream(problem.originalFile.toByteArray(Charsets.UTF_8)),
                        filesByClass,
                        filesInSamePackage)

        val refactoringPlan: Map<File, NewFileLocation> =
                adjustedMoveRules
                    .map { rule -> Pair(filesByClass[rule.first]!!, newLocationOf(rule.second)) }
                    .toMap()

        val edits = refs.map { ref -> ref.getAdjustment(refactoringPlan) }
                                    .filterNotNull()

        val writer = StringWriter()
        EditProcessor(problem.originalFile.splitLines(), writer).applyEdits(edits)
        val actualResult = writer.toString()

        assertThat(actualResult).isEqualTo(problem.expectedResult)
    }


    /**
     * Constructs a file entry for a given class name. We assume that the source tree path and module path are empty,
     * so this is a slightly simplified case.
     */
    fun getFileForFqcn(fqcn: String) : File {

        val (path, fileName) = splitPath(classToFile(fqcn))
        return File(fullPath = path, path = path, fileName = fileName, type = FileType.JAVA)
    }



    /**
     * Parses the file to extract the main class name, including package.
     */
    fun getFqcnFromFileContent(content: String): String {

        val compilationUnit = StringReader(content).use(JavaParser::parse)
        val className = compilationUnit.types[0].name
        val packageName = compilationUnit.`package`.packageName
        return packageName + "." + className
    }



}