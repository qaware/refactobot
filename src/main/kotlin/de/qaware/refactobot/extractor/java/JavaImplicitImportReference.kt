package de.qaware.refactobot.extractor.java

import de.qaware.refactobot.model.codebase.File
import de.qaware.refactobot.model.operation.FileOperation
import de.qaware.refactobot.model.operation.Location
import de.qaware.refactobot.model.operation.Span
import de.qaware.refactobot.model.plan.FileLocation
import de.qaware.refactobot.model.reference.Reference
import de.qaware.refactobot.util.fileToClass
import de.qaware.refactobot.util.slashify

/**
 * A special type of reference object for the classes that are implicitly in scope for this file, since they are in
 * the same package as this file. This reference does not correspond to code, but to an empty location in the header
 * where new imports will be added if the implicitly referenced files.
 *
 * We do not care if some explicit imports become unnecessary in the course of the reorganization, since those imports
 * do not hurt and other tools clean them up easily.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class JavaImplicitImportReference(override val origin: File, val targets: Set<File>, val location: Location): Reference {

    override fun getAdjustment(refactoringPlan: Map<File, FileLocation>): FileOperation.Edit {

        val newPath = refactoringPlan[origin]!!.path
        
        // find the classes that now live in a different package than this class (either this or the other file could
        // have moved, or both), and generate imports for them.
        val importsToInsert = targets.map { file -> refactoringPlan[file]!! }
                .filter { it.path != newPath }
                .map { "import " + fileToClass(it.path.resolve(it.fileName).slashify()) + ";\n" }
                .sorted()
                .joinToString("")

        return FileOperation.Edit(Span(location, location), importsToInsert)
    }


}
