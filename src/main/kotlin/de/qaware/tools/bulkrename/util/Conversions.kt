package de.qaware.tools.bulkrename.util

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
 * Converts the class name to a file name, assuming standard conventions.
 *
 * @param className the fully qualified class name.
 * @return the corresponding file name.
 */
fun classToFile(className: String) : String =
        className.replace('.', '/') + ".java"