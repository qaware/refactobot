package de.qaware.repackager.executor.git

import de.qaware.repackager.executor.git.impl.JgitRepositoryFactory
import org.apache.commons.lang3.StringUtils
import org.assertj.core.api.Assertions.assertThat
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.treewalk.TreeWalk
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.nio.charset.StandardCharsets.UTF_8

/**
 * Test for the JGit wrapper.

 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class JgitRepositoryFactoryTest {

    @Rule
    @JvmField
    val tempFolder = TemporaryFolder()

    @Test
    fun testJGitWrapper() {

        // create git repository in temp folder
        Git.init().setDirectory(tempFolder.root).call()

        // create repo handler
        val repo = JgitRepositoryFactory().getRepository(tempFolder.root.toPath())!!

        // commit two files
        val file1 = tempFolder.newFile("file1.txt")
        file1.writeText("foo")
        val file2 = tempFolder.newFile("file2.txt")
        file2.writeText("bar")
        repo.commit(setOf(file1.toPath(), file2.toPath()), emptySet(), "first commit")

        // remove the second file again, change the first one
        file1.writeText("baz")
        file2.delete()
        repo.commit(setOf(file1.toPath()), setOf(file2.toPath()), "second commit")


        // check that the repo has two commits
        val commits = Git.open(tempFolder.root).log().call().toList().reversed()

        assertThat(commits).hasSize(2)

        assertThat(commits[0].fullMessage).isEqualTo("first commit")
        assertThat(filesInCommit(Git.open(tempFolder.root).repository, commits[0])).isEqualTo(mapOf(
                "file1.txt" to "foo",
                "file2.txt" to "bar"
        ))

        assertThat(commits[1].fullMessage).isEqualTo("second commit")
        assertThat(filesInCommit(Git.open(tempFolder.root).repository, commits[1])).isEqualTo(mapOf(
                "file1.txt" to "baz"
        ))

    }

    private fun filesInCommit(repo: Repository, commit: RevCommit): Map<String, String> {
        val treeWalk = TreeWalk(repo)
        treeWalk.addTree(commit.tree)
        treeWalk.isRecursive = true
        treeWalk.reset(commit.tree)
        val result = mutableMapOf<String, String>()
        while (treeWalk.next()) {
            val path = treeWalk.pathString
            val content = StringUtils.toEncodedString(repo.open(treeWalk.getObjectId(0)).bytes, UTF_8)
            result.put(path, content)
        }
        treeWalk.close()
        return result
    }

}
