package de.qaware.refactobot.model.plan

import de.qaware.refactobot.model.plan.FileLocation

/**
 * A refactoring step, described in terms of a function that maps file locations.
 *
 * Since they are just functions, steps are trivially composable, and can be applied symbolically, to obtain the
 * final location of any given file, without actually performing the operation.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
typealias RefactoringMapping = (FileLocation) -> FileLocation
