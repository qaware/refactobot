package de.qaware.tools.bulkrename.extractor.java

import de.qaware.tools.bulkrename.model.codebase.File
import de.qaware.tools.bulkrename.model.operation.FileOperation
import de.qaware.tools.bulkrename.model.operation.Span
import de.qaware.tools.bulkrename.model.plan.NewFileLocation
import de.qaware.tools.bulkrename.model.reference.Reference
import de.qaware.tools.bulkrename.util.pathToPackage

/**
 * Reference to the java package this file is from. Must be adapted when the class is moved to another package.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
data class JavaPackageReference(override val origin: File, val span: Span): Reference {

    override fun getAdjustment(refactoringPlan: Map<File, NewFileLocation>): FileOperation.Edit {

        val newTarget = refactoringPlan.get(origin)!!;
        return FileOperation.Edit(span, "package " + pathToPackage(newTarget.path) + ";")
    }

}