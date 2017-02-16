package de.qaware.tools.bulkrename.extractor.imports

/**
 * Abstract model of various types of import declarations.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
sealed class ImportClause {

    /**
     * Denotes an import of a particular class from a given package, i.e.,
     *
     * import a.b.C;               // imports C
     */
    class SimpleImport(val packageName: String, val className: String) : ImportClause() {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other?.javaClass != javaClass) return false

            other as SimpleImport

            if (packageName != other.packageName) return false
            if (className != other.className) return false

            return true
        }

        override fun hashCode(): Int {
            var result = packageName.hashCode()
            result = 31 * result + className.hashCode()
            return result
        }
    }

    /**
     * Denotes an import of members of a given class, i.e.,
     *
     * import a.b.C;               // imports C
     * import a.b.C.D;             // imports a static member class of C
     * import static a.b.C.m;      // imports a static method from C
     * import static a.b.C.*;      // imports all members of C statically
     *
     * Here, the package is always "a.b", the class is always "C", and the suffix denotes the material from the class
     * that is actually imported.
     *
     * This object contains enough information to recreate the import statement.
     */
    class SingleClassImport(val packageName: String, val className: String, val suffix: String, static: Boolean) : ImportClause() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other?.javaClass != javaClass) return false

            other as SingleClassImport

            if (packageName != other.packageName) return false
            if (className != other.className) return false
            if (suffix != other.suffix) return false

            return true
        }

        override fun hashCode(): Int {
            var result = packageName.hashCode()
            result = 31 * result + className.hashCode()
            result = 31 * result + suffix.hashCode()
            return result
        }
    }

    /**
     * Denotes the import of all classes from a given package, written like this:
     *
     * import a.b.*;
     *
     * This brings all classes from the package into the namespace. We must be aware of which classes these are, since
     * more explicit imports might be needed when classes move around.
     */
    class PackageImport(val packageName: String) : ImportClause() {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other?.javaClass != javaClass) return false

            other as PackageImport

            if (packageName != other.packageName) return false

            return true
        }

        override fun hashCode(): Int {
            return packageName.hashCode()
        }
    }

}