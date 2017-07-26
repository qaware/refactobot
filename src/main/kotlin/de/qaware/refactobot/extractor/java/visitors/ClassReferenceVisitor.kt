package de.qaware.refactobot.extractor.java.visitors

import com.github.javaparser.ast.expr.FieldAccessExpr
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.type.ClassOrInterfaceType
import de.qaware.refactobot.extractor.java.ReferenceExtractionContext
import de.qaware.refactobot.extractor.java.UnitReferenceVisitor
import de.qaware.refactobot.model.operation.Location
import de.qaware.refactobot.model.operation.Span

/**
 * Visitor for class references.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class ClassReferenceVisitor(context: ReferenceExtractionContext) : UnitReferenceVisitor(context) {

    override fun visit(n: ClassOrInterfaceType?, arg: Unit) {
        if (n != null) {

            fun fail(msg: String): Nothing =
                    throw UnsupportedOperationException("%s: %s (%d:%d-%d:%d) in file %s"
                            .format(msg, n.name, n.begin.line, n.begin.column, n.end.line, n.end.column, context.getCurrentFile()))

            val fullName = (if (n.scope != null) n.scope.toString() + "." else "") + n.name

            if (n.typeArguments.typeArguments.isNotEmpty() && n.begin.line != n.end.line) {
                fail("Multiline usage of generic types is not supported")
            }
            if (n.typeArguments.typeArguments.isNotEmpty() && n.typeArguments.typeArguments.first().begin.column - (n.begin.column + fullName.length) != 1) {
                fail("Spaces in generic type references are not supported")
            }

            // extract span
            val endLocation = when {
                n.typeArguments.typeArguments.isNotEmpty() ->
                    Location.oneBased(n.typeArguments.typeArguments.first().begin.line, n.typeArguments.typeArguments.first().begin.column - 1)
                n.isUsingDiamondOperator ->
                    Location.oneBased(n.end.line, n.end.column - 1)
                else -> Location.oneBased(n.end.line, n.end.column + 1)
            }

            val span = Span(n.begin.toLocation(), endLocation)

            if (n.scope != null) {
                emitQualifiedReference(fullName, span)

            } else {
                emitSimpleReference(fullName, span)
            }
        }
        super.visit(n, arg)
    }

    override fun visit(n: FieldAccessExpr, arg: Unit) {

        val scope = n.scope
        when (scope) {
            is NameExpr -> {
                emitSimpleReference(scope.name, scope.toSpan())
            }
            else -> {
                // scope is not a simple name, so it could be a qualified class name -> look it up
                emitQualifiedReference(scope.toString(), scope.toSpan())
            }
        }

        super.visit(n, arg)
    }


}
