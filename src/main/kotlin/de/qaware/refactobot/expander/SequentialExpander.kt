package de.qaware.refactobot.expander

import de.qaware.refactobot.configuration.RefactoringStep
import de.qaware.refactobot.model.codebase.Codebase
import de.qaware.refactobot.model.codebase.File
import de.qaware.refactobot.model.plan.FileLocation
import de.qaware.refactobot.util.slashify
import java.util.*

/**
 * The sequential expander creates a full refactoring plan from a schematic refactoring plan by applying the steps
 * onto each other in sequence. Files transformed by the first step will be matched by the second step, if the result
 * of the first transformation matches the matcher of the second step and so on.
 *
 * @author Florian Engel florian.engel@qaware.de
 */
class SequentialExpander(val codebase: Codebase) : Expander {

    override fun expandRefactoringPlan(refactoringPlan: List<RefactoringStep>): Map<File, FileLocation> {
        val fullRefactoringPlan = HashMap<File, FileLocation>()

        // for each file in the codebase...
        for (module in codebase.modules) {
            for (folder in module.sourceFolders) {
                for (file in folder.files) {

                    // ...apply all transformations to its location...
                    val initialLocation = FileLocation(module.modulePath.slashify(), folder.path, file.path.slashify(), file.fileName)
                    val finalLocation = refactoringPlan.fold(initialLocation, { loc, step -> step(loc) })

                    // ...and save the result as the final location of the file
                    fullRefactoringPlan.put(file, createNewFileLocation(finalLocation))
                }
            }
        }
        return fullRefactoringPlan
    }

    private fun createNewFileLocation(expansionResult: FileLocation): FileLocation {

        // TODO, this is now a mere check.

        val fileName = expansionResult.fileName
        val path = expansionResult.path
        val moduleName = expansionResult.module
        val sourceRoot = expansionResult.sourceFolder

        val newModule = codebase.modules.find { it.modulePath.slashify() == moduleName }
                ?: throw IllegalStateException("Unknown module " + moduleName)
        val newSourceFolder = newModule.sourceFolders.find { it.path == sourceRoot }
                ?: throw IllegalStateException("No source folder with path " + sourceRoot + " in module " + newModule.name)

        return FileLocation(newModule.modulePath.slashify(), newSourceFolder.path, path, fileName)

    }

}
