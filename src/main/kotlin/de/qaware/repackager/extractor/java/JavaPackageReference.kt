package de.qaware.repackager.extractor.java

import de.qaware.repackager.model.codebase.File
import de.qaware.repackager.model.operation.FileOperation
import de.qaware.repackager.model.operation.Span
import de.qaware.repackager.model.plan.NewFileLocation
import de.qaware.repackager.model.reference.Reference
import de.qaware.repackager.util.pathToPackage

/**
 * Reference to the java package this file is from. Must be adapted when the class is moved to another package.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
data class JavaPackageReference(override val origin: File, val span: Span): Reference {

    override fun getAdjustment(refactoringPlan: Map<File, NewFileLocation>): FileOperation.Edit {

        val newTarget = refactoringPlan.get(origin)!!
        return FileOperation.Edit(span, "package " + pathToPackage(newTarget.path) + ";")
    }

}
