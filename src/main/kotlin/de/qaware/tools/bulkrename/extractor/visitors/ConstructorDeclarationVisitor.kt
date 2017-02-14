package de.qaware.tools.bulkrename.extractor.visitors

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ConstructorDeclaration
import de.qaware.tools.bulkrename.extractor.JavaSimpleTypeReference
import de.qaware.tools.bulkrename.extractor.ReferenceExtractionContext
import de.qaware.tools.bulkrename.extractor.ReferenceVisitor

/**
 * Visitor for constructor declarations. Names of constructors must be adapted when the class is renamed.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class ConstructorDeclarationVisitor(context: ReferenceExtractionContext) : ReferenceVisitor(context) {

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
