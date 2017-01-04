package de.qaware.tools.bulkrename.expander

import de.qaware.tools.bulkrename.model.codebase.Codebase
import de.qaware.tools.bulkrename.model.codebase.File
import de.qaware.tools.bulkrename.model.plan.NewFileLocation
import de.qaware.tools.bulkrename.model.plan.SchematicRefactoringPlan
import de.qaware.tools.bulkrename.util.slashify
import java.nio.file.Paths
import java.util.*

/**
 * The sequential expander creates a full refactoring plan from a schematic refactoring plan by applying the steps
 * onto each other in sequence. Files transformed by the first step will be matched by the second step, if the result
 * of the first transformation matches the matcher of the second step and so on.
 *
 * @author Florian Engel florian.engel@qaware.de
 */
class SequentialExpander(val codebase: Codebase) : Expander {

    override fun expandRefactoringPlan(refactoringPlan: SchematicRefactoringPlan): Map<File, NewFileLocation> {
        val fullRefactoringPlan = HashMap<File, NewFileLocation>()

        // for each file in the codebase...
        for (module in codebase.modules) {
            for (folder in module.sourceFolders) {
                for (file in folder.files) {

                    // ...apply all transformations to its location...
                    var location = RawLocation(module.modulePath.slashify(), folder.path, file.path.toString(), file.fileName)
                    for (step in refactoringPlan.steps) {
                        location = location.applyStep(step)
                    }

                    // ...and save the result as the final location of the file
                    fullRefactoringPlan.put(file, createNewFileLocation(location))
                }
            }
        }
        return fullRefactoringPlan
    }

    private fun createNewFileLocation(expansionResult: RawLocation): NewFileLocation {

        val fileName = expansionResult.filename
        val path = expansionResult.path
        val moduleName = expansionResult.module
        val sourceRoot = expansionResult.sourceRoot

        val newModule = codebase.modules.find { it.modulePath.slashify() == moduleName }
                ?: throw IllegalStateException("Unknown module " + moduleName)
        val newSourceFolder = newModule.sourceFolders.find { it.path == sourceRoot }
                ?: throw IllegalStateException("No source folder with path " + sourceRoot + " in module " + newModule.name)

        return NewFileLocation(newModule, newSourceFolder, Paths.get(path), fileName)

    }

}
