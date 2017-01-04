package de.qaware.tools.bulkrename.extractor

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import de.qaware.tools.bulkrename.model.operation.Location
import de.qaware.tools.bulkrename.model.operation.Span

/**
 * Abstract visitor implementation that can be used to extract references from a compilation unit.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
abstract class ReferenceVisitor : VoidVisitorAdapter<Unit>() {

    val collectedReferences: MutableSet<RawReference> = hashSetOf()

    protected fun emit(ref: RawReference) {
        collectedReferences += ref
    }

    fun extractReferences(file: CompilationUnit): Set<RawReference> {
        visit(file, Unit)
        return collectedReferences
    }

    /**
     * Creates a span for a given node.
     */
    protected fun Node.toSpan() =
            Span(Location.oneBased(this.begin.line, this.begin.column), Location.oneBased(this.end.line, this.end.column + 1))


}
