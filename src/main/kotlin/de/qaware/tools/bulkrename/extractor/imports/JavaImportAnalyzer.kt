package de.qaware.tools.bulkrename.extractor.imports

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.ImportDeclaration
import de.qaware.tools.bulkrename.model.codebase.File
import de.qaware.tools.bulkrename.util.fileToClass

/**
 * Functions to analyze imports of a java compilation unit.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class JavaImportAnalyzer {


    /**
     * Creates an import clause object, by analyzing the import.
     *
     * @param name the imported object, e.g., "org.example.Class" or "org.example.*"
     */
    fun getImport(name: String, static: Boolean): ImportClause {

        val nameParts = name.split(".")

        // by convention we assume that packages start with lower case letters
        fun startsWithLowerCase(s: String) = s[0].isLowerCase()
        val packageName = nameParts.takeWhile(::startsWithLowerCase).joinToString(".")
        val nonPackageParts = nameParts.dropWhile(::startsWithLowerCase)

        if (nonPackageParts == listOf("*")) {

            // a full package import
            return ImportClause.PackageImport(packageName)

        } else if (nonPackageParts.size == 1) {

            // a simple class import
            return ImportClause.SimpleImport(packageName, nonPackageParts.first())

        } else {

            val className = nonPackageParts.first()
            val suffixParts = nonPackageParts.subList(1, nonPackageParts.size)

            return ImportClause.SingleClassImport(packageName, className, suffixParts.joinToString("."), static)
        }
    }


    fun getImportedClasses(importClause : ImportClause, filesByClass: Map<String, File>, filesByPackage: Map<String, Set<File>>) : Map<String, File> {

        when (importClause) {
            is ImportClause.SimpleImport -> {
                val fqcn = importClause.packageName + "." + importClause.className
                val file : File? = filesByClass[fqcn]
                if (file == null) {
                    return emptyMap()
                } else {
                    return mapOf(Pair(importClause.className, file))
                }
            }
            is ImportClause.PackageImport -> {
                return (filesByPackage[importClause.packageName] ?: emptySet<File>())
                        .map { file -> Pair(fileToClass(file.fileName), file) }
                        .toMap()
            }
            is ImportClause.SingleClassImport -> {
                // the class name itself does not contribute to the namespace, and we do not have to keep track of
                // names of inner material.
                return emptyMap()
            }
        }
    }

    fun getNamespace(cu: CompilationUnit, filesByClass: Map<String, File>, filesByPackage: Map<String, Set<File>>) : Map<String, File> {

        // funny special casing of asterisk in javaparser model.
        fun importedName(importDecl: ImportDeclaration) =
                if (importDecl.isAsterisk) importDecl.name.toString() + ".*" else importDecl.name.toString()

        return cu.imports.map { importDecl -> getImport(importedName(importDecl), importDecl.isStatic) }
                .map { importClause -> getImportedClasses(importClause, filesByClass, filesByPackage) }
                .reduce { l, r -> l + r }
    }


}