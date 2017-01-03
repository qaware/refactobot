package de.qaware.tools.bulkrename.executor

import de.qaware.tools.bulkrename.model.operation.FileOperation
import java.io.Writer

/**
 * Code to apply edit operations on files and strings.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class EditProcessor(input : List<String>, private val output: Writer) {

    private val source = LineSource(input);

    fun applyEdits(edits: List<FileOperation.Edit>) {

        for ((span, replacementString) in edits) {

            // copy over any material before the edit location
            output.write(source.readUpTo(span.start));

            // discard the text up to the end location, and write out the replacement instead
            source.readUpTo(span.end)
            output.write(replacementString)
        }
        output.write(source.readRest())
        output.close()
    }
}
