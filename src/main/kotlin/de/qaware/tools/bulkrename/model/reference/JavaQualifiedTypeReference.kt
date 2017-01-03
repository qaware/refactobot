package de.qaware.tools.bulkrename.model.reference

import de.qaware.tools.bulkrename.model.codebase.File
import de.qaware.tools.bulkrename.model.operation.FileOperation
import de.qaware.tools.bulkrename.model.operation.Span
import de.qaware.tools.bulkrename.model.plan.NewFileLocation
import de.qaware.tools.bulkrename.util.fileToClass

/**
 * A fully qualified type reference, which is textually represented as the fully qualified class name, e.g.
 * "org.example.codebase.packagename.MyClass".
 *
 * When the class is renamed or moved, the new qualified name should be inserted.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
data class JavaQualifiedTypeReference(override val origin: File, override val target: File, val span: Span): Reference {

    override fun getAdjustment(newTarget: NewFileLocation): FileOperation.Edit {
        val newFqcn = fileToClass(newTarget.path.resolve(newTarget.fileName).joinToString("/"))
        return FileOperation.Edit(span.start, span.end, newFqcn)
    }

}
