package de.qaware.refactobot.extractor.java

import de.qaware.refactobot.model.codebase.File
import de.qaware.refactobot.model.operation.FileOperation
import de.qaware.refactobot.model.operation.Span
import de.qaware.refactobot.model.plan.FileLocation
import de.qaware.refactobot.model.reference.Reference
import de.qaware.refactobot.util.pathToPackage

/**
 * Reference to the java package this file is from. Must be adapted when the class is moved to another package.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
data class JavaPackageReference(override val origin: File, val span: Span): Reference {

    override fun getAdjustment(refactoringPlan: Map<File, FileLocation>): FileOperation.Edit {

        val newTarget = refactoringPlan.get(origin)!!
        return FileOperation.Edit(span, "package " + pathToPackage(newTarget.path) + ";")
    }

}
