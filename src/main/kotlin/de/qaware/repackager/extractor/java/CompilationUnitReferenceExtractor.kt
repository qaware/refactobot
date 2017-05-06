package de.qaware.repackager.extractor.java

import com.github.javaparser.ast.CompilationUnit
import de.qaware.repackager.model.reference.Reference

/**
 * TODO describe type.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
interface CompilationUnitReferenceExtractor {
 fun extractReferences(file: CompilationUnit): Set<Reference>
}