package de.qaware.repackager.extractor.java

import de.qaware.repackager.model.codebase.File
import de.qaware.repackager.model.operation.FileOperation
import de.qaware.repackager.model.operation.Span
import de.qaware.repackager.model.plan.NewFileLocation
import de.qaware.repackager.model.reference.Reference
import de.qaware.repackager.util.fileToClass
import de.qaware.repackager.util.slashify

/**
 * A fully qualified type reference, which is textually represented as the fully qualified class name, e.g.
 * "org.example.codebase.packagename.MyClass".
 *
 * When the class is renamed or moved, the new qualified name should be inserted.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
data class JavaQualifiedTypeReference(override val origin: File, val target: File, val span: Span): Reference {

    override fun getAdjustment(refactoringPlan: Map<File, NewFileLocation>): FileOperation.Edit {

        val newTarget = refactoringPlan.get(target)!!
        val newFqcn = fileToClass(newTarget.path.resolve(newTarget.fileName).slashify())
        return FileOperation.Edit(span, newFqcn)
    }

}
