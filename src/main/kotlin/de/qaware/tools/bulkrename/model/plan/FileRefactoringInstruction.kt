package de.qaware.tools.bulkrename.model.plan

/**
 * Data class for target names/locations with regard to a single file.
 *
 * @author Florian Engel florian.engel@qaware.de
 */
data class FileRefactoringInstruction(

        val targets: Map<RefactoringSubject, String>
)

