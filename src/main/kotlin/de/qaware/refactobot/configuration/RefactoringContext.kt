package de.qaware.refactobot.configuration

import de.qaware.refactobot.model.plan.FileLocation
import de.qaware.refactobot.model.plan.RefactoringStep

/**
 * Context for specifying a refactoring operation.
 *
 * Contains mutable fields that represent a [FileLocation]. The code may modify these fields to whatever new values
 * are desired, to change name and location of the given file.
 */
class RefactoringContext(originalLocation: FileLocation) {

    var module = originalLocation.module
    var sourceFolder = originalLocation.sourceFolder
    var path = originalLocation.path
    var fileName = originalLocation.fileName

    val newLocation: FileLocation
        get() = FileLocation(module, sourceFolder, path, fileName)

    companion object {
        fun build(stepFn : RefactoringContext.() -> Unit): RefactoringStep = { location ->
            RefactoringContext(location).apply(stepFn).newLocation
        }
    }

}