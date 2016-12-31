package de.qaware.tools.bulkrename.model.action

import java.nio.file.Path

/**
 * Action for moving a file or renaming it
 *
 * @author Alexander Lannes alexander.lannes@qaware.de
 */
class MoveAction (

        override val changedFile: String,

        override val actionType: ActionType,

        /**
        * The new file name if the file shall be renamed. Otherwise it equals the current filename
        */
        val newFileName : String,

        /**
        * The path to target module the file shall be moved to. If the file shall not be moved it equals the current module
        */
        val newModule : Path,

        /**
        * The path to target package the file shall be moved to, equals the current one if the file shall not be moved
        */
        val newPackage : Path
) : Action()