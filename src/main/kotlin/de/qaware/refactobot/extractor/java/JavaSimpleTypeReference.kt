package de.qaware.refactobot.extractor.java

import de.qaware.refactobot.model.codebase.File
import de.qaware.refactobot.model.operation.FileOperation
import de.qaware.refactobot.model.operation.Span
import de.qaware.refactobot.model.plan.NewFileLocation
import de.qaware.refactobot.model.reference.Reference
import de.qaware.refactobot.util.fileToClass

/**
 * A simple type reference, which is textually represented as an unqualified class name in the source.
 *
 * When the class is renamed, the new name should be inserted into the source, replacing the old one.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
data class JavaSimpleTypeReference(override val origin: File, val target: File, val span: Span): Reference {

    override fun getAdjustment(refactoringPlan: Map<File, NewFileLocation>): FileOperation.Edit {

        val newTarget = refactoringPlan.get(target)!!
        return FileOperation.Edit(span, fileToClass(newTarget.fileName))
    }


}
