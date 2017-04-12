package de.qaware.tools.bulkrename.extractor.java.visitors

import com.github.javaparser.ast.expr.MethodCallExpr
import de.qaware.tools.bulkrename.extractor.java.ReferenceExtractionContext
import de.qaware.tools.bulkrename.extractor.java.ReferenceVisitor

/**
 * Visitor for annotation usage.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class MethodCallVisitor(context: ReferenceExtractionContext) : ReferenceVisitor(context) {

    override fun visit(n: MethodCallExpr?, arg: Unit?) {

        if (n != null && n.scope != null) {
            visitName(n.scope)
        }

        super.visit(n, arg)
    }

}
