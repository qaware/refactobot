package de.qaware.refactobot.extractor.java.visitors

import com.github.javaparser.ast.expr.MethodCallExpr
import de.qaware.refactobot.extractor.java.ReferenceExtractionContext
import de.qaware.refactobot.extractor.java.UnitReferenceVisitor

/**
 * Visitor for method calls.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class MethodCallVisitor(context: ReferenceExtractionContext) : UnitReferenceVisitor(context) {

    override fun visit(n: MethodCallExpr?, arg: Unit?) {

        if (n != null && n.scope.isPresent) {
            visitName(n.scope.get())
        }

        super.visit(n, arg)
    }

}
