package de.qaware.refactobot.model.plan

import de.qaware.refactobot.configuration.RefactoringContext
import de.qaware.refactobot.util.classToFile
import de.qaware.refactobot.util.fileToClass
import de.qaware.refactobot.util.slashify
import de.qaware.refactobot.util.splitPath

/**
 * Various factory methods for refactoring steps.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
object Steps {

    /**
     * Moves classes around according to a given class map.
     *
     * This step changes the names and paths of files, but never their modules or source folders.
     */
    fun applyClassMap(ctxt: RefactoringContext, map : Map<String, String>) {

        val origClass = fileToClass(ctxt.path + "/" + ctxt.fileName)
        val newClass: String? = map[origClass]
        if (newClass != null) {
            val (newPath, newFileName) = splitPath(classToFile(newClass))
            ctxt.fileName = newFileName
            ctxt.path = newPath.slashify()
        }
    }


}