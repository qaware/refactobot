package de.qaware.refactobot.extractor.java.visitors

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ConstructorDeclaration
import de.qaware.refactobot.extractor.java.JavaSimpleTypeReference
import de.qaware.refactobot.extractor.java.ReferenceExtractionContext
import de.qaware.refactobot.extractor.java.UnitReferenceVisitor

/**
 * Visitor for constructor declarations. Names of constructors must be adapted when the class is renamed.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class ConstructorDeclarationVisitor(context: ReferenceExtractionContext) : UnitReferenceVisitor(context) {

    override fun visit(decl: ConstructorDeclaration?, arg: Unit) {
        if (decl != null) {
            if (decl.parentNode.get().parentNode.get() is CompilationUnit) {
                // this is a constructor of the top-level type
                emit(JavaSimpleTypeReference(context.getCurrentFile(), context.getCurrentFile(), decl.name.toSpan()))
            }
        }
        super.visit(decl, arg)
    }

}
