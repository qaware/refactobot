package de.qaware.refactobot.executor

import de.qaware.refactobot.executor.filesystem.FilesystemActionExecutor
import de.qaware.refactobot.model.operation.FileOperation
import de.qaware.refactobot.model.operation.Location
import de.qaware.refactobot.model.operation.Span
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


/**
 * Test for Executor

 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class FilesystemActionExecutorTest {

    @Rule
    @JvmField
    var tmpFolder = TemporaryFolder()

    @Test
    fun testExecute() {

        val codebase = tmpFolder.newFolder("codebase").toPath()

        val source1 = Paths.get("a/sourcefile.txt")
        val target1 = Paths.get("b/targetfile.txt")

        val source2 = Paths.get("a/othersource.txt")
        val target2 = Paths.get("b/othersource.txt")

        writeFile(codebase.resolve(source1), "Hello world.")
        writeFile(codebase.resolve(source2), "Some text.")

        val operations = listOf(
                FileOperation(source1, target1, listOf(
                        FileOperation.Edit(Span(Location(0, 6), Location(0, 11)), "Universe")
                )),
                FileOperation(source2, target2, emptyList())
        )

        FilesystemActionExecutor(codebase).execute(operations)

        // TODO AKR assert results
    }

    private fun writeFile(path: Path, text: String) {
        Files.createDirectories(path.parent)
        path.toFile().writeText(text)
    }

}