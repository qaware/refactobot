package de.qaware.tools.bulkrename.extractor

import de.qaware.tools.bulkrename.model.codebase.File
import de.qaware.tools.bulkrename.model.operation.FileOperation
import de.qaware.tools.bulkrename.model.operation.Location
import de.qaware.tools.bulkrename.model.operation.Span
import de.qaware.tools.bulkrename.model.plan.NewFileLocation
import de.qaware.tools.bulkrename.model.reference.Reference
import de.qaware.tools.bulkrename.util.fileToClass
import de.qaware.tools.bulkrename.util.slashify

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

    override fun getAdjustment(refactoringPlan: Map<File, NewFileLocation>): FileOperation.Edit {

        val newPath = refactoringPlan[origin]!!.path
        
        // find the classes that now live in a different package than this class (either this or the other file could
        // have moved, or both), and generate imports for them.
        val importsToInsert = targets.map { file -> refactoringPlan[file]!! }
                .filter { it.path != newPath }
                .map { "import " + fileToClass(it.path.resolve(it.fileName).slashify()) + ";\n" }
                .joinToString()

        return FileOperation.Edit(Span(location, location), importsToInsert)
    }


}
