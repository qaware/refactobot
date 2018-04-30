package de.qaware.refactobot.util

import org.assertj.core.api.Assertions.assertThat
import java.io.File

/**
 * Handy comparison method that should live in some library.
 */
object DirectoryComparison {

    /**
     * Recursively compares two files / directory structures and asserts their equality.
     * Files are compared by their content, ignoring file attributes.
     * For directories, we assert that they contain the same set of files.
     *
     * Intended to be used in tests.
     *
     * @throws assertion errors when content is not equal.
     */
    fun assertDirectoriesEqual(file1: File, file2: File) {

        if (file1.isFile && file2.isFile) {

            assertThat(file2).hasSameContentAs(file1, Charsets.UTF_8)

        } else if (file1.isDirectory && file2.isDirectory) {

            val content1 = file1.listFiles().sorted()
            val content2 = file2.listFiles().sorted()

            assertThat(content1.map { it.name }).hasSameElementsAs(content2.map { it.name })
            (content1 zip content2).forEach { (f1, f2) -> assertDirectoriesEqual(f1, f2) }

        } else {
            throw AssertionError("invalid file types of $file1, $file2")
        }
    }

}