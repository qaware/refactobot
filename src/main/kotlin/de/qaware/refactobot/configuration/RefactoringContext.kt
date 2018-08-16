package de.qaware.refactobot.configuration

import de.qaware.refactobot.model.plan.FileLocation
import de.qaware.refactobot.util.classToFile
import de.qaware.refactobot.util.fileToClass

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


    /**
     * DSL element for simple file rename
     */
    fun renameFile(regex: String, newName: String) {
        fileName = fileName.replace(regex.toRegex(), newName)
    }

    /**
     * DSL element for applying a class map
     */
    fun applyClassMap(classMap: Map<String, String>) {

        val mappedClass = classMap.get(fileToClass(fileName))
        if (mappedClass != null) {
            fileName = classToFile(mappedClass)
        }
    }





}