package de.qaware.refactobot.extractor.java.visitors

import com.github.javaparser.ast.expr.StringLiteralExpr
import de.qaware.refactobot.extractor.general.FqcnExtractor
import de.qaware.refactobot.extractor.java.ReferenceExtractionContext
import de.qaware.refactobot.extractor.java.UnitReferenceVisitor

/**
 * Visitor for string literals. Tries to find class names.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class StringLiteralVisitor(context: ReferenceExtractionContext) : UnitReferenceVisitor(context) {

    override fun visit(literal: StringLiteralExpr?, arg: Unit) {

        if (literal != null) {

            FqcnExtractor.findFqcnReferences(context.getCurrentFile(), context::resolveFullName,
                    literal.value, literal.range.get().begin.toLocation() + 1, this::emit)

        }

        super.visit(literal, arg)
    }

}
