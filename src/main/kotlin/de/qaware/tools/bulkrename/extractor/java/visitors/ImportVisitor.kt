package de.qaware.tools.bulkrename.extractor.java.visitors

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.expr.QualifiedNameExpr
import de.qaware.tools.bulkrename.extractor.java.JavaQualifiedTypeReference
import de.qaware.tools.bulkrename.extractor.java.ReferenceExtractionContext
import de.qaware.tools.bulkrename.extractor.java.ReferenceVisitor

/**
 * Visitor for import statements.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class ImportVisitor(context: ReferenceExtractionContext) : ReferenceVisitor(context) {

    override fun visit(n: ImportDeclaration?, arg: Unit) {
        if (n != null) {

            val importedClass =
                    if (n.isStatic) (n.name as QualifiedNameExpr).qualifier
                    else n.name

            val target = context.resolveFullName(importedClass.toString())
            if (target != null) {
                emit(JavaQualifiedTypeReference(context.getCurrentFile(), target, importedClass.toSpan()))
            }
        }
        super.visit(n, arg)
    }

}
