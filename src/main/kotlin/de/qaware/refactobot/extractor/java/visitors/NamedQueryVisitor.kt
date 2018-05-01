package de.qaware.refactobot.extractor.java.visitors

import com.github.javaparser.ast.expr.NormalAnnotationExpr
import com.github.javaparser.ast.expr.StringLiteralExpr
import de.qaware.refactobot.extractor.general.ClassNameExtractor
import de.qaware.refactobot.extractor.java.ReferenceExtractionContext
import de.qaware.refactobot.extractor.java.ReferenceVisitor

/**
 * Visitor for JPA named queries. Finds unqualified class names in query strings.
 *
 * Heuristically, we cover all annotations with the name "NamedQuery", without checking for the namespace.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class NamedQueryVisitor(context: ReferenceExtractionContext) : ReferenceVisitor<Boolean>(context) {

    /**
     * The boolean context determines whether string literals are currently processed. It is true only when we are
     * visiting below a @NamedQuery annotation.
     */
    override fun initialContext(): Boolean = false

    override fun visit(annotation: NormalAnnotationExpr?, arg: Boolean) {

        if (annotation != null && annotation.name.identifier == "NamedQuery") {

            for (pair in annotation.pairs) {

                if (pair.name.identifier == "query") {
                    pair.value.accept(this, true)
                }
            }
        }

        // no need to visit further.
    }

    override fun visit (literal: StringLiteralExpr?, arg: Boolean) {

        // only if we are active
        if (arg && literal != null) {

            ClassNameExtractor.findSimpleClassReference(context.getCurrentFile(), context::resolveUniqueSimpleName,
                    literal.value, literal.range.get().begin.toLocation() + 1, this::emit)

        }
    }

}
