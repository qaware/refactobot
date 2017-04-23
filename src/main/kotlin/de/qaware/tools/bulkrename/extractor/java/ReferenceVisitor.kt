package de.qaware.tools.bulkrename.extractor.java

import com.github.javaparser.Position
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.expr.FieldAccessExpr
import com.github.javaparser.ast.expr.QualifiedNameExpr
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

    protected fun visitName(n: Expression) {

        when (n) {

            is QualifiedNameExpr, is FieldAccessExpr -> {
                val fullName = n.toStringWithoutComments()
                val target = context.resolveFullName(fullName)
                if (target != null) {
                    emit(JavaQualifiedTypeReference(context.getCurrentFile(), target, n.toSpan()))
                }
            }
            else -> {

                val target = context.resolveSimpleName(n.toStringWithoutComments())
                if (target != null) {
                    emit(JavaSimpleTypeReference(context.getCurrentFile(), target, n.toSpan()))
                }
            }
        }
    }

    protected fun emitQualifiedReference(name: String, span: Span) {
        val target = context.resolveFullName(name)
        if (target != null) {
            emit(JavaQualifiedTypeReference(context.getCurrentFile(), target, span))
        }

        if (name.endsWith("_")) {
            val target2 = context.resolveFullName(name.dropLast(1))
            if (target2 != null) {
                emit(JavaQualifiedTypeReference(context.getCurrentFile(), target2, span.shortenBy(1)))
            }
        }
    }

    protected fun emitSimpleReference(name: String, span: Span) {
        val target = context.resolveSimpleName(name)
        if (target != null) {
            emit(JavaSimpleTypeReference(context.getCurrentFile(), target, span))
        }

        if (name.endsWith("_")) {
            val target2 = context.resolveSimpleName(name.dropLast(1))
            if (target2 != null) {
                emit(JavaSimpleTypeReference(context.getCurrentFile(), target2, span.shortenBy(1)))
            }
        }
    }

}
