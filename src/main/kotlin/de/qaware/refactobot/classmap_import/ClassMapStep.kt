package de.qaware.refactobot.classmap_import

import de.qaware.refactobot.model.plan.RefactoringStep
import de.qaware.refactobot.util.classToFile
import de.qaware.refactobot.util.fileToClass
import de.qaware.refactobot.util.slashify
import de.qaware.refactobot.util.splitPath

/**
 * Importer for class maps from structure101.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
object ClassMapStep {

    fun getStep(map : Map<String, String>): RefactoringStep = { fileLocation ->

        val origClass = fileToClass(fileLocation.path + "/" + fileLocation.fileName)
        val newClass: String? = map[origClass]
        if (newClass == null) {
            fileLocation
        } else {
            val (newPath, newFileName) = splitPath(classToFile(newClass))
            fileLocation.copy(fileName = newFileName, path = newPath.slashify())
        }
    }

}
