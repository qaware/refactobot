package de.qaware.tools.bulkrename.expander

import de.qaware.tools.bulkrename.model.codebase.Codebase
import de.qaware.tools.bulkrename.model.codebase.File
import de.qaware.tools.bulkrename.model.plan.NewFileLocation
import de.qaware.tools.bulkrename.model.plan.RefactoringSubject
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
            transformationMap = expandStepToTransformationMap(step, transformationMap)
        }
        return transformationMap.mapValues { entry -> createNewFileLocation(entry.value.targetExpression) }
    }

    private fun createNewFileLocation(expansionResult: Map<RefactoringSubject, String>): NewFileLocation {

        val newFileName = expansionResult[RefactoringSubject.FILE_NAME]!!
        val newFilePath = expansionResult[RefactoringSubject.FILE_PATH]!!
        val newModuleName = expansionResult[RefactoringSubject.MODULE_NAME]
        val newModulePath = expansionResult[RefactoringSubject.MODULE_PATH]

        val newModule = codebase.modules.find { it.name == newModuleName }
                ?: throw IllegalStateException("Unknown module " + newModuleName)
        val newSourceFolder = newModule.sourceFolders.find { it.path == newModulePath }
                ?: throw IllegalStateException("No source folder with path " + newModulePath + " in module " + newModule.name)

        return NewFileLocation(newModule, newSourceFolder, Paths.get(newFilePath), newFileName)

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
            for (folder in module.sourceFolders) {
                for (file in folder.files) {

                    val expressions = mapOf(Pair(RefactoringSubject.MODULE_NAME, module.name),
                            Pair(RefactoringSubject.MODULE_PATH, folder.path),
                            Pair(RefactoringSubject.FILE_NAME, file.fileName),
                            Pair(RefactoringSubject.FILE_PATH, file.path.toString()))

                    transformationMap.put(file, ExpandedStep(expressions, expressions))
                }
            }
        }
        return transformationMap
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
        return transformationMap.mapValues { applyStepOnStep(step, it.value) }
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

        return step.sourceExpressions.plus(step.targetExpression)
    }

    /**
     * Applies the given current step onto the given last step (if it matches)
     *
     * @param currentStep the step to apply
     * @param lastStep the step to apply the given currentStep onto
     * @return lastStep with the currentStep applied onto it
     */
    private fun applyStepOnStep(currentStep: Step, lastStep: ExpandedStep): ExpandedStep {

        if (!allMatch(currentStep, lastStep)) {
            return lastStep
        }

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
        // Depending on the degree of automation it might be sensible to also check for other invalid steps
        for (stepType in RefactoringSubject.values()) {
            if (step.targetExpression.containsKey(stepType) && !step.sourceExpressions.containsKey(stepType)) {
                throw IllegalArgumentException()
            }
        }
    }
}
