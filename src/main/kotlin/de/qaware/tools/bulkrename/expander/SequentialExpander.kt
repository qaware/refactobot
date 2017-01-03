package de.qaware.tools.bulkrename.expander

import de.qaware.tools.bulkrename.model.codebase.Codebase
import de.qaware.tools.bulkrename.model.codebase.File
import de.qaware.tools.bulkrename.model.plan.NewFileLocation
import de.qaware.tools.bulkrename.model.plan.SchematicRefactoringPlan
import de.qaware.tools.bulkrename.model.plan.Step
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
        var transformationMap = initializeTransformationMap(codebase)
        for (step in refactoringPlan.steps) {
            transformationMap = transformationMap.mapValues { applyStep(step, it.value) }
        }
        return transformationMap.mapValues { entry -> createNewFileLocation(entry.value) }
    }

    private fun createNewFileLocation(expansionResult: RawLocation): NewFileLocation {

        val fileName = expansionResult.filename
        val path = expansionResult.path
        val moduleName = expansionResult.moduleName
        val sourceRoot = expansionResult.sourceRoot

        val newModule = codebase.modules.find { it.name == moduleName }
                ?: throw IllegalStateException("Unknown module " + moduleName)
        val newSourceFolder = newModule.sourceFolders.find { it.path == sourceRoot }
                ?: throw IllegalStateException("No source folder with path " + sourceRoot + " in module " + newModule.name)

        return NewFileLocation(newModule, newSourceFolder, Paths.get(path), fileName)

    }

    /**
     * Initializes a simple map of all files in the given codebase to an initial no-op step.
     *
     * The step's source information is set to the current file information, while the step's target
     * information is empty.
     *
     * @param codebase the codebase for which the transformation map should be created
     * @return a map of every file in the codebase to a no-op step
     */
    private fun initializeTransformationMap(codebase: Codebase): Map<File, RawLocation> {
        val transformationMap = HashMap<File, RawLocation>()

        for (module in codebase.modules) {
            for (folder in module.sourceFolders) {
                for (file in folder.files) {
                    transformationMap.put(file, RawLocation(module.name, folder.path, file.path.toString(), file.fileName))
                }
            }
        }
        return transformationMap
    }

    /**
     * Applies the given current step onto the given last step (if it matches)
     *
     * @param step the step to apply
     * @param location the location to apply the given step to
     * @return new location
     */
    private fun applyStep(step: Step, location: RawLocation): RawLocation {

        // if all regexes match their corresponding components...
        if (step.replacements.all { location.ruleMatches(it) }) {

            // apply all rule components
            return step.replacements.entries.fold(location, RawLocation::applyRule)

        } else {
            // otherwise ignore this rule
            return location;
        }
    }
}
