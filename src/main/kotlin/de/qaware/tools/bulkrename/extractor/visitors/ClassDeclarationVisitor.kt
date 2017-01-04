package de.qaware.tools.bulkrename.extractor.visitors

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import de.qaware.tools.bulkrename.extractor.RawReference
import de.qaware.tools.bulkrename.extractor.ReferenceExtractionContext
import de.qaware.tools.bulkrename.extractor.ReferenceType
import de.qaware.tools.bulkrename.extractor.ReferenceVisitor

/**
 * Visitor for class definitions.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class ClassDeclarationVisitor(context: ReferenceExtractionContext) : ReferenceVisitor(context) {

    override fun visit(decl: ClassOrInterfaceDeclaration?, arg: Unit) {
        if (decl != null) {
            emit(RawReference(
                    ReferenceType.CLASS_OR_INTERFACE_REFERENCE,
                    decl.nameExpr.toSpan(),
                    null,
                    decl.name
            ))
        }
        super.visit(decl, arg)
    }

}
