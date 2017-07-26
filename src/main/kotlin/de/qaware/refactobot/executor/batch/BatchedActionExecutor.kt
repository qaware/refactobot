package de.qaware.refactobot.executor.batch

import de.qaware.refactobot.executor.ActionExecutor
import de.qaware.refactobot.model.operation.FileOperation
import de.qaware.refactobot.util.partition

/**
 * Action executor that splits the operation into manageable batches, which are then passed to another executor.
 *
 * This is necessary to avoid flooding git with too many move operations in a single commit. Git's rename detection only
 * works well within some bounds, so we split the operation into multiple commits.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class BatchedActionExecutor(val batchSize : Int, val executor: ActionExecutor) : ActionExecutor {

    override fun execute(operations: List<FileOperation>, commitMsg: String) {

        val batches = partition(operations, batchSize)
        val numBatches = batches.size

        for ((i, batch) in batches.withIndex()) {
            executor.execute(batch, commitMsg + " (${i+1}/${numBatches})")
        }
    }

}