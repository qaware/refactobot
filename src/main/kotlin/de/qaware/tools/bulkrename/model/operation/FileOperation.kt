package de.qaware.tools.bulkrename.model.operation

import java.nio.file.Path

/**
 * An operation on a file. The file can be moved to a different location, and edited.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 *
 * @property sourceFile the source file location.
 * @property targetFile the target file location.
 * @property edits the edits that should be applied to the file content.
 */
data class FileOperation(val sourceFile: Path, val targetFile: Path, val edits: List<Edit>) {

    /**
     * An edit action, that can be applied to a sequence of characters.
     *
     * @property range the range to replace
     * @property replacementString the new text to put into this range.
     */
    data class Edit(val span: Span, val replacementString: String)
}

