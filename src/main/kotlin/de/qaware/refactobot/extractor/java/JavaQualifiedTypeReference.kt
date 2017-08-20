package de.qaware.refactobot.extractor.java

import de.qaware.refactobot.model.codebase.File
import de.qaware.refactobot.model.operation.FileOperation
import de.qaware.refactobot.model.operation.Span
import de.qaware.refactobot.model.plan.FileLocation
import de.qaware.refactobot.model.reference.Reference
import de.qaware.refactobot.util.fileToClass

/**
 * A fully qualified type reference, which is textually represented as the fully qualified class name, e.g.
 * "org.example.codebase.packagename.MyClass".
 *
 * When the class is renamed or moved, the new qualified name should be inserted.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
data class JavaQualifiedTypeReference(override val origin: File, val target: File, val span: Span): Reference {

    override fun getAdjustment(refactoringPlan: Map<File, FileLocation>): FileOperation.Edit {

        val newTarget = refactoringPlan.get(target)!!
        val newFqcn = fileToClass(newTarget.path + "/" + newTarget.fileName)
        return FileOperation.Edit(span, newFqcn)
    }

}
