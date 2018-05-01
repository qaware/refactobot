package de.qaware.refactobot.extractor.java.visitors

import com.github.javaparser.ast.expr.FieldAccessExpr
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.type.Type
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

            val begin = n.begin.get()
            val end = n.end.get()
            val typeArguments : List<Type> =
                    if (n.typeArguments.isPresent) n.typeArguments.get()
                    else emptyList()

            fun fail(msg: String): Nothing =
                    throw UnsupportedOperationException("%s: %s (%d:%d-%d:%d) in file %s"
                            .format(msg, n.name, begin.line, begin.column, end.line, end.column, context.getCurrentFile()))

            val fullName = (if (n.scope.isPresent()) n.scope.get().toString() + "." else "") + n.name

            if (typeArguments.isNotEmpty() && begin.line != end.line) {
                fail("Multiline usage of generic types is not supported")
            }
            if (typeArguments.isNotEmpty()
                    && typeArguments.first().begin.get().column - (begin.column + fullName.length) != 1) {
                fail("Spaces in generic type references are not supported")
            }

            // extract span
            val endLocation = when {
                typeArguments.isNotEmpty() ->
                    Location.oneBased(typeArguments.first().begin.get().line, typeArguments.first().begin.get().column - 1)
                n.isUsingDiamondOperator ->
                    Location.oneBased(end.line, end.column - 1)
                else -> Location.oneBased(end.line, end.column + 1)
            }

            val span = Span(begin.toLocation(), endLocation)

            if (n.scope.isPresent) {
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
                emitSimpleReference(scope.toString(), scope.toSpan())
            }
            else -> {
                // scope is not a simple name, so it could be a qualified class name -> look it up
                emitQualifiedReference(scope.toString(), scope.toSpan())
            }
        }

        super.visit(n, arg)
    }


}
