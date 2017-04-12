package de.qaware.tools.bulkrename.extractor.java.visitors

import com.github.javaparser.ast.expr.MarkerAnnotationExpr
import com.github.javaparser.ast.expr.NormalAnnotationExpr
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr
import de.qaware.tools.bulkrename.extractor.java.ReferenceExtractionContext
import de.qaware.tools.bulkrename.extractor.java.ReferenceVisitor

/**
 * Visitor for annotation usage.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class AnnotationVisitor(context: ReferenceExtractionContext) : ReferenceVisitor(context) {

    override fun visit(n: SingleMemberAnnotationExpr?, arg: Unit?) {

        if (n != null) {
            visitName(n.name)
        }

        super.visit(n, arg)
    }

    override fun visit(n: NormalAnnotationExpr?, arg: Unit?) {

        if (n != null) {
            visitName(n.name)
        }

        super.visit(n, arg)
    }

    override fun visit(n: MarkerAnnotationExpr?, arg: Unit?) {

        if (n != null) {
            visitName(n.name)
        }

        super.visit(n, arg)
    }

}
