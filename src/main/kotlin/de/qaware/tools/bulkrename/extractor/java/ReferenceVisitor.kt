package de.qaware.tools.bulkrename.extractor.java

import com.github.javaparser.Position
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import de.qaware.tools.bulkrename.model.operation.Location
import de.qaware.tools.bulkrename.model.operation.Span
import de.qaware.tools.bulkrename.model.reference.Reference

/**
 * Abstract visitor implementation that can be used to extract references from a compilation unit.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
abstract class ReferenceVisitor(val context: ReferenceExtractionContext) : VoidVisitorAdapter<Unit>() {

    val collectedReferences: MutableSet<Reference> = hashSetOf()

    protected fun emit(ref: Reference) {
        collectedReferences += ref
    }

    fun extractReferences(file: CompilationUnit): Set<Reference> {
        visit(file, Unit)
        return collectedReferences
    }

    protected fun Position.toLocation() = Location(this.line - 1, this.column - 1)

    /**
     * Creates a span for a given node.
     */
    protected fun Node.toSpan() =
            Span(Location.Companion.oneBased(this.begin.line, this.begin.column), Location.Companion.oneBased(this.end.line, this.end.column + 1))


}
