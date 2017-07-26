package de.qaware.refactobot.extractor.java

import com.github.javaparser.ast.CompilationUnit
import de.qaware.refactobot.model.reference.Reference

/**
 * TODO describe type.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
interface CompilationUnitReferenceExtractor {
 fun extractReferences(file: CompilationUnit): Set<Reference>
}