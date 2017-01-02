package de.qaware.tools.bulkrename.model.action

import java.nio.file.Path

/**
 * Data type for actions.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
sealed class Action {

    class MoveFile(val sourceFile: Path, val targetFile: Path): Action()

    class EditText(val file: Path, val range: Range, val newText: String): Action()

}

