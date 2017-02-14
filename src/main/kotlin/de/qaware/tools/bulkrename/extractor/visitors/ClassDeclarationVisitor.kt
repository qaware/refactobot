package de.qaware.tools.bulkrename.extractor.visitors

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import de.qaware.tools.bulkrename.extractor.JavaSimpleTypeReference
import de.qaware.tools.bulkrename.extractor.ReferenceExtractionContext
import de.qaware.tools.bulkrename.extractor.ReferenceVisitor

/**
 * Visitor for class definitions.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class ClassDeclarationVisitor(context: ReferenceExtractionContext) : ReferenceVisitor(context) {

    override fun visit(decl: ClassOrInterfaceDeclaration?, arg: Unit) {
        if (decl != null) {
            if (decl.parentNode is CompilationUnit) {
                // we are the top-level type
                emit(JavaSimpleTypeReference(context.getCurrentFile(), context.getCurrentFile(), decl.nameExpr.toSpan()))
            }
        }
        super.visit(decl, arg)
    }

}
