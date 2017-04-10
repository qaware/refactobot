package de.qaware.tools.bulkrename.classmap_import

import de.qaware.tools.bulkrename.model.plan.SchematicRefactoringPlan

/**
 * TODO describe type.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
interface ClassMapImporter {

    fun importClassMap(filename: String): SchematicRefactoringPlan

}