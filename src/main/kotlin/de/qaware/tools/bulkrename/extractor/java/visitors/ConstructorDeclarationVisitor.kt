package de.qaware.tools.bulkrename.extractor.java.visitors

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ConstructorDeclaration
import de.qaware.tools.bulkrename.extractor.java.JavaSimpleTypeReference
import de.qaware.tools.bulkrename.extractor.java.ReferenceExtractionContext
import de.qaware.tools.bulkrename.extractor.java.UnitReferenceVisitor

/**
 * Visitor for constructor declarations. Names of constructors must be adapted when the class is renamed.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class ConstructorDeclarationVisitor(context: ReferenceExtractionContext) : UnitReferenceVisitor(context) {

    override fun visit(decl: ConstructorDeclaration?, arg: Unit) {
        if (decl != null) {
            if (decl.parentNode.parentNode is CompilationUnit) {
                // this is a constructor of the top-level type
                emit(JavaSimpleTypeReference(context.getCurrentFile(), context.getCurrentFile(), decl.nameExpr.toSpan()))
            }
        }
        super.visit(decl, arg)
    }

}
