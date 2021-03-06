package de.qaware.refactobot.extractor.java.visitors

import com.github.javaparser.ast.comments.JavadocComment
import de.qaware.refactobot.extractor.general.FqcnExtractor
import de.qaware.refactobot.extractor.java.ReferenceExtractionContext
import de.qaware.refactobot.extractor.java.UnitReferenceVisitor

/**
 * Visitor for javadoc. Replaces fqcns.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class JavadocVisitor(context: ReferenceExtractionContext) : UnitReferenceVisitor(context) {

    override fun visit(comment: JavadocComment?, arg: Unit) {

        if (comment != null) {

            FqcnExtractor.findFqcnReferences(context.getCurrentFile(), context::resolveFullName,
                    comment.content, comment.range.get().begin.toLocation() + 3, this::emit)

        }

        super.visit(comment, arg)
    }

}
