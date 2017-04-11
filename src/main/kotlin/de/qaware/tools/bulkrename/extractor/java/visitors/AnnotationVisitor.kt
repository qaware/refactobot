package de.qaware.tools.bulkrename.extractor.java.visitors

import com.github.javaparser.ast.expr.*
import de.qaware.tools.bulkrename.extractor.java.JavaQualifiedTypeReference
import de.qaware.tools.bulkrename.extractor.java.JavaSimpleTypeReference
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
            visitAnnotation(n.name)
        }

        super.visit(n, arg)
    }

    override fun visit(n: NormalAnnotationExpr?, arg: Unit?) {

        if (n != null) {
            visitAnnotation(n.name)
        }

        super.visit(n, arg)
    }

    override fun visit(n: MarkerAnnotationExpr?, arg: Unit?) {

        if (n != null) {
            visitAnnotation(n.name)
        }

        super.visit(n, arg)
    }

    private fun visitAnnotation(n: NameExpr) {

        when (n) {

            is QualifiedNameExpr -> {
                val fullName = n.toStringWithoutComments()
                val target = context.resolveFullName(fullName)
                if (target != null) {
                    emit(JavaQualifiedTypeReference(context.getCurrentFile(), target, n.toSpan()))
                }
            }
            else -> {

                val target = context.resolveSimpleName(n.name)
                if (target != null) {
                    emit(JavaSimpleTypeReference(context.getCurrentFile(), target, n.toSpan()))
                }
            }
        }
    }
}
