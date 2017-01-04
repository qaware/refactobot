package de.qaware.tools.bulkrename.extractor.visitors

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.expr.QualifiedNameExpr
import de.qaware.tools.bulkrename.extractor.RawReference
import de.qaware.tools.bulkrename.extractor.ReferenceType
import de.qaware.tools.bulkrename.extractor.ReferenceVisitor

/**
 * Visitor for import statements.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class ImportVisitor : ReferenceVisitor() {

    override fun visit(n: ImportDeclaration?, arg: Unit) {
        if (n != null) {
            emit(RawReference(
                    ReferenceType.IMPORT,
                    n.name.toSpan(),
                    (n.name as? QualifiedNameExpr)?.qualifier?.toString() ?: throw UnsupportedOperationException("Imports must be qualified."),
                    n.name.name
            ))
        }
        super.visit(n, arg)
    }

}
