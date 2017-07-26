package de.qaware.refactobot.extractor.java

import com.github.javaparser.Position
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.expr.*
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import de.qaware.refactobot.model.operation.Location
import de.qaware.refactobot.model.operation.Span
import de.qaware.refactobot.model.reference.Reference

/**
 * Abstract visitor implementation that can be used to extract references from a compilation unit.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 *
 * @param <A> a context parameter that can be used to pass values down the structure. Can be set to Unit if not used
 */
abstract class ReferenceVisitor<A>(val context: ReferenceExtractionContext) : VoidVisitorAdapter<A>(), CompilationUnitReferenceExtractor {

    val collectedReferences: MutableSet<Reference> = hashSetOf()

    protected fun emit(ref: Reference) {
        collectedReferences += ref
    }

    /**
     * Returns the initial context.
     */
    protected abstract fun initialContext() : A

    override fun extractReferences(file: CompilationUnit): Set<Reference> {
        visit(file, initialContext())
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
                emitQualifiedReference(n.toStringWithoutComments(), n.toSpan())
            }
            is ThisExpr -> {
                if (n.classExpr != null) {
                    // qualified this, might contain a name.
                    visitName(n.classExpr)
                }
            }
            else -> {
                emitSimpleReference(n.toStringWithoutComments(), n.toSpan())
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
        val target = context.resolveImportedName(name)
        if (target != null) {
            emit(JavaSimpleTypeReference(context.getCurrentFile(), target, span))
        }

        if (name.endsWith("_")) {
            val target2 = context.resolveImportedName(name.dropLast(1))
            if (target2 != null) {
                emit(JavaSimpleTypeReference(context.getCurrentFile(), target2, span.shortenBy(1)))
            }
        }
    }

    /**
     * Emits a qualified name reference for the given name, or any matching prefixes.
     *
     * For example, a.b.C.D.E could refer to classes a.b.C or a.b.C.D or a.b.C.D.E.
     */
    protected fun emitReferenceForFullClassName(name: NameExpr) {
        val target = context.resolveFullName(name.toString())
        if (target != null) {
            emit(JavaQualifiedTypeReference(context.getCurrentFile(), target, name.toSpan()))
        } else if (name.toString().endsWith("_")) {
            val target2 = context.resolveFullName(name.toString().dropLast(1))
            if (target2 != null) {
                emit(JavaQualifiedTypeReference(context.getCurrentFile(), target2, name.toSpan().shortenBy(1)))
            }
        } else if (name is QualifiedNameExpr) {
            emitReferenceForFullClassName(name.qualifier)
        }
    }

}
