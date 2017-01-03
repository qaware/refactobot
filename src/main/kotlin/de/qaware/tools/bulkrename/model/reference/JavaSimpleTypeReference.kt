package de.qaware.tools.bulkrename.model.reference

import de.qaware.tools.bulkrename.model.codebase.File
import de.qaware.tools.bulkrename.model.operation.FileOperation
import de.qaware.tools.bulkrename.model.operation.Span
import de.qaware.tools.bulkrename.model.plan.NewFileLocation
import de.qaware.tools.bulkrename.util.fileToClass

/**
 * A simple type reference, which is textually represented as an unqualified class name in the source.
 *
 * When the class is renamed, the new name should be inserted into the source, replacing the old one.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class JavaSimpleTypeReference(origin: File, target: File, val span: Span): Reference(origin, target) {

    override fun getAdjustment(newTarget: NewFileLocation) =
        FileOperation.Edit(span.start, span.end, fileToClass(newTarget.fileName))

}
