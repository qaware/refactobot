package de.qaware.tools.bulkrename.model.action

/**
 * Action for changing references on a class
 *
 * @author Alexander Lannes alexander.lannes@qaware.de
 */
class EditAction (

        override val changedFile: String,

        override val actionType: ActionType,

        /**
         * The range that needs to be changed
         */
        val range: Range,

        /**
         * The new reference
         */
        val newReference: String

)  : Action()