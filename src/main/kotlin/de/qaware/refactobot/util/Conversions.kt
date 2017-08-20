package de.qaware.refactobot.util

import java.nio.file.Path
import java.nio.file.Paths

/**
 * Converts the file name and, to a fully-qualified class name. This merely converts slashes to dots and removes the
 * extension, which assumes the standard Java conventions on package layout.
 *
 * @param filename the filename
 * @return the class name.
 */
fun fileToClass(filename: String): String {
    if (!filename.endsWith(".java")) {
        throw IllegalArgumentException("Expecting file extension '.java', but got " + filename)
    }

    return filename.removeSuffix(".java").replace('/', '.')
}

/**
 * Converts a path to a java package name, basically converting path separators to dots.
 *
 * @param path the path
 * @return the package name.
 */
fun pathToPackage(path: String): String = path.replace('/', '.')

/**
 * Converts a path to a java package name, basically converting path separators to dots.
 *
 * @param path the path
 * @return the package name.
 */
fun pathToPackage(path: Path): String = pathToPackage(path.slashify())

/**
 * Converts the class name to a file name, assuming standard conventions.
 *
 * @param className the fully qualified class name.
 * @return the corresponding file name.
 */
fun classToFile(className: String) : String =
        className.replace('.', '/') + ".java"


/**
 * Splits a full path (with slashes) into a path and a filename component.
 *
 * @param fullPath the path
 * @return a pair of path and filename
 */
fun splitPath(fullPath: String): Pair<Path, String> {
    val pathAndFile = fullPath.split("/")
    val path = Paths.get(pathAndFile.dropLast(1).joinToString("/"))
    val fileName = pathAndFile.last()
    return Pair(path, fileName)
}

/**
 * Splits a string into lines, but keeps newlines.
 */
fun String.splitLines() = this.split(Regex("(?<=\\n)"))

/**
 * Converts a path to a slash-separated string, instead of the system dependent version, which may use backslashes.
 */
fun Path.slashify() = this.joinToString("/")