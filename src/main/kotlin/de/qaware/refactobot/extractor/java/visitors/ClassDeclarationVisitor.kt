package de.qaware.refactobot.extractor.java.visitors

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.EnumDeclaration
import com.github.javaparser.ast.body.TypeDeclaration
import de.qaware.refactobot.extractor.java.JavaSimpleTypeReference
import de.qaware.refactobot.extractor.java.ReferenceExtractionContext
import de.qaware.refactobot.extractor.java.UnitReferenceVisitor

/**
 * Visitor for class, interface and enum definitions.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class ClassDeclarationVisitor(context: ReferenceExtractionContext) : UnitReferenceVisitor(context) {

    override fun visit(decl: ClassOrInterfaceDeclaration?, arg: Unit) {
        if (decl != null) {
            visitTypeDeclaration(decl)
        }
        super.visit(decl, arg)
    }

    override fun visit(decl: EnumDeclaration?, arg: Unit) {
        if (decl != null) {
            visitTypeDeclaration(decl)
        }
        super.visit(decl, arg)
    }

    private fun visitTypeDeclaration(decl: TypeDeclaration<*>) {
        if (decl.parentNode.get() is CompilationUnit) {
            // we are the top-level type
            emit(JavaSimpleTypeReference(context.getCurrentFile(), context.getCurrentFile(), decl.name.toSpan()))
        }
    }
}
