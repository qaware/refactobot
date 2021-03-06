package de.qaware.refactobot.extractor.java.visitors

import com.github.javaparser.ast.ImportDeclaration
//import com.github.javaparser.ast.expr.QualifiedNameExpr
import de.qaware.refactobot.extractor.java.ReferenceExtractionContext
import de.qaware.refactobot.extractor.java.UnitReferenceVisitor

/**
 * Visitor for import statements.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class ImportVisitor(context: ReferenceExtractionContext) : UnitReferenceVisitor(context) {

    override fun visit(n: ImportDeclaration?, arg: Unit) {
        if (n != null) {

            if (n.isAsterisk && !n.isStatic) {
                throw UnsupportedOperationException("Found non-static *-import, which is currently unsupported: " +
                        context.getCurrentFile().fullName)
            }

            val importedClass =
//                    if (n.isStatic && !n.isAsterisk) (n.name as QualifiedNameExpr).qualifier
//                    else
                        n.name

            emitReferenceForFullClassName(importedClass)
        }
        super.visit(n, arg)
    }

}
