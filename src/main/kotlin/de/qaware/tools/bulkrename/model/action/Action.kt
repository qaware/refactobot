package de.qaware.tools.bulkrename.model.action

/**
 * Interface for actions of the refactoring
 *
 * @author Alexander Lannes alexander.lannes@qaware.de
 */
abstract class Action {

    /**
     *  The file that is changed
     */
    abstract val changedFile: String

    /**
     * Action type to differ between move and edit action
     */
    abstract val actionType: ActionType
}

