package de.qaware.refactobot.expander

import de.qaware.refactobot.model.codebase.Codebase
import de.qaware.refactobot.model.codebase.File
import de.qaware.refactobot.model.plan.FileLocation
import de.qaware.refactobot.model.plan.RefactoringStep
import de.qaware.refactobot.util.slashify

/**
 * The sequential expander creates a full refactoring plan from a schematic refactoring plan by applying the steps
 * onto each other in sequence. Files transformed by the first step will be matched by the second step, if the result
 * of the first transformation matches the matcher of the second step and so on.
 *
 * @author Florian Engel florian.engel@qaware.de
 */
class SequentialExpander(val codebase: Codebase) : Expander {

    override fun expandRefactoringPlan(refactoringPlan: List<RefactoringStep>): Map<File, FileLocation> {
        val fullRefactoringPlan = mutableMapOf<File, FileLocation>()

        // for each file in the codebase...
        for (module in codebase.modules) {
            for (folder in module.sourceFolders) {
                for (file in folder.files) {

                    // ...apply all transformations to its location...
                    val initialLocation = FileLocation(module.modulePath.slashify(), folder.path, file.path.slashify(), file.fileName)
                    val finalLocation = refactoringPlan.fold(initialLocation, { loc, step -> step(loc) })


                    // ... check the result for validity
                    val newModule = codebase.modules.find { it.modulePath.slashify() == finalLocation.module }
                            ?: throw IllegalStateException("Unknown module " + finalLocation.module)

                    if (newModule.sourceFolders.none { it.path == finalLocation.sourceFolder } ) {
                        throw IllegalStateException("No source folder with path " + finalLocation.sourceFolder + " in module " + newModule.name)
                    }

                    // ...and save the result as the final location of the file
                    fullRefactoringPlan.put(file, finalLocation)
                }
            }
        }
        return fullRefactoringPlan
    }

}
