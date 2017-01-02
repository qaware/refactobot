package de.qaware.tools.bulkrename.expander

import de.qaware.tools.bulkrename.model.codebase.Codebase
import de.qaware.tools.bulkrename.model.codebase.File
import de.qaware.tools.bulkrename.model.codebase.Module
import de.qaware.tools.bulkrename.model.codebase.SourceFolder
import de.qaware.tools.bulkrename.model.plan.*
import java.nio.file.Path
import java.util.*

/**
 * The sequential expander creates a full refactoring plan from a schematic refactoring plan by applying the steps
 * onto each other in sequence. Files transformed by the first step will be matched by the second step, if the result
 * of the first transformation matches the matcher of the second step and so on.
 *
 * @author Florian Engel florian.engel@qaware.de
 */
class SequentialExpander : Expander {

    override fun expandRefactoringPlan(refactoringPlan: SchematicRefactoringPlan, codebase: Codebase): FullRefactoringPlan {
        var transformationMap = initializeTransformationMap(codebase)
        for (step in refactoringPlan.steps) {
            transformationMap = expandStepToTransformationMap(step, transformationMap)
        }
        return createRefactoringPlanFromTransformationMap(transformationMap)
    }

    private fun createRefactoringPlanFromTransformationMap(transformationMap: Map<File, ExpandedStep>): FullRefactoringPlan {
        val instructionMap = HashMap<File, FileRefactoringInstruction>()
        transformationMap.forEach { instructionMap.put(it.key, FileRefactoringInstruction(it.value.targetExpression)) }
        return FullRefactoringPlan(instructionMap)
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
    private fun initializeTransformationMap(codebase: Codebase): Map<File, ExpandedStep> {
        val transformationMap = HashMap<File, ExpandedStep>()
        for (module in codebase.modules) {
            transformationMap.putAll(initializeTransformationMapForModule(module))
        }
        return transformationMap
    }

    private fun initializeTransformationMapForModule(module: Module): Map<File, ExpandedStep> {
        val moduleTransformationMap = HashMap<File, ExpandedStep>()
        val moduleRootPath = module.modulePath
        for (sourceFolder in module.sourceFolders) {
            moduleTransformationMap.putAll(initializeTransformationMapForFolder(sourceFolder, moduleRootPath, module.name))
        }
        return moduleTransformationMap
    }

    private fun initializeTransformationMapForFolder(folder: SourceFolder, moduleRootPath: Path, moduleName: String): Map<File, ExpandedStep> {
        val moduleFilesTransformationMap = HashMap<File, ExpandedStep>()
        for (file in folder.files) {
            val sourceModuleName = moduleName
            val sourceModulePath = moduleRootPath.toString()
            val sourceFileName = file.fileName
            val sourceFilePath = file.path.toString()
            val sourceExpressions = mapOf(Pair(RefactoringSubject.MODULE_NAME, sourceModuleName),
                    Pair(RefactoringSubject.MODULE_PATH, sourceModulePath),
                    Pair(RefactoringSubject.FILE_NAME, sourceFileName),
                    Pair(RefactoringSubject.FILE_PATH, sourceFilePath))
            val targetExpressions = emptyMap<RefactoringSubject, String>()
            moduleFilesTransformationMap.put(file, ExpandedStep(sourceExpressions, targetExpressions))
        }
        return moduleFilesTransformationMap
    }

    /**
     * Applies the given (collapsed) step to all (expanded) steps in the transformation map.
     *
     * @param step the collapsed step to expand
     * @param transformationMap the transformation map containing all expanded steps
     * @return an updated version of the given transformation map with the given step applied to all steps it matched.
     */
    private fun expandStepToTransformationMap(step: Step, transformationMap: Map<File, ExpandedStep>): Map<File, ExpandedStep> {
        validateStep(step)
        val resultTransformationMap = HashMap<File, ExpandedStep>()
        for (file in transformationMap.keys) {
            if (allMatch(step, transformationMap[file]!!)) {
                // transformationMap[file] cannot be null since we iterate over keys we got from the map
                resultTransformationMap.put(file, applyStepOnStep(step, transformationMap[file]!!))
            } else {
                // same here
                resultTransformationMap.put(file, transformationMap[file]!!)
            }
        }
        return resultTransformationMap
    }

    /**
     * Returns a flat version of a step in which source steps are replaced by existing target steps.
     * Since the target steps may not exist, the returned map describes the resulting file after the step has been applied.
     * If no target step is defined for a step type, the expression is returned as is (source).
     *
     * @param step the step to merge
     * @return a map of step types to expressions, target expression if defined in the given step, source expression otherwise.
     */
    private fun getMergedStepExpressions(step: ExpandedStep): Map<RefactoringSubject, String> {
        val mergedStepExpressions = HashMap<RefactoringSubject, String>()
        for (stepType in RefactoringSubject.values()) {
            if (step.targetExpression.containsKey(stepType)) {
                mergedStepExpressions.put(stepType, step.targetExpression[stepType]!!)
            } else {
                mergedStepExpressions.put(stepType, step.sourceExpressions[stepType]!!)
            }
        }
        return mergedStepExpressions
    }

    /**
     * Applies the given current step onto the given last step.
     *
     * @param currentStep the step to apply
     * @param lastStep the step to apply the given currentStep onto
     * @return lastStep with the currentStep applied onto it
     */
    private fun applyStepOnStep(currentStep: Step, lastStep: ExpandedStep): ExpandedStep {
        val mergedLastStepExpressions = getMergedStepExpressions(lastStep)
        val resultStepExpressions = HashMap<RefactoringSubject, String>()
        for (stepType in RefactoringSubject.values()) {
            if (currentStep.targetExpression.containsKey(stepType)) {
                val targetExpression = currentStep.targetExpression[stepType]!!
                val transformedExpression = currentStep.sourceExpressions[stepType]!!.replace(mergedLastStepExpressions[stepType]!!, targetExpression.toString())
                resultStepExpressions.put(stepType, transformedExpression)
            }
        }
        return ExpandedStep(lastStep.sourceExpressions, resultStepExpressions)
    }

    /**
     * Checks if all source expressions in the given currentStep match the given lastStep.
     *
     * @param currentStep the step whose source expressions should be checked
     * @param lastStep the subject of the expression check
     * @return true, if all source expressions in currentStep would match a file on which lastStep has been executed.
     */
    private fun allMatch(currentStep: Step, lastStep: ExpandedStep): Boolean {
        val mergedLastStepExpressions = getMergedStepExpressions(lastStep)
        for (stepType in RefactoringSubject.values()) {
            if (currentStep.sourceExpressions.containsKey(stepType)) {
                val regex = currentStep.sourceExpressions[stepType]!!
                if (!regex.matches(mergedLastStepExpressions[stepType]!!)) {
                    return false
                }
            }
        }
        return true
    }

    /**
     * Checks if a step is valid, throws an argument exception if the step could not be executed.
     *
     * @param step the step to check
     */
    private fun validateStep(step: Step) {
        // This just checks if every target expression also has a source expression.
        // Depending on the degree of automation it might be sensible to also check for other invalid steps,
        // e.g. file name change without entity change for java classes etc.
        for (stepType in RefactoringSubject.values()) {
            if (step.targetExpression.containsKey(stepType) && !step.sourceExpressions.containsKey(stepType)) {
                throw IllegalArgumentException()
            }
        }
    }
}
