package de.qaware.repackager.classmap_import

import de.qaware.repackager.model.plan.SchematicRefactoringPlan

/**
 * TODO describe type.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
interface ClassMapImporter {

    fun importClassMap(filename: String): SchematicRefactoringPlan

}