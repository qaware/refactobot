package de.qaware.repackager.executor.git

import de.qaware.repackager.executor.ActionExecutor
import de.qaware.repackager.model.operation.FileOperation
import java.nio.file.Path

/**
 * Action executor that creates a commit in a git repository.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class GitActionExecutor(private val rootPath: Path) : ActionExecutor {


    init {

    }






    override fun execute(operations: List<FileOperation>, commitMsg: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}
