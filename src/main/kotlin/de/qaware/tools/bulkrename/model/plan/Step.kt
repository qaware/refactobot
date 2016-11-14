package de.qaware.tools.bulkrename.model.plan

/**
 * A step in a refactoring plan.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 * @author Florian Engel florian.engel@qaware.de
 */
data class Step (

        val oldModuleName: String,
        val oldModulePath: String,
        val oldFileName: String,
        val oldFilePath: String,
        val oldEntity : String,

        val newModuleName: String,
        val newModulePath: String,
        val newFileName: String,
        val newFilePath: String,
        val newEntity : String

)