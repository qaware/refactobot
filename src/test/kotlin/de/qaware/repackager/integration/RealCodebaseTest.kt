package de.qaware.repackager.integration

import de.qaware.repackager.classmap_import.ClassMapImporterImpl
import de.qaware.repackager.model.plan.RefactoringSubject
import de.qaware.repackager.model.plan.SchematicRefactoringPlan
import de.qaware.repackager.model.plan.Step
import java.nio.file.Paths


/**
 * Renames a few entities.
 */
val ENTITY_RENAME = SchematicRefactoringPlan(listOf(
        renameStep(Regex("PSAProject\\.java"), "ProjectEntity.java"),
        renameStep(Regex("PSASystemrelease\\.java"), "SystemreleaseEntity.java"),
        renameStep(Regex("PSADomain\\.java"), "DomainEntity.java"),
        renameStep(Regex("PSAQuestionnaire\\.java"), "QuestionnaireEntity.java")
))

/**
 * Renames all DaoBeans to DaoImpl
 */
val DAO_RENAME = SchematicRefactoringPlan(listOf(
        renameStep(Regex("(.*)DaoBean\\.java"), "$1DaoImpl.java")
))

/**
 * Moves some DTOs to the respective business component.
 */
val DTO_MOVE = SchematicRefactoringPlan(listOf(
        moveStep("Report(List)?DTO\\.java", "application/modules/psa_reports.api", "com/telekom/gis/psa/reports/api/dto")
))


/**
 * Imports a class map from structure101
 */
val CLASS_MAP_RENAME = ClassMapImporterImpl().importClassMap("P:/temp/classmap.xml")


/**
 * Main function to run the tool on a real codebase.
 */
fun main(args: Array<String>) {

    BulkRenameTool.refactorMavenCodebase(Paths.get("P:/codebase2/code"), CLASS_MAP_RENAME)
}


private fun renameStep(from: Regex, to: String) =
        Step(mapOf(Pair(RefactoringSubject.FILE_NAME, Step.Replacement(from, to))))


private fun moveStep(filenameRegex: String, newModule: String, newPath: String) =
        Step(mapOf(
                Pair(RefactoringSubject.FILE_NAME, Step.Replacement(Regex("^($filenameRegex)$"), "$1")),
                Pair(RefactoringSubject.MODULE, Step.Replacement(Regex("^.*$"), newModule)),
                Pair(RefactoringSubject.FILE_PATH, Step.Replacement(Regex("^.*$"), newPath))
        ))