package de.qaware.refactobot.classmap_import

import de.qaware.refactobot.model.plan.old.SchematicRefactoringPlan

/**
 * TODO describe type.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
interface ClassMapImporter {

    fun importClassMap(filename: String): SchematicRefactoringPlan

}