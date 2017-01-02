package de.qaware.tools.bulkrename.executor

import de.qaware.tools.bulkrename.model.action.Action

/**
 * Executer that executes a list of Actions.
 *
 * @author Alexander Lannes alexander.lannes@qaware.de
 */
class Executor {

    fun executeActions(actions: List<Action>) {
        //mutable copy of actions
        val mutableActions = actions as MutableList<Action>
        var doneActions = listOf<Action>()

        for (action in mutableActions) {
            if(!doneActions.contains(action)) {
                var actionsForSameFile = listOf<Action>()
                actionsForSameFile += action
                doneActions += action
//                for (otherAction in mutableActions) {
//                    if (action != otherAction && action.changedFile == otherAction.changedFile && !doneActions.contains(otherAction)) {
//                        actionsForSameFile += otherAction
//                        doneActions += otherAction
//                    }
//                }
//                var sortedActionsForFile = listOf<Action>()
//
//
//                if(action.actionType == ActionType.EDIT_ACTION) {
//                    sortedActionsForFile += action
//                } else {
//                    for (a in actionsForSameFile) {
//                      if (a.actionType == ActionType.EDIT_ACTION) {
//                          sortedActionsForFile += a
//                          break
//                      }
//                    }
//                }
//                for (a in actionsForSameFile) {
//                    if (a.actionType == ActionType.EDIT_ACTION) {
//                        val fileAction = a as EditAction
//                        for (sortedAction in sortedActionsForFile) {
//                            if ((sortedAction as EditAction).range.line < fileAction.range.line && (sortedAction as EditAction).range.column < fileAction.range.column) {
//                                //TODO
//                                break
//                            }
//                        }
//                    }
//                }
//
//
//                val file = File(actionsForSameFile[0].changedFile)
//                val writer = file.writer(Charset.defaultCharset())
            }
        }

    }
}