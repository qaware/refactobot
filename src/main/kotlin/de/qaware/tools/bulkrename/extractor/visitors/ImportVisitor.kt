package de.qaware.tools.bulkrename.extractor.visitors

import com.github.javaparser.ast.ImportDeclaration
import de.qaware.tools.bulkrename.extractor.JavaQualifiedTypeReference
import de.qaware.tools.bulkrename.extractor.ReferenceExtractionContext
import de.qaware.tools.bulkrename.extractor.ReferenceVisitor

/**
 * Visitor for import statements.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class ImportVisitor(context: ReferenceExtractionContext) : ReferenceVisitor(context) {

    override fun visit(n: ImportDeclaration?, arg: Unit) {
        if (n != null) {

            val target = context.resolveFullName(n.name.toString())
            if (target != null) {
                emit(JavaQualifiedTypeReference(context.getCurrentFile(), target, n.name.toSpan()))
            }
        }
        super.visit(n, arg)
    }

}
